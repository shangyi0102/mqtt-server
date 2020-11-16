package com.qjzh.link.mqtt.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @DESC: 数据请求结构
 * @author LIU.ZHENXING
 * @date 2020年8月19日下午2:11:42
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class RequestData {

	private String msgId;

	private Long timestamp;

	private JSONObject params;

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

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}