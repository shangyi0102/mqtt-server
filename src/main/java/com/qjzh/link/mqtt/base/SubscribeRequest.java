package com.qjzh.link.mqtt.base;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

public interface SubscribeRequest {

	public String getTopic();
	
	public int getQos();
	
	public boolean isSubscribe();
	
	public IMqttMessageListener getMqttMessageListener();
	
}
