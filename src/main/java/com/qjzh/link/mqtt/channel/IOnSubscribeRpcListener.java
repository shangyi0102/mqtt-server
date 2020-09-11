package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishRequest;

public interface IOnSubscribeRpcListener extends IOnSubscribeListener {

	void onSubscribeFailed(String topic, MqttError paramAError);

	void onReceived(String topic, PublishRequest equest, 
			IOnRpcResponseHandle paramIOnRrpcResponseHandle);

	void onResponseSuccess(String topic);

	void onResponseFailed(String topic, MqttError paramAError);


}
