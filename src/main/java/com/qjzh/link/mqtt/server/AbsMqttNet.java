package com.qjzh.link.mqtt.server;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import com.qjzh.link.mqtt.base.IMqttNet;
import com.qjzh.link.mqtt.server.MqttInitParams.Will;
import com.qjzh.link.mqtt.server.channel.ConnectState;
import com.qjzh.link.mqtt.utils.MqttTrustManager;

/**
 * @DESC: MQTT 客户端连接
 * @author LIU.ZHENXING
 * @date 2020年8月17日上午11:31:48
 * @version 1.0.0
 * @copyright www.7g.com
 */
public abstract class AbsMqttNet implements IMqttNet, MqttCallbackExtended {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	//恢复连接间隔
	private final int recoveryInterval = 10000;
	//等待动作完成最大时间
	private int timeToWait = 5000;
	// 保存形式，默认为以内存保存
	private MqttClientPersistence persistence;	
	// mqtt 客户端
	protected volatile IMqttAsyncClient mqttAsyncClient;
	// mqtt连接配置
	private MqttConnectOptions connectOptions;
	// ssl证书资源
	private InputStream mqttRootCrtFile;
	//client ID
	private String clientId;
	// mqtt初始化连接配置
	private MqttInitParams mqttInitParams;
	//定时任务
	private TaskScheduler taskScheduler;
	// 连接状态
	private volatile ConnectState connectState = ConnectState.DISCONNECTED;
	
	private volatile ScheduledFuture<?> reconnectFuture;

	protected final Lock connLock = new ReentrantLock();

	public AbsMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		this.clientId = clientId;
		this.mqttInitParams = initParams;
		this.taskScheduler = taskScheduler;
		this.persistence = new MemoryPersistence();
		this.timeToWait = mqttInitParams.getTimeToWait();
	}
	
	protected MqttInitParams getInitParams() {
		return this.mqttInitParams;
	}

	public IMqttAsyncClient getClient() {
		return mqttAsyncClient;
	}

	public ConnectState getConnectState() {
		return this.connectState;
	}
	
	public int getTimeToWait() {
		return timeToWait;
	}
	
	@PostConstruct
	public void connect() throws MqttException {
		if (connectState == ConnectState.CONNECTED) {
			return;
		}
		
		logger.debug("connecting.....");
		if (connectOptions == null) {
			initConnectOptions();
		}
		String timestamp = System.currentTimeMillis() + "";

		String mqttClientId = "clientId=" + this.clientId + ",timestamp=" + timestamp + "|";
		this.connLock.lock();
		try {
			this.mqttAsyncClient = new MqttAsyncClient("tcp://NO_URL_PROVIDED", mqttClientId, this.persistence);
		} catch (MqttException ex) {
			logger.error("create mqtt client error", ex);
			this.connectState = ConnectState.CONNECTFAIL;
			throw ex;
		}
		this.mqttAsyncClient.setCallback(this);
		try {
			this.connectState = ConnectState.CONNECTING;
			this.mqttAsyncClient.connect(this.connectOptions, null, null).waitForCompletion(timeToWait);
			logger.debug("mqtt client connect url:{}", String.join(",", mqttInitParams.getServerURIs()));
			
		} catch (MqttException ex) {
			this.connectState = ConnectState.CONNECTFAIL;
			throw ex;
		}finally {
			this.connLock.unlock();
		}
		
		if (mqttAsyncClient.isConnected()) {
			logger.info("connect success.....");
			connectState = ConnectState.CONNECTED;
		}
		
	}

	public void disconnect() throws MqttException {
		logger.info("disconnect....");
		if (this.mqttAsyncClient == null) {
			logger.info("client is null");
			return;
		}
		try {
			this.mqttAsyncClient.disconnectForcibly(mqttInitParams.getTimeToWait());
			this.mqttAsyncClient.setCallback(null);
			this.mqttAsyncClient.close();
			this.mqttAsyncClient = null;
			this.connectState = ConnectState.DISCONNECTED;
			this.persistence.close();
			this.mqttRootCrtFile.close();

		} catch (MqttException mex) {
			logger.error("disconnect error! ", mex);
			throw mex;
		} catch (Exception ex) {
			logger.error("disconnect error! ", ex);
			throw new MqttException(ex);
		}

	}
	
	@PreDestroy
	public void destroy() {
		logger.debug("destroy");
		try {
			disconnect();
		} catch (Exception ex) {
			logger.error("destroy error! ", ex);
		}
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		logger.info("connection lost.....");
		this.connectState = ConnectState.DISCONNECTED;
		if (this.mqttAsyncClient != null) {
			try {
				this.mqttAsyncClient.setCallback(null);
				this.mqttAsyncClient.close();
			}
			catch (MqttException ex) {
				logger.error("connectionLost error! ", ex);
			}
		}
		this.mqttAsyncClient = null;
		//定时重试连接
		scheduleReconnect();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		if (mqttAsyncClient.isConnected()) {
			connectSuccess();
		}
	}
	
	public abstract void connectSuccess();
	
	private void scheduleReconnect(){
		reconnectFuture = taskScheduler.scheduleAtFixedRate(() -> {
			try {
				logger.debug("attempting reconnect");
				synchronized (this) {
					if (!(this.connectState == ConnectState.CONNECTED)) {
						connect();
					}
					reconnectFuture.cancel(false);
					reconnectFuture = null;
				}
			} catch (MqttException ex) {
				logger.error("exception while connecting.... exce = {}", ex.toString());
			}
		}, new Date(System.currentTimeMillis() + 3000), this.recoveryInterval);
	}
	
	private void initConnectOptions() {
		if (mqttInitParams.getMqttRootCrtFile() == null) {
			logger.info("default cert file");
			try {
				this.mqttRootCrtFile = AbsMqttNet.class.getResourceAsStream("/root.crt");
			} catch (Exception e) {
				logger.error("cannot find config cert file：{}", e.getMessage());
			}
		} else {
			logger.info("custom cert file");
			this.mqttRootCrtFile = mqttInitParams.getMqttRootCrtFile();
		}
		
		this.connectOptions = new MqttConnectOptions();
		this.connectOptions.setServerURIs(mqttInitParams.getServerURIs());
		this.connectOptions.setUserName(mqttInitParams.getUsername());
		this.connectOptions.setPassword(mqttInitParams.getPassword().toCharArray());
		this.connectOptions.setMqttVersion(4);
		this.connectOptions.setMaxInflight(mqttInitParams.getMaxInflight());
		
		if (mqttInitParams.isCheckRootCrt() && mqttRootCrtFile != null) {
			try {
				this.connectOptions.setSocketFactory(createSSLSocket());
			} catch (Exception e) {
				logger.error("create SSL Socket error", e);
			}
		}
		
		this.connectOptions.setAutomaticReconnect(false);
		this.connectOptions.setCleanSession(mqttInitParams.getCleanSession());
		this.connectOptions.setKeepAliveInterval(mqttInitParams.getKeepAliveInterval());
		
		if (mqttInitParams.getWill() != null) {
			Will will = mqttInitParams.getWill();
			this.connectOptions.setWill(will.getTopic(), will.getPayload(), will.getQos(), will.isRetained());
		}
		
		
	}

	private SSLSocketFactory createSSLSocket() throws Exception {
		SSLContext context = SSLContext.getInstance("TLSV1.2");
		context.init(null, new TrustManager[] { (TrustManager) new MqttTrustManager(this.mqttRootCrtFile) }, null);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		return socketFactory;
	}

}
