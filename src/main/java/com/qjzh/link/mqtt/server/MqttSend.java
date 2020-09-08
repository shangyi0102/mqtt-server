package com.qjzh.link.mqtt.server;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.AbsQJSend;
import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.QJRequest;
import com.qjzh.link.mqtt.base.QJResponse;
import com.qjzh.link.mqtt.channel.BadNetworkException;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.server.request.MqttPublishRequest;
import com.qjzh.link.mqtt.server.request.MqttSubscribeRequest;

/**
 * @DESC: mqtt发送者
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttSend extends AbsQJSend implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private IOnSubscribeListener subscribeListener = null;

	public MqttSend(MqttNet mqttNet, QJRequest qJRequest, IOnCallListener listener) {
		super(mqttNet, qJRequest, listener);
		setStatus(MqttSendStatus.waitingToSend);
	}

	public MqttSend(MqttNet mqttNet, QJRequest qJRequest, IOnSubscribeListener listener) {
		super(mqttNet, qJRequest, null);
		this.subscribeListener = listener;
		setStatus(MqttSendStatus.waitingToSend);
	}

	public void setStatus(MqttSendStatus state) {
		this.status = state;
	}

	public MqttSendStatus getStatus() {
		return (MqttSendStatus) this.status;
	}

	public IOnSubscribeListener getSubscribeListener() {
		return this.subscribeListener;
	}

	public void onSuccess(IMqttToken asyncActionToken) {
		if (this.qJRequest instanceof MqttSubscribeRequest) {
			boolean isSucc = true;
			try {
				int qos = asyncActionToken.getGrantedQos()[0];
				if (qos == 128) isSucc = false;
			} catch (Exception e) {
				logger.error("获取Qos异常!", e);
			}
			String topic = ((MqttSubscribeRequest)this.qJRequest).getTopic();
			
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
		} else if (this.qJRequest instanceof MqttPublishRequest) {
			if (this.listener != null) {
				this.listener.onSuccess(this.qJRequest, this.qJResponse);
			}
		}
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "Mqtt send failed: unknown error";

		if (this.qJRequest instanceof MqttSubscribeRequest 
				&& this.subscribeListener != null) {

			if (exception instanceof BadNetworkException) {
				QJError error = new QJError();
				error.setCode(4101);
				this.subscribeListener.onFailed(((MqttSubscribeRequest) this.qJRequest).getTopic(), error);
			} else {
				QJError error = new QJError();
				error.setCode(4201);
				error.setMsg(msg);
				this.subscribeListener.onFailed(((MqttSubscribeRequest) this.qJRequest).getTopic(), error);
			}

		} else if (this.qJRequest instanceof MqttPublishRequest && this.listener != null) {

			if (exception instanceof BadNetworkException) {
				QJError error = new QJError();
				error.setCode(4101);
				this.listener.onFailed(this.qJRequest, error);
			} else {
				QJError error = new QJError();
				error.setCode(4201);
				error.setMsg(msg);
				this.listener.onFailed(this.qJRequest, error);
			}
		}
	}

	public void rpcMessageArrived(String topic, MqttMessage message) {
		logger.info("rpc topic={}, msg={}", topic, message.toString());
		if (this.qJRequest instanceof MqttPublishRequest) {

			MqttPublishRequest publishRequest = (MqttPublishRequest) this.qJRequest;

			if (publishRequest.isRPC()
					&& (this.status == MqttSendStatus.published || this.status == MqttSendStatus.waitingToPublish)
					&& topic.equals(publishRequest.getReplyTopic())) {
				logger.info("match succ!,replyTopic={}", topic);

				setStatus(MqttSendStatus.completed);

				if (null == this.qJResponse)
					this.qJResponse = new QJResponse();
				this.qJResponse.data = message.toString();

				if (this.listener != null)
					this.listener.onSuccess(this.qJRequest, this.qJResponse);
			}
		}
	}
}

