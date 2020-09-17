package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.AbsMqtt;
import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.exception.BadNetworkException;
import com.qjzh.link.mqtt.base.exception.MqttThrowable;
import com.qjzh.link.mqtt.server.channel.ConnectState;
import com.qjzh.link.mqtt.server.channel.IOnCallListener;

/**
 * @DESC: mqtt发送者
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublish extends AbsMqtt implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//请求体
	private PublishRequest publishRequest;
	 
	private IOnCallListener callListener = new CallListenerInternal();

	public MqttPublish(MqttNet mqttNet, PublishRequest publishRequest, 
			IOnCallListener callListener) {
		super(mqttNet);
		this.publishRequest = publishRequest;
		if (callListener != null) {
			this.callListener = callListener;
		}
	}

	public PublishRequest getPublishRequest() {
		return publishRequest;
	}

	public void setPublishRequest(PublishRequest publishRequest) {
		this.publishRequest = publishRequest;
	}


	public IOnCallListener getCallListener() {
		return callListener;
	}

	public void setCallListener(IOnCallListener callListener) {
		this.callListener = callListener;
	}

	public void send() {
		if (null == this.publishRequest) {
			logger.error("bad parameters: null");
			return;
		}
		
		if (!(getNet() instanceof MqttNet)) {
			logger.error("bad parameter: need MqttNet");
			return;
		}
		MqttNet mqttNet = (MqttNet)getNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient) {
			logger.error("MqttNet::getClient() return null");
			return;
		}
		
		if (mqttNet.getConnectState() != ConnectState.CONNECTED) {
			logger.error("mqtt not connected!");
			onFailure(null, new BadNetworkException());
			return;
		}
		
		Object objPayload = publishRequest.getPayload();
		String topic = publishRequest.getTopic();
		int qos = publishRequest.getQos();

		if (StringUtils.isEmpty(topic) || objPayload == null) {
			logger.error("bad parameters: topic or payload is empty");
			onFailure(null, new NullPointerException("topic or payload is empty"));
			return;
		}
		try {
			byte[] payload = null;
			if (objPayload instanceof String) {
				payload = objPayload.toString().getBytes("UTF-8");
			} else if (objPayload instanceof byte[]) {
				payload = (byte[]) objPayload;
			} else {
				payload = JSON.toJSONString(objPayload).getBytes("UTF-8");
			}
			
			MqttMessage message = new MqttMessage(payload);
			message.setQos(qos);

			mqttAsyncClient.publish(topic, message, null, this).waitForCompletion(mqttNet.getTimeToWait());
			
		} catch (Exception e) {
			logger.error("send publish error! ", e);
			onFailure(null, new MqttThrowable(e.getMessage()));
		}
	}
	
	
	public void onSuccess(IMqttToken asyncActionToken) {
		this.callListener.onSuccess(this.publishRequest);
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "publish failed: unknown error";
		
		if (exception instanceof BadNetworkException) {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_NET);
			error.setMsg("bad network");
			this.callListener.onFailed(this.publishRequest, error);
		} else {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_SERVER);
			error.setMsg(msg);
			this.callListener.onFailed(this.publishRequest, error);
		}
	}
	
	class CallListenerInternal implements IOnCallListener {
		@Override
		public void onSuccess(PublishRequest publishRequest) {
			logger.info("publish succ, topic = [{}], qos=[{}], msg = [{}]", publishRequest.getTopic(), 
					publishRequest.getQos() ,JSON.toJSONString(publishRequest.getPayload()));
		}

		@Override
		public void onFailed(PublishRequest publishRequest, MqttError error) {
			logger.error("publish fail, topic = [{}], error=[{}]", publishRequest.getTopic(), 
					error.toString());
		}
    }
	
}

