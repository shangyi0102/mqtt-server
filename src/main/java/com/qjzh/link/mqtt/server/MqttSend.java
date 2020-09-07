package com.qjzh.link.mqtt.server;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.QJRequest;
import com.qjzh.link.mqtt.base.QJResponse;
import com.qjzh.link.mqtt.base.QJSend;
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
public class MqttSend extends QJSend implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private IOnSubscribeListener subscribeListener = null;

	public MqttSend(MqttNet mqttNet, QJRequest request, IOnCallListener listener) {
		super(mqttNet, request, listener);
		setStatus(MqttSendStatus.waitingToSend);
	}

	public MqttSend(MqttNet mqttNet, QJRequest request, IOnSubscribeListener listener) {
		super(mqttNet, request, null);
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
		if (this.request instanceof MqttSubscribeRequest) {
			setStatus(MqttSendStatus.completed);

			boolean isSucc = true;
			try {
				int qos = asyncActionToken.getGrantedQos()[0];
				if (qos == 128)
					isSucc = false;
			} catch (Exception e) {
				logger.error("获取Qos异常!", e);
			}
			if (this.subscribeListener != null) {
				if (isSucc) {
					this.subscribeListener.onSuccess(((MqttSubscribeRequest) this.request).getTopic());
				} else {
					QJError error = new QJError();
					error.setCode(4103);
					error.setMsg("subACK Failure");
					this.subscribeListener.onFailed(((MqttSubscribeRequest) this.request).getTopic(), error);
				}

			}
		} else if (this.request instanceof MqttPublishRequest) {
			MqttPublishRequest publishRequest = (MqttPublishRequest) this.request;

			if (!publishRequest.isRPC()) {
				setStatus(MqttSendStatus.completed);

				if (this.listener != null) {
					this.listener.onSuccess(this.request, this.response);
				}

			} else if (this.status == MqttSendStatus.waitingToSubReply) {
				setStatus(MqttSendStatus.subReplyed);
				MqttSendExecutor sendExecutor = new MqttSendExecutor();
				sendExecutor.asyncSend(this);
			} else if (this.status == MqttSendStatus.waitingToPublish) {
				setStatus(MqttSendStatus.published);
			}
		}
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		String msg = (null != exception) ? exception.getMessage() : "MqttNet send failed: unknown error";

		setStatus(MqttSendStatus.completed);
		if (this.request instanceof MqttSubscribeRequest) {

			if (this.subscribeListener != null) {
				byte type = 5;
				if (exception instanceof com.qjzh.link.mqtt.channel.BadNetworkException) {
					type = 6;
				}

				if (type == 6) {
					QJError error = new QJError();
					error.setCode(4101);
					this.subscribeListener.onFailed(((MqttSubscribeRequest) this.request).getTopic(), error);
				} else {
					QJError error = new QJError();
					error.setCode(4201);
					error.setMsg(msg);
					this.subscribeListener.onFailed(((MqttSubscribeRequest) this.request).getTopic(), error);
				}

			}

		} else if (this.request instanceof MqttPublishRequest && this.listener != null) {

			byte type = 2;
			if (exception instanceof com.qjzh.link.mqtt.channel.BadNetworkException) {
				type = 3;
			}

			if (type == 3) {
				QJError error = new QJError();
				error.setCode(4101);
				this.listener.onFailed(this.request, error);
			} else {
				QJError error = new QJError();
				error.setCode(4201);
				error.setMsg(msg);
				this.listener.onFailed(this.request, error);
			}
		}
	}

	public void rpcMessageArrived(String topic, MqttMessage message) {
		logger.info("rpc topic={}, msg={}", topic, message.toString());
		if (this.request instanceof MqttPublishRequest) {

			MqttPublishRequest publishRequest = (MqttPublishRequest) this.request;

			if (publishRequest.isRPC()
					&& (this.status == MqttSendStatus.published || this.status == MqttSendStatus.waitingToPublish)
					&& topic.equals(publishRequest.getReplyTopic())) {
				logger.info("match succ!,replyTopic={}", topic);

				setStatus(MqttSendStatus.completed);

				if (null == this.response)
					this.response = new QJResponse();
				this.response.data = message.toString();

				if (this.listener != null)
					this.listener.onSuccess(this.request, this.response);
			}
		}
	}
}

