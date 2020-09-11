package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.MqttError;

public interface IOnSubscribeListener {
	
	void onSuccess(String topic);

	void onFailed(String topic, MqttError error);

}
