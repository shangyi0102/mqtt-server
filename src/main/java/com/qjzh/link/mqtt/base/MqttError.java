package com.qjzh.link.mqtt.base;

import com.alibaba.fastjson.JSON;

public class MqttError {
	
	public static final int SUCCESS = ErrorCode.SUCCESS_OK;

	private int code;
	
	private String msg;
	
	private String domain;
	
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDomain() {
		return this.domain;
	}

	public int getCode() {
		return this.code;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}

