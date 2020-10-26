package com.qjzh.link.mqtt.server;

import java.io.InputStream;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.server.callback.DefaulMessageCallback;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.callback.RequestMessageListener;
import com.qjzh.link.mqtt.server.channel.ConnectState;
import com.qjzh.link.mqtt.server.channel.IOnCallListener;
import com.qjzh.link.mqtt.server.channel.IOnCallReplyListener;
import com.qjzh.link.mqtt.server.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.utils.MqttTrustManager;

/**
 * @DESC: MQTT 客户端连接
 * @author LIU.ZHENXING
 * @date 2020年8月17日上午11:31:48
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttNet implements INet{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//保存形式，默认为以内存保存
	private MemoryPersistence persistence;
	//mqtt 客户端
	private MqttAsyncClient mqttAsyncClient;
	//ssl工厂，用于ssl mqtt
	private SSLSocketFactory socketFactory;
	//mqtt连接配置
	private MqttConnectOptions connOpts;
	//ssl证书资源
	private InputStream mqttRootCrtFile;
	//是否初始化连接
	private boolean isInitConnect = false;
	//等待动作完成最大时间
	private int timeToWait = 5000;
	//连接状态
	private ConnectState connectState = ConnectState.DISCONNECTED;
	//回调类
	private DefaulMessageCallback defaulCallback = null;
	//mqtt初始化连接配置
	private MqttInitParams mqttInitParams;
	
	private String clientId;
	private IOnCallListener callListener;
	
	private IOnCallReplyListener callReplyListener;
	
	private IOnSubscribeListener subscribeListener;
	@Autowired
	private RequestMessageListener requestMessageListener;
	@Autowired
	private ReplyMessageListener replyMessageListener;

	public MqttNet(String clientId, MqttInitParams initParams) {
		this.clientId = clientId;
		this.mqttInitParams = initParams;
	}
	
	public void init() {
		logger.debug("init");

		if (this.isInitConnect || this.connectState == ConnectState.CONNECTING
				|| this.connectState == ConnectState.CONNECTED) {
			logger.info("already init, ignore init call!");
			return;
		}
		if (mqttInitParams == null || !(mqttInitParams instanceof MqttInitParams)
				|| !((MqttInitParams) mqttInitParams).checkValid()) {
			logger.info("init error ,params error");
			return;
		}

		if (mqttInitParams.getMqttRootCrtFile() == null) {
			logger.info("default cert file");
			try {
				this.mqttRootCrtFile = MqttNet.class.getResourceAsStream("/root.crt");
			} catch (Exception e) {
				logger.error("cannot find config cert file：{}", e.getMessage());
			}
		} else {
			logger.info("custom cert file");
			this.mqttRootCrtFile = mqttInitParams.getMqttRootCrtFile();
		}
		mqttClientConnect();
	}

	public MqttInitParams getInitParams() {
		return this.mqttInitParams;
	}

	public void setConnectState(ConnectState connectState) {
		this.connectState = connectState;
	}

	public ConnectState getConnectState() {
		return this.connectState;
	}
	
	public int getTimeToWait() {
		return timeToWait;
	}

	public void setCallListener(IOnCallListener callListener) {
		this.callListener = callListener;
	}

	public void setCallReplyListener(IOnCallReplyListener callReplyListener) {
		this.callReplyListener = callReplyListener;
	}

	public void setSubscribeListener(IOnSubscribeListener subscribeListener) {
		this.subscribeListener = subscribeListener;
	}

	public void setRequestMessageListener(RequestMessageListener requestMessageListener) {
		//this.requestMessageListener = requestMessageListener;
	}

	public void setReplyMessageListener(ReplyMessageListener replyMessageListener) {
		this.replyMessageListener = replyMessageListener;
	}

	public IMqttAsyncClient getClient() {
		return this.mqttAsyncClient;
	}

	private void mqttClientConnect() {
		this.persistence = new MemoryPersistence();
		String timestamp = System.currentTimeMillis() + "";

		String[] serverURIs = mqttInitParams.getServerURIs();
		
		String mqttClientId = "clientId=" + this.clientId + ",timestamp=" + timestamp + "|";

		this.timeToWait = mqttInitParams.getTimeToWait();
		String username = mqttInitParams.getUsername();
		String password = mqttInitParams.getPassword();

		try {
			this.mqttAsyncClient = new MqttAsyncClient(serverURIs[0], mqttClientId, this.persistence);
		} catch (Exception e) {
			logger.error("create mqtt client error", e);
			this.connectState = ConnectState.CONNECTFAIL;
			return;
		}
		this.connOpts = new MqttConnectOptions();
		this.connOpts.setMqttVersion(4);
		this.connOpts.setMaxInflight(100);
		if (mqttInitParams.isCheckRootCrt()) {
			try {
				this.socketFactory = createSSLSocket();
				this.connOpts.setSocketFactory(this.socketFactory);
			} catch (Exception e) {
				logger.error("create SSL Socket error", e);
			}
		}

		this.connOpts.setAutomaticReconnect(true);
		this.connOpts.setCleanSession(mqttInitParams.getCleanSession());
		this.connOpts.setUserName(username);
		this.connOpts.setPassword(password.toCharArray());
		this.connOpts.setKeepAliveInterval(mqttInitParams.getKeepAliveInterval());

		this.defaulCallback = new DefaulMessageCallback();
		this.mqttAsyncClient.setCallback(this.defaulCallback);
		try {
			this.connectState = ConnectState.CONNECTING;
			this.mqttAsyncClient.connect(this.connOpts, null, new IMqttActionListener() {
				public void onSuccess(IMqttToken asyncActionToken) {
					logger.info("connect onSuccess");
					MqttNet.this.isInitConnect = true;
					MqttNet.this.connectState = ConnectState.CONNECTED;
				}

				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					logger.info("connect onFailure, exce = " + exception.toString());
					MqttNet.this.connectState = ConnectState.CONNECTFAIL;
				}
			}).waitForCompletion(timeToWait);
			logger.debug("mqtt client connect..." + String.join(",", serverURIs));
		} catch (Exception e) {
			logger.error("mqtt client connect error, {}", e);
			this.connectState = ConnectState.CONNECTFAIL;
		}
	}

	private SSLSocketFactory createSSLSocket() throws Exception {
		SSLContext context = SSLContext.getInstance("TLSV1.2");
		context.init(null, new TrustManager[] { (TrustManager) new MqttTrustManager(this.mqttRootCrtFile) }, null);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		return socketFactory;
	}

	@PreDestroy
	public void destroy() {
		logger.debug("destroy");
		this.isInitConnect = false;
		if (getClient() == null) {
			logger.info("client is null");
			return;
		}
		try {
			this.mqttAsyncClient.disconnect();
			logger.info("disconnect....");
			this.connectState = ConnectState.DISCONNECTED;
			this.persistence.close();
			this.persistence = null;
			this.mqttAsyncClient = null;
			this.socketFactory = null;
			this.mqttRootCrtFile.close();
			this.mqttRootCrtFile = null;

			this.mqttInitParams = null;
		} catch (Exception ex) {
			logger.error("destroy error! ", ex);
		}
	}

	@Override
	public void publish(PublishRequest publishRequest) {
		/*MqttPublish publish = new MqttPublish(this, publishRequest, callListener);
		publish.send();*/
	}

	@Override
	public PublishResponse publishRpc(PublishRequest publishRequest) {
		/*MqttPublishRpc publishRpc = new MqttPublishRpc(this, publishRequest, callListener);
		return publishRpc.sendRpc();*/
		return null;
	}
	
	@Override
	public PublishResponse publishRpc(PublishRequest publishRequest, int timeout) {
		/*MqttPublishRpc publishRpc = new MqttPublishRpc(this, publishRequest, timeout, callListener);
		return publishRpc.sendRpc();*/
		return null;
	}

	@Override
	public void publishReply(PublishResponse publishResponse) {
		/*MqttPublishReply publishReply = new MqttPublishReply(this, publishResponse, callReplyListener);
		publishReply.send();*/
	}
	
	@Override
	public void subscribe(SubscribeRequest subscribeRequest) {
		/*MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				requestMessageListener, subscribeListener);
		mqttSubscribe.receive();*/
	}
	
	@Override
	public void subscribeReply(SubscribeRequest subscribeRequest) {
		/*MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				replyMessageListener, subscribeListener);
		mqttSubscribe.receive();*/
	}
	
	
}
