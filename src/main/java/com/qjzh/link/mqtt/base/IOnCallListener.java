package com.qjzh.link.mqtt.base;

public interface IOnCallListener {
	
	void onSuccess(PublishRequest paramARequest, PublishResponse paramAResponse);

	void onFailed(PublishRequest paramARequest, QJError paramAError);

	boolean needUISafety();
}

