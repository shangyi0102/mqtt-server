package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishRequest;

public interface IOnCallListener {
	
	void onSuccess(PublishRequest publishRequest);

	void onFailed(PublishRequest publishRequest, MqttError error);

}

