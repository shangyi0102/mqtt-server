package com.qjzh.link.mqtt.base;

public class SubscribeRequest {

	// 订阅主题
	private String topic;
	// 服务质量
	private int qos = 0;
	// 是否取消订阅
	private boolean isSubscribe = true;

	private boolean isReply = false;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isSubscribe() {
		return isSubscribe;
	}

	public boolean isReply() {
		return isReply;
	}

	public void setSubscribe(boolean isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	public void check() throws IllegalStateException {
		// TODO Auto-generated method stub

	}
}
