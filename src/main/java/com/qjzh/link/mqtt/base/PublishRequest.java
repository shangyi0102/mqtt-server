package com.qjzh.link.mqtt.base;

import com.qjzh.link.mqtt.model.RequestData;

public class PublishRequest {

	private String msgId;
	
	private String topic;
	
	private String replyTopic;
	
	private int qos = 0;
	
	private RequestData payload;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
		this.replyTopic = topic + "_reply";
	}

	public String getReplyTopic() {
		return replyTopic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}
	
	public RequestData getPayload() {
		return payload;
	}

	public void setPayload(RequestData payload) {
		this.payload = payload;
	}

	public void check() throws IllegalStateException {
		
	}

	public Class<PublishResponse> getResponseClass() {
		return PublishResponse.class;
	}
	
}
