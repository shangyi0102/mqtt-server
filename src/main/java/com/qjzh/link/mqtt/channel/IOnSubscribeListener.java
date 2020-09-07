package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.QJError;

public interface IOnSubscribeListener {
	
	void onSuccess(String paramString);

	void onFailed(String paramString, QJError paramAError);

	boolean needUISafety();
	
}
