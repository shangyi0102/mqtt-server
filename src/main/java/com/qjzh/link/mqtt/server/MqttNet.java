package com.qjzh.link.mqtt.server;

import java.io.InputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.QJRequest;
import com.qjzh.link.mqtt.base.QJSend;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.channel.IOnSubscribeRrpcListener;
import com.qjzh.link.mqtt.channel.MqttEventDispatcher;
import com.qjzh.link.mqtt.server.callback.MqttDefaulCallback;
import com.qjzh.link.mqtt.server.request.MqttSubscribeRequest;
import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.utils.MqttTrustManager;

/**
 * @DESC: MQTT 客户端连接
 * @author LIU.ZHENXING
 * @date 2020年8月17日上午11:31:48
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttNet implements INet, InitializingBean,DisposableBean {
	
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
	//连接状态
	private ConnectState connectState = ConnectState.DISCONNECTED;
	//回调类
	private MqttDefaulCallback defaulCallback = null;
	//mqtt初始化连接配置
	private MqttInitParams mqttInitParams;

	public MqttNet(MqttInitParams initParams) {
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

	public QJSend asyncSend(QJRequest request, IOnCallListener listener) {
		MqttSend mqttSend = new MqttSend(this, request, listener);
		MqttSendExecutor sendExecutor = new MqttSendExecutor();
		sendExecutor.asyncSend(mqttSend);
		return (QJSend) mqttSend;
	}

	public void retry(QJSend mqttSend) {
		MqttSendExecutor sendExecutor = new MqttSendExecutor();
		sendExecutor.asyncSend(mqttSend);
	}

	public void subscribe(String topic, IOnSubscribeListener listener) {
		if (StringUtils.isEmpty(topic)) {
			logger.info("topic is empty");
			return;
		}
		MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
		subscribeRequest.setTopic(topic);
		subscribeRequest.setSubscribe(true);
		MqttSend mqttSend = new MqttSend(this, (QJRequest) subscribeRequest, listener);
		MqttSendExecutor sendExecutor = new MqttSendExecutor();
		sendExecutor.asyncSend((QJSend) mqttSend);
	}

	public void unSubscribe(String topic, IOnSubscribeListener listener) {
		if (StringUtils.isEmpty(topic)) {
			logger.info("topic is empty");
			return;
		}
		MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
		subscribeRequest.setTopic(topic);
		subscribeRequest.setSubscribe(false);
		MqttSend mqttSend = new MqttSend(this, (QJRequest) subscribeRequest, listener);
		MqttSendExecutor sendExecutor = new MqttSendExecutor();
		sendExecutor.asyncSend((QJSend) mqttSend);
	}

	public void subscribeRrpc(String topic, final IOnSubscribeRrpcListener listener) {
		logger.debug("topic = " + topic);

		if (StringUtils.isEmpty(topic) || listener == null) {
			logger.info("params error");
			return;
		}
		subscribe(topic, new IOnSubscribeListener() {
			public void onSuccess(String topic) {
				listener.onSubscribeSuccess(topic);
			}

			public void onFailed(String topic, QJError error) {
				listener.onSubscribeFailed(topic, error);
			}

			public boolean needUISafety() {
				return listener.needUISafety();
			}
		});
		if (this.defaulCallback != null) {
			logger.info("registerRrpcListener");
			this.defaulCallback.registerRrpcListener(topic, listener);
		}
	}

	
	public IMqttAsyncClient getClient() {
		return (IMqttAsyncClient) this.mqttAsyncClient;
	}

	private void mqttClientConnect() {
		this.persistence = new MemoryPersistence();
		String timestamp = System.currentTimeMillis() + "";

		String[] serverURIs = mqttInitParams.getServerURIs();
		String serverURI = serverURIs[0];
		String mqttClientId = "clientId" + ",timestamp=" + timestamp + "|";

		String username = mqttInitParams.getUsername();
		String password = mqttInitParams.getPassword();

		try {
			this.mqttAsyncClient = new MqttAsyncClient(serverURI, mqttClientId, this.persistence);
		} catch (Exception e) {
			logger.error("create mqtt client error", e);
			this.connectState = ConnectState.CONNECTFAIL;
			MqttEventDispatcher.getInstance().broadcastMessage(7, null, null,
					"create mqtt client error,e=" + e.toString());
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

		this.defaulCallback = new MqttDefaulCallback(this);
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
					MqttEventDispatcher.getInstance().broadcastMessage(7, null, null, exception.toString());
				}
			});
			logger.debug("mqtt client connect..." + String.join(",", serverURIs));
		} catch (Exception e) {
			logger.error("mqtt client connect error, {}", e);
			this.connectState = ConnectState.CONNECTFAIL;
			MqttEventDispatcher.getInstance().broadcastMessage(7, null, null, e.toString());
		}
	}

	private SSLSocketFactory createSSLSocket() throws Exception {
		SSLContext context = SSLContext.getInstance("TLSV1.2");
		context.init(null, new TrustManager[] { (TrustManager) new MqttTrustManager(this.mqttRootCrtFile) }, null);
		SSLSocketFactory socketFactory = context.getSocketFactory();
		return socketFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	
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
			MqttEventDispatcher.getInstance().broadcastMessage(2, null, null, "disconnect success");

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
	
}
