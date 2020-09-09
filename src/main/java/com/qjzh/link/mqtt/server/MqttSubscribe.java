package com.qjzh.link.mqtt.server;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.AbsMqttFeed;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.exception.BadNetworkException;

/**
 * @DESC: 订阅器
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttSubscribe extends AbsMqttFeed implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//订阅请求
	private SubscribeRequest subscribeRequest;
	
	private IOnSubscribeListener subscribeListener;

	public MqttSubscribe(MqttNet mqttNet, SubscribeRequest subscribeRequest, 
			IOnSubscribeListener subscribeListener) {
		super(mqttNet);
		this.subscribeRequest = subscribeRequest;
		this.subscribeListener = subscribeListener;
		setStatus(MqttStatus.waitingToSend);
	}
	
	public void setStatus(MqttStatus status) {
		this.status = status;
	}

	public MqttStatus getStatus() {
		return (MqttStatus)status;
	}

	public IOnSubscribeListener getSubscribeListener() {
		return subscribeListener;
	}
	
	public SubscribeRequest getSubscribeRequest() {
		return subscribeRequest;
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
		
		if (this.subscribeListener != null) {
			if (isSucc) {
				this.subscribeListener.onSuccess(topic);
			} else {
				QJError error = new QJError();
				error.setCode(4103);
				error.setMsg("subACK Failure");
				this.subscribeListener.onFailed(topic, error);
			}
		} 
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "Mqtt Subscribe failed: unknown error";
		if (this.subscribeListener == null) return;

		if (exception instanceof BadNetworkException) {
			QJError error = new QJError();
			error.setCode(4101);
			this.subscribeListener.onFailed(subscribeRequest.getTopic(), error);
		} else {
			QJError error = new QJError();
			error.setCode(4201);
			error.setMsg(msg);
			this.subscribeListener.onFailed(subscribeRequest.getTopic(), error);
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

