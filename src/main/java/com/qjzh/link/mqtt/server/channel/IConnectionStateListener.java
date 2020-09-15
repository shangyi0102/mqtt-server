package com.qjzh.link.mqtt.server.channel;

public interface IConnectionStateListener {
	void onConnectFail(String paramString);

	void onConnected();

	void onDisconnect();
}
