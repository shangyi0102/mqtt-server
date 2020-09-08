package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.QJError;

public interface IOnSubscribeListener {
	
	void onSuccess(String topic);

	void onFailed(String topic, QJError error);

	boolean needUISafety();
	
}
