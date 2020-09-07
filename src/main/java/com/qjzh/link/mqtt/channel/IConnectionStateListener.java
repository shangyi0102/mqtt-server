package com.qjzh.link.mqtt.channel;

public interface IConnectionStateListener {
	void onConnectFail(String paramString);

	void onConnected();

	void onDisconnect();
}
