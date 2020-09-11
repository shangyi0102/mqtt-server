package com.qjzh.link.mqtt.base;

public interface PublishRequest {

	String getTopic();
	
	public String getReplyTopic();
	
	int getQos();
	
	String getMsgId();
	
	void check() throws IllegalStateException;
	
	Class<?> getResponseClass();

	Object getPayload();
	
}
