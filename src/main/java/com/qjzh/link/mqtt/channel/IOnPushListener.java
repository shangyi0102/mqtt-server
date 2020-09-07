package com.qjzh.link.mqtt.channel;

public interface IOnPushListener {
	void onCommand(String paramString, byte[] paramArrayOfbyte);

	boolean shouldHandle(String paramString);
}
