package com.qjzh.link.mqtt.base;

public class QJError {
	
	public static final String ERR_DOMAIN_NAME_ALINK = "alinkErrorDomain";
	public static final int AKErrorSuccess = 0;
	
	public static final int AKErrorUnknownError = 4201;
	
	public static final int AKErrorInvokeNetError = 4101;
	
	public static final int AKErrorInvokeServerError = 4102;
	
	public static final int AKErrorServerBusinessError = 4103;
	
	public static final int AKErrorLoginTokenIllegalError = 4001;
	
	private String domain = "alinkErrorDomain";

	private int code;
	private String msg;
	private String subDomain;

	public void setDomain(String domain) {
		this.domain = domain;
	}

	private int subCode;

	public void setCode(int code) {
		this.code = code;
	}

	private String subMsg;
	private Object originResponseObj;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}

	public void setSubCode(int subCode) {
		this.subCode = subCode;
	}

	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
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

	public String getSubDomain() {
		return this.subDomain;
	}

	public int getSubCode() {
		return this.subCode;
	}

	public String getSubMsg() {
		return this.subMsg;
	}

	public Object getOriginResponseObject() {
		return this.originResponseObj;
	}

	public void setOriginResponseObject(Object originResponseObj) {
		this.originResponseObj = originResponseObj;
	}
}

