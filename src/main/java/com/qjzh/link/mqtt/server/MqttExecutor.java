package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.exception.BadNetworkException;
import com.qjzh.link.mqtt.exception.MqttRpcException;
import com.qjzh.link.mqtt.exception.MqttThrowable;

/**
 * @DESC: mqtt发送执行器
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:07:53
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttExecutor{
	
	private static final Logger logger = LoggerFactory.getLogger(MqttExecutor.class);

	public static void publish(MqttPublish mqttPublish) {
		if (null == mqttPublish || null == mqttPublish.getPublishRequest()) {
			logger.error("bad parameters: null");
			return;
		}
		
		if (!(mqttPublish.getNet() instanceof MqttNet)) {
			logger.error("bad parameter: need MqttNet");
			return;
		}
		MqttNet mqttNet = (MqttNet) mqttPublish.getNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient) {
			logger.error("MqttNet::getClient() return null");
			return;
		}
		
		if (mqttNet.getConnectState() != ConnectState.CONNECTED) {
			logger.error("mqtt not connected!");
			mqttPublish.setStatus(MqttStatus.completed);
			mqttPublish.onFailure(null, new BadNetworkException());

			return;
		}
		
		PublishRequest publishRequest = mqttPublish.getPublishRequest();
			
		Object objPayload = publishRequest.getPayload();
		String topic = publishRequest.getTopic();
		int qos = publishRequest.getQos();

		if (StringUtils.isEmpty(topic) || objPayload == null) {
			logger.error("bad parameters: topic or payload is empty");
			mqttPublish.onFailure(null, new NullPointerException("topic or payload is empty"));
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

			mqttPublish.setStatus(MqttStatus.waitingToPublish);

			mqttAsyncClient.publish(publishRequest.getTopic(), message, null, mqttPublish);
			
		} catch (Exception e) {
			logger.error("send publish error! ", e);
			mqttPublish.onFailure(null, new MqttThrowable(e.getMessage()));
		}
	}
	
	public static PublishResponse publishRpc(MqttPublishRpc mqttPublishRpc) {
		try {
			publish(mqttPublishRpc);
			return mqttPublishRpc.get();
		} catch (MqttRpcException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void subscribe(MqttSubscribe mqttSubscribe) {
		
		SubscribeRequest subscribeRequest = mqttSubscribe.getSubscribeRequest();
		
		if (null == mqttSubscribe || null == subscribeRequest) {
			logger.error("bad parameters: null");
			return;
		}
		
		if (!(mqttSubscribe.getNet() instanceof MqttNet)) {
			logger.error("bad parameter: need MqttNet");
			return;
		}
		MqttNet mqttNet = (MqttNet) mqttSubscribe.getNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient) {
			logger.error("MqttNet::getClient() return null");
			return;
		}
		
		if (mqttNet.getConnectState() != ConnectState.CONNECTED) {
			logger.error("mqtt not connected!");
			mqttSubscribe.setStatus(MqttStatus.completed);
			mqttSubscribe.onFailure(null, new BadNetworkException());
			return;
		}
		
		String topic = subscribeRequest.getTopic();
		int qos = subscribeRequest.getQos();
		IMqttMessageListener messageListener = subscribeRequest.getMqttMessageListener();
			
		if (StringUtils.isEmpty(topic)) {
			logger.error("bad parameters: subscribe request , topic empty");
			mqttSubscribe.onFailure(null, new NullPointerException("subscribe request , topic empty"));
			return;
		}
			
		try {
			if (subscribeRequest.isSubscribe()) {
				mqttAsyncClient.subscribe(topic, qos, null, mqttSubscribe, messageListener);
			} else {
				mqttAsyncClient.unsubscribe(topic, null, mqttSubscribe);
			}
		} catch (Exception e) {
			logger.error("subsribe request error! ", e);
			mqttSubscribe.onFailure(null, new MqttThrowable(e.getMessage()));
		}
	}
	
}

