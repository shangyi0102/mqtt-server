package com.qjzh.link.mqtt.server.channel;

public interface INetSessionStateListener {
	
	void onNeedLogin();

	void onSessionEffective();

	void onSessionInvalid();
}
