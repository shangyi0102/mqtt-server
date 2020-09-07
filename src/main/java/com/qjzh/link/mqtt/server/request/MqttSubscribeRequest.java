package com.qjzh.link.mqtt.server.request;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

import com.qjzh.link.mqtt.base.QJRequest;

/**
 * @DESC: 订阅请求参数
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午2:28:17
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttSubscribeRequest extends QJRequest {
	
	//订阅主题
	private String topic;
	//是否取消订阅
	private boolean isSubscribe = true;
	
	private IMqttMessageListener mqttMessageListener;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public boolean isSubscribe() {
		return isSubscribe;
	}

	public void setSubscribe(boolean isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public IMqttMessageListener getMqttMessageListener() {
		return mqttMessageListener;
	}

	public void setMqttMessageListener(IMqttMessageListener mqttMessageListener) {
		this.mqttMessageListener = mqttMessageListener;
	}
	
}
