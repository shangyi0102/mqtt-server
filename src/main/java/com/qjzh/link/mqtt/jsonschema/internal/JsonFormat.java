package com.qjzh.link.mqtt.jsonschema.internal;


public enum JsonFormat {
	
	DATE("date"),
	DATETIME("date-time"),
	TIME("time"),
	//DATE_C("date-c"),
	DATETIME_C("datetime-c"),
	EMAIL("email"),
	HOSTNAME("hostname");

	private String code;

	JsonFormat(String code){
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
