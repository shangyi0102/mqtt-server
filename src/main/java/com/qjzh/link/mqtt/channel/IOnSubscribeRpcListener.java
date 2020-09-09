package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.PublishRequest;

public interface IOnSubscribeRpcListener extends IOnSubscribeListener {

	void onSubscribeFailed(String topic, QJError paramAError);

	void onReceived(String topic, PublishRequest equest, 
			IOnRrpcResponseHandle paramIOnRrpcResponseHandle);

	void onResponseSuccess(String topic);

	void onResponseFailed(String topic, QJError paramAError);


}
