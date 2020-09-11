package com.qjzh.link.mqtt.base;

public abstract class PublishResponse{
	
	//OK=正常返回data
	public static final Integer OK = ErrorCode.SUCCESS_OK;
	//应答主题
	private String replyTopic;
	//request生成的messageId
	private String msgId;
	
	private Object data;
	
	private int qos = 0;
	
	private Integer status = OK;
	
	private String errorMsg;

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

	public String getReplyTopic() {
		return replyTopic;
	}

	public void setReplyTopic(String replyTopic) {
		this.replyTopic = replyTopic;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}
}

