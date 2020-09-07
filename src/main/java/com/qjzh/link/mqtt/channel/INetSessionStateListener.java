package com.qjzh.link.mqtt.channel;

public interface INetSessionStateListener {
	
	void onNeedLogin();

	void onSessionEffective();

	void onSessionInvalid();
}
