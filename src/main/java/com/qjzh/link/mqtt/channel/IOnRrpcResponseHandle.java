package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.QJResponse;

public interface IOnRrpcResponseHandle {
	
	void onRrpcResponse(String paramString, QJResponse paramAResponse);
}

