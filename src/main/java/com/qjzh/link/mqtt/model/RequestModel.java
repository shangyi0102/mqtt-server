package com.qjzh.link.mqtt.model;

import com.alibaba.fastjson.JSON;

public class RequestModel<T> {

	private String msgId;

	private Long timestamp;

	private T params;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public T getParams() {
		return params;
	}

	public void setParams(T params) {
		this.params = params;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}