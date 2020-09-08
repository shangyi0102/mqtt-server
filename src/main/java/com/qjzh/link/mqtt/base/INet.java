package com.qjzh.link.mqtt.base;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.channel.IOnSubscribeRrpcListener;

public interface INet {
	
	void init();

	AbsQJSend send(QJRequest qJRequest, IOnCallListener onCallListener);
	
	void setConnectState(ConnectState connectState);
	
	ConnectState getConnectState();
	
	void retry(AbsQJSend send);

	void subscribe(String topic, IMqttMessageListener mqttMessageListener, 
			IOnSubscribeListener listener);

	void unSubscribe(String topic, IOnSubscribeListener onSubscribeListener);

	void subscribeRpc(String topic, IOnSubscribeRrpcListener onSubscribeRrpcListener);
	
	void destroy();

	
}
