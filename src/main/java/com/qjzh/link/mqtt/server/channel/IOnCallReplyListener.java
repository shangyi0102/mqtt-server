package com.qjzh.link.mqtt.server.channel;

import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishResponse;

public interface IOnCallReplyListener {
	
	void onSuccess(PublishResponse publishResponse);

	void onFailed(PublishResponse publishResponse, MqttError error);
}

