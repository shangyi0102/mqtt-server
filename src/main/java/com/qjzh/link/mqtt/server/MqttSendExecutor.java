package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.AbsQJSend;
import com.qjzh.link.mqtt.base.QJRequest;
import com.qjzh.link.mqtt.channel.BadNetworkException;
import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.server.callback.RpcMsgCallback;
import com.qjzh.link.mqtt.server.request.MqttPublishRequest;
import com.qjzh.link.mqtt.server.request.MqttSubscribeRequest;
import com.qjzh.link.mqtt.utils.MqttProtocolHelper;

/**
 * @DESC: mqtt发送执行器
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:07:53
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttSendExecutor{
	
	private static final Logger logger = LoggerFactory.getLogger(MqttSendExecutor.class);

	public static void send(AbsQJSend send) {
		if (null == send || null == send.getRequest()) {
			logger.error("bad parameters: null");
			return;
		}
		
		if (!(send.getINet() instanceof MqttNet)) {
			logger.error("bad parameter: need MqttNet");
			return;
		}
		MqttNet mqttNet = (MqttNet) send.getINet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient) {
			logger.error("MqttNet::getClient() return null");
			return;
		}

		if (!(send instanceof MqttSend)) {
			logger.error("bad parameter: need MqttSend");
			return;
		}
		
		MqttSend mqttSend = (MqttSend) send;
		if (mqttNet.getConnectState() != ConnectState.CONNECTED) {
			logger.error("mqtt not connected!");
			mqttSend.setStatus(MqttSendStatus.completed);
			mqttSend.onFailure(null, new BadNetworkException());

			return;
		}
		
		QJRequest qJRequest = send.getRequest();

		if (qJRequest instanceof MqttPublishRequest) {
			MqttPublishRequest publishRequest = (MqttPublishRequest) qJRequest;

			if (StringUtils.isEmpty(publishRequest.getTopic()) || publishRequest.getPayload() == null) {
				logger.error("bad parameters: topic or payload empty");
				mqttSend.onFailure(null, new NullPointerException("topic or payload empty"));
				return;
			}
			
			if (publishRequest.isRPC()) {
				try {
					String payloadStr = null;
					if (publishRequest.getPayload() instanceof String) {
						payloadStr = publishRequest.getPayload().toString();
					} else if (publishRequest.getPayload() instanceof byte[]) {
						payloadStr = new String((byte[]) publishRequest.getPayload(), "UTF-8");
					} else {
						try {
							payloadStr = publishRequest.getPayload().toString();
						} catch (Exception e) {
							logger.error("publish , toString error," + e.toString());
							mqttSend.setStatus(MqttSendStatus.completed);
							mqttSend.onFailure(null,
									new MqttThrowable("RPC request ,payload should be String or byte[]"));

							return;
						}
					}
					publishRequest.setMsgId(MqttProtocolHelper.parseMsgIdFromPayload(payloadStr));

					if (StringUtils.isEmpty(publishRequest.getReplyTopic())) {
						publishRequest.setReplyTopic(publishRequest.getTopic() + "_reply");
					}
					logger.info("publish: RPC sub reply topic: [ " + publishRequest.getReplyTopic() + " ]");
					mqttSend.setStatus(MqttSendStatus.waitingToSubReply);
					mqttAsyncClient.subscribe(publishRequest.getReplyTopic(), 0, null, mqttSend,
							new RpcMsgCallback(publishRequest.getReplyTopic(), mqttSend));
				} catch (Exception e) {
					logger.error("publish , send subsribe reply error", e);
					mqttSend.setStatus(MqttSendStatus.completed);
					mqttSend.onFailure(null, new MqttThrowable(e.getMessage()));
				}
			} else {
				try {
					byte[] payload = null;

					Object objPayload = publishRequest.getPayload();
					if (objPayload instanceof String) {
						payload = objPayload.toString().getBytes("UTF-8");
					} else if (objPayload instanceof byte[]) {
						payload = (byte[]) objPayload;
					} else {
						payload = JSON.toJSONString(objPayload).getBytes("UTF-8");
					}

					if (objPayload == null) {
						logger.error("payload is empty");
						mqttSend.onFailure(null, new NullPointerException("payload empty"));

						return;
					}
					logger.info("publish: topic: [ " + publishRequest.getTopic() + " ]");
					logger.info("publish: payload: [ " + objPayload.toString() + " ]");
					MqttMessage message = new MqttMessage(payload);
					message.setQos(publishRequest.getQos());

					if (publishRequest.isRPC()) {
						mqttSend.setStatus(MqttSendStatus.waitingToPublish);
					} else {
						mqttSend.setStatus(MqttSendStatus.waitingToComplete);
					}

					mqttAsyncClient.publish(publishRequest.getTopic(), message, null, mqttSend);
				} catch (Exception e) {
					logger.error("send publish error, e = ", e);
					mqttSend.setStatus(MqttSendStatus.completed);
					mqttSend.onFailure(null, new MqttThrowable(e.getMessage()));
				}

			}

		} else if (qJRequest instanceof MqttSubscribeRequest) {
			
			MqttSubscribeRequest subscribeRequest = (MqttSubscribeRequest) qJRequest;
			if (StringUtils.isEmpty(subscribeRequest.getTopic())) {
				logger.error("bad parameters: subscribe request , topic empty");
				mqttSend.onFailure(null, new NullPointerException("subscribe request , topic empty"));
				return;
			}
			
			String topic = subscribeRequest.getTopic();
			int qos = subscribeRequest.getQos();
			IMqttMessageListener mqttMessageListener = subscribeRequest.getMqttMessageListener();
			try {
				if (subscribeRequest.isSubscribe()) {
					//logger.info("subscribe: topic: [{}]", topic);
					mqttAsyncClient.subscribe(topic, qos, null, mqttSend, mqttMessageListener);
				} else {
					//logger.info("unsubscribe: topic: [{}]", topic);
					mqttAsyncClient.unsubscribe(topic, null, mqttSend);
				}
			} catch (Exception e) {
				logger.error("send subsribe error! ", e);
				mqttSend.onFailure(null, new MqttThrowable(e.getMessage()));
			}
		}
	}
}

