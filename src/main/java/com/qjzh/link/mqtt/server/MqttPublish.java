package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.AbsMqttDriven;
import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.IMqttNet;
import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.exception.BadNetworkException;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.channel.IOnCallListener;

/**
 * @DESC: mqtt发布
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublish extends AbsMqttDriven implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//请求体
	private PublishRequest publishRequest;
	 
	private IOnCallListener callListener = new CallListenerInternal();

	public MqttPublish(IMqttNet mqttNet, PublishRequest publishRequest, 
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

	public void send() throws MqttInvokeException {
		if (null == this.publishRequest) {
			onFailure(null, new IllegalArgumentException("bad parameters: publishRequest"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
		}
		
		if (!(getMqttNet() instanceof AbsMqttNet)) {
			onFailure(null, new IllegalArgumentException("bad parameter: need AbsMqttNet"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
		}
		
		AbsMqttNet mqttNet = (AbsMqttNet)getMqttNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient || !mqttAsyncClient.isConnected()) {
			onFailure(null, new BadNetworkException());
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "mqtt server not connected!");
		}
		
		Object objPayload = publishRequest.getPayload();
		String topic = publishRequest.getTopic();
		int qos = publishRequest.getQos();

		if (StringUtils.isEmpty(topic) || objPayload == null) {
			onFailure(null, new NullPointerException("bad parameters: topic or payload is empty"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
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
			
		} catch (Exception ex) {
			logger.error("send publish error! ", ex);
			onFailure(null, ex);
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, ex.getMessage());
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

