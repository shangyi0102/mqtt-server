package com.qjzh.link.mqtt.model;

import java.util.Map;

/**
 * @DESC: 数据响应结构
 * @author LIU.ZHENXING
 * @date 2020年8月19日下午2:18:42
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class ResponseModel {

	private String msgId;

	private Long timestamp;

	private Integer code;

	private String message;

	private Map<String, Object> data;

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

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}