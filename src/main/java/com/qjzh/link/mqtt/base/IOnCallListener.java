package com.qjzh.link.mqtt.base;

public interface IOnCallListener {
	
	void onSuccess(QJRequest paramARequest, QJResponse paramAResponse);

	void onFailed(QJRequest paramARequest, QJError paramAError);

	boolean needUISafety();
}

