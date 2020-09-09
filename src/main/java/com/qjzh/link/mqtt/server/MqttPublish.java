package com.qjzh.link.mqtt.server;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import com.qjzh.link.mqtt.base.AbsMqttFeed;
import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.exception.BadNetworkException;

/**
 * @DESC: mqtt发送者
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublish extends AbsMqttFeed implements IMqttActionListener {
	
	//请求体
	private PublishRequest publishRequest;
	//响应体
	private volatile PublishResponse publishResponse;
	 
	private IOnCallListener callListener;

	public MqttPublish(MqttNet mqttNet, PublishRequest publishRequest, 
			IOnCallListener callListener) {
		super(mqttNet);
		this.publishRequest = publishRequest;
		this.callListener = callListener;
		setStatus(MqttStatus.waitingToSend);
	}

	public void setStatus(MqttStatus status) {
		this.status = status;
	}

	public MqttStatus getStatus() {
		return (MqttStatus)status;
	}

	public PublishRequest getPublishRequest() {
		return publishRequest;
	}

	public void setPublishRequest(PublishRequest publishRequest) {
		this.publishRequest = publishRequest;
	}

	public PublishResponse getPublishResponse() {
		return publishResponse;
	}

	public void setPublishResponse(PublishResponse publishResponse) {
		this.publishResponse = publishResponse;
	}

	public IOnCallListener getCallListener() {
		return callListener;
	}

	public void setCallListener(IOnCallListener callListener) {
		this.callListener = callListener;
	}

	public void onSuccess(IMqttToken asyncActionToken) {
		if (this.callListener != null) {
			this.callListener.onSuccess(this.publishRequest, this.publishResponse);
		}
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "Mqtt send failed: unknown error";

		if (this.callListener != null) return;
		
		if (exception instanceof BadNetworkException) {
			QJError error = new QJError();
			error.setCode(4101);
			this.callListener.onFailed(this.publishRequest, error);
		} else {
			QJError error = new QJError();
			error.setCode(4201);
			error.setMsg(msg);
			this.callListener.onFailed(this.publishRequest, error);
		}
		
	}

	/*public void rpcMessageArrived(String topic, MqttMessage message) {
		logger.info("rpc topic={}, msg={}", topic, message.toString());
		if (this.request instanceof MqttPublishRequest) {

			MqttPublishRequest publishRequest = (MqttPublishRequest) this.request;

			if (publishRequest.isRPC()
					&& (this.status == MqttSendStatus.published || this.status == MqttSendStatus.waitingToPublish)
					&& topic.equals(publishRequest.getReplyTopic())) {
				logger.info("match succ!,replyTopic={}", topic);

				setStatus(MqttSendStatus.completed);

				if (null == this.response)
					this.response = new GeneralResponse();
				this.response.data = message.toString();

				if (this.listener != null)
					this.listener.onSuccess(this.request, this.response);
			}
		}
	}*/
}

