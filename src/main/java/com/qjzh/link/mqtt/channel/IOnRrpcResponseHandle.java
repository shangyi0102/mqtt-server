package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.PublishResponse;

public interface IOnRrpcResponseHandle {
	
	void onRrpcResponse(String paramString, PublishResponse paramAResponse);
}

