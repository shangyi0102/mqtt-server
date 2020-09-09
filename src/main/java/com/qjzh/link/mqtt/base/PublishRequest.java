package com.qjzh.link.mqtt.base;

public interface PublishRequest {

	String getTopic();
	
	int getQos();
	
	String getMsgId();
	
	void check() throws IllegalStateException;
	
	Class<?> getResponseClass();

	Object getPayload();
	
}
