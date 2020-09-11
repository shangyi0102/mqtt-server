package com.qjzh.link.mqtt.channel;

import org.springframework.stereotype.Component;

import com.qjzh.link.mqtt.base.PublishRequest;

@Component
public class MqttPushRequestHandle implements IOnPushRequestHandle{

	@Override
	public Object onCommand(PublishRequest publishRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
