package com.qjzh.link.mqtt.base;


public interface SubscribeRequest {

	public String getTopic();
	
	public int getQos();
	
	public boolean isSubscribe();
	
	void check() throws IllegalStateException;
}
