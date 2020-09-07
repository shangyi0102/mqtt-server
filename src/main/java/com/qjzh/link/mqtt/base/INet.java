package com.qjzh.link.mqtt.base;

import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.channel.IOnSubscribeRrpcListener;

public interface INet {
	
	void init();

	QJSend asyncSend(QJRequest paramARequest, IOnCallListener paramIOnCallListener);
	
	void setConnectState(ConnectState connectState);
	
	ConnectState getConnectState();
	
	void retry(QJSend paramASend);

	void subscribe(String topic, IOnSubscribeListener paramIOnSubscribeListener);

	void unSubscribe(String topic, IOnSubscribeListener paramIOnSubscribeListener);

	void subscribeRrpc(String topic, IOnSubscribeRrpcListener paramIOnSubscribeRrpcListener);
	
	void destroy();

	
}
