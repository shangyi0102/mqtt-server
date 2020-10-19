package com.qjzh.link.mqtt.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @DESC: 数据响应结构
 * @author LIU.ZHENXING
 * @date 2020年8月19日下午2:18:42
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class ResponseData {

	private String msgId;

	private Long timestamp = System.currentTimeMillis();

	private Integer code = 0;

	private String message = "";

	private JSONObject data;

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

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	

}