package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.AbsMqttFeed;
import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.channel.IOnCallReplyListener;
import com.qjzh.link.mqtt.exception.BadNetworkException;
import com.qjzh.link.mqtt.exception.MqttThrowable;

/**
 * @DESC: mqtt发送者
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublishReply extends AbsMqttFeed implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//请求应答
	private PublishResponse publishResponse;
	 
	private IOnCallReplyListener callReplyListener = new CallReplyListenerInternal();

	public MqttPublishReply(MqttNet mqttNet, PublishResponse publishResponse, 
			IOnCallReplyListener callReplyListener) {
		super(mqttNet);
		this.publishResponse = publishResponse;
		if (callReplyListener != null) {
			this.callReplyListener = callReplyListener;
		}
	}

	public void setStatus(MqttStatus status) {
		this.status = status;
	}

	public MqttStatus getStatus() {
		return (MqttStatus)status;
	}

	public PublishResponse getPublishResponse() {
		return publishResponse;
	}

	public void setPublishResponse(PublishResponse publishResponse) {
		this.publishResponse = publishResponse;
	}

	public IOnCallReplyListener getCallReplyListener() {
		return callReplyListener;
	}

	public void send(){
		
		if (null == this.publishResponse) {
			logger.error("bad parameters: null");
			return;
		}
		
		if (!(getNet() instanceof MqttNet)) {
			logger.error("bad parameter: need MqttNet");
			return;
		}
		MqttNet mqttNet = (MqttNet) getNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient) {
			logger.error("MqttNet::getClient() return null");
			return;
		}
		
		if (mqttNet.getConnectState() != ConnectState.CONNECTED) {
			logger.error("mqtt not connected!");
			setStatus(MqttStatus.completed);
			onFailure(null, new BadNetworkException());

			return;
		}
		
		Object objPayload = publishResponse.getData();
		String topic = publishResponse.getReplyTopic();
		int qos = publishResponse.getQos();

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
			setStatus(MqttStatus.waitingToPublish);

			mqttAsyncClient.publish(topic, message, null, this).waitForCompletion(mqttNet.getTimeToWait());
			
		} catch (Exception e) {
			logger.error("send publish error! ", e);
			onFailure(null, new MqttThrowable(e.getMessage()));
		}
		
	}
	
	public void onSuccess(IMqttToken asyncActionToken) {
		this.callReplyListener.onSuccess(this.publishResponse);
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "publish reply failed: unknown error";
		
		if (exception instanceof BadNetworkException) {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_NET);
			error.setMsg("bad network");
			this.callReplyListener.onFailed(this.publishResponse, error);
		} else {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_SERVER);
			error.setMsg(msg);
			this.callReplyListener.onFailed(this.publishResponse, error);
		}
	}
	
	class CallReplyListenerInternal implements IOnCallReplyListener {

		@Override
		public void onSuccess(PublishResponse response) {
			logger.info("publish reply succ, topic = [{}], qos=[{}], msg = [{}]", response.getReplyTopic(), 
					response.getQos() ,JSON.toJSONString(response.getData()));
		}
		@Override
		public void onFailed(PublishResponse response, MqttError error) {
			logger.error("publish reply fail, topic = [{}], error=[{}]", response.getReplyTopic(), 
					error.toString());
		}
		
    }
	
}

