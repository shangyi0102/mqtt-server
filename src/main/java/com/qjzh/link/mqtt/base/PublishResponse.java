package com.qjzh.link.mqtt.base;

public abstract class PublishResponse{
	
	//OK=正常返回data
	public static final Integer OK = 200;
	//request生成的messageId
	private String msgId;
	
	private Object data;
	
	private Integer status = OK;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}

