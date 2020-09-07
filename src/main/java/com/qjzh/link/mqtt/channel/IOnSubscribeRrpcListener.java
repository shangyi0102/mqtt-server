package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.QJRequest;

public interface IOnSubscribeRrpcListener {

	void onSubscribeSuccess(String paramString);

	void onSubscribeFailed(String paramString, QJError paramAError);

	void onReceived(String paramString, QJRequest equest, IOnRrpcResponseHandle paramIOnRrpcResponseHandle);

	void onResponseSuccess(String paramString);

	void onResponseFailed(String paramString, QJError paramAError);

	boolean needUISafety();

}
