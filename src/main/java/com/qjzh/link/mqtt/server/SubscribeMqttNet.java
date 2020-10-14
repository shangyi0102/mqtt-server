package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import com.qjzh.link.mqtt.base.AbsMqttDriven;
import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.IMqttNet;
import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.base.exception.BadNetworkException;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.callback.RequestMessageListener;
import com.qjzh.link.mqtt.server.channel.IOnSubscribeListener;

public class SubscribeMqttNet extends AbsMqttNet {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<SubscribeRequest> subscribeRequests;
	
	private IOnSubscribeListener subscribeListener;
	@Autowired
	private RequestMessageListener requestMessageListener;
	@Autowired
	private ReplyMessageListener replyMessageListener;
	
	public SubscribeMqttNet(MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		this("subscribe", initParams, taskScheduler);
	}
	
	public SubscribeMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		this(clientId, initParams, taskScheduler, new ArrayList<SubscribeRequest>());
	}
	
	public SubscribeMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler, @NotNull List<SubscribeRequest> subscribeRequests) {
		super(clientId, initParams, taskScheduler);
		this.subscribeRequests = subscribeRequests;
	}
	
	public void addSubscribeRequest(SubscribeRequest subscribeRequest) throws MqttInvokeException {
		this.subscribeRequests.add(subscribeRequest);
		if (subscribeRequest.isReply()) {
			subscribeReply(subscribeRequest);
		}else{
			subscribe(subscribeRequest);
		}
	} 
	
	public void setRequestMessageListener(RequestMessageListener requestMessageListener) {
		this.requestMessageListener = requestMessageListener;
	}
	
	public void setReplyMessageListener(ReplyMessageListener replyMessageListener) {
		this.replyMessageListener = replyMessageListener;
	}

	@Override
	public void connectSuccess() {
		for (SubscribeRequest subscribeRequest : subscribeRequests) {
			try {
				if (subscribeRequest.isReply()) {
					subscribeReply(subscribeRequest);
				}else{
					subscribe(subscribeRequest);
				}
			} catch (MqttInvokeException ex) {
				logger.error("订阅主题失败!", ex);
			}
		}
	}
	
	
	private void subscribe(SubscribeRequest subscribeRequest) throws MqttInvokeException {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				requestMessageListener, subscribeListener);
		mqttSubscribe.subscribe();
	}
	
	private void subscribeReply(SubscribeRequest subscribeRequest) throws MqttInvokeException {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				replyMessageListener, subscribeListener);
		mqttSubscribe.subscribe();
	}
	
	/**
	 * @DESC: 订阅器
	 * @author LIU.ZHENXING
	 * @date 2020年8月18日下午1:50:19
	 * @version 1.0.0
	 * @copyright www.7g.com
	 */
	class MqttSubscribe extends AbsMqttDriven implements IMqttActionListener {
		
		private final Logger logger = LoggerFactory.getLogger(getClass());
		//订阅请求
		private SubscribeRequest subscribeRequest;
		//消息监听器
		private IMqttMessageListener mqttMessageListener;
		
		private IOnSubscribeListener subscribeListener = new SubscribeListenerInternal();

		public MqttSubscribe(IMqttNet mqttNet, SubscribeRequest subscribeRequest, 
				IMqttMessageListener mqttMessageListener,
				IOnSubscribeListener subscribeListener) {
			super(mqttNet);
			this.mqttMessageListener = mqttMessageListener;
			this.subscribeRequest = subscribeRequest;
			if (subscribeListener != null) {
				this.subscribeListener = subscribeListener;
			}
		}
		
		public IOnSubscribeListener getSubscribeListener() {
			return subscribeListener;
		}
		
		public SubscribeRequest getSubscribeRequest() {
			return subscribeRequest;
		}

		public void subscribe() throws MqttInvokeException {
			
			if (null == subscribeRequest) {
				onFailure(null, new IllegalArgumentException("bad parameters: subscribeRequest"));
				throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
			}
			
			if (!(getMqttNet() instanceof AbsMqttNet)) {
				onFailure(null, new IllegalArgumentException("bad parameter: need MqttNet"));
				throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
			}
			
			AbsMqttNet mqttNet = (AbsMqttNet)getMqttNet();
			IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
			if (null == mqttAsyncClient || !mqttAsyncClient.isConnected()) {
				onFailure(null, new BadNetworkException());
				throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "mqtt server not connected!");
			}
			
			
			String topic = subscribeRequest.getTopic();
			int qos = subscribeRequest.getQos();
				
			if (StringUtils.isEmpty(topic)) {
				onFailure(null, new NullPointerException("bad parameters: subscribe request , topic empty"));
				throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
				
			}
				
			try {
				if (subscribeRequest.isSubscribe()) {
					IMqttToken tok = mqttAsyncClient.subscribe(topic, qos, null, this, mqttMessageListener);
					tok.waitForCompletion(mqttNet.getTimeToWait());
				} else {
					mqttAsyncClient.unsubscribe(topic, null, this);
				}
			} catch (Exception ex) {
				logger.error("subsribe request error! ", ex);
				onFailure(null, ex);
				throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, ex.getMessage());
				
			}
		}
		
		public void onSuccess(IMqttToken asyncActionToken) {
			boolean isSucc = true;
			try {
				int qos = asyncActionToken.getGrantedQos()[0];
				if (qos == 128) isSucc = false;
			} catch (Exception e) {
				logger.error("获取Qos异常!", e);
			}
			String topic = subscribeRequest.getTopic();
			
			if (isSucc) {
				this.subscribeListener.onSuccess(topic);
			} else {
				MqttError error = new MqttError();
				error.setCode(4103);
				error.setMsg("subACK Failure");
				this.subscribeListener.onFailed(topic, error);
			}
		}

		public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
			
			String msg = (null != exception && StringUtils.isEmpty(exception.getMessage())) 
					? exception.getMessage() : "subscribe failed: unknown error";

			if (exception instanceof BadNetworkException) {
				MqttError error = new MqttError();
				error.setCode(ErrorCode.INVOKE_NET);
				error.setMsg("bad network");
				this.subscribeListener.onFailed(subscribeRequest.getTopic(), error);
			} else {
				MqttError error = new MqttError();
				error.setCode(ErrorCode.INVOKE_SERVER);
				error.setMsg(msg);
				this.subscribeListener.onFailed(subscribeRequest.getTopic(), error);
			}
		}
		
		class SubscribeListenerInternal implements IOnSubscribeListener {

			@Override
			public void onSuccess(String topic) {
				logger.info("subscribe succ, topic = [{}]", topic);
			}

			@Override
			public void onFailed(String topic, MqttError error) {
				logger.error("subscribe fail, topic = [{}], error=[{}]", topic, error.toString());
			}

	    }

	}
	
}
