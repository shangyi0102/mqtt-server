package com.qjzh.link.mqtt.server.channel;

import org.springframework.stereotype.Component;

import com.qjzh.link.mqtt.base.PublishResponse;

@Component
public class MqttReplyResponseHandler implements IReplyResponseHandler {

	@Override
	public void onResponse(PublishResponse response) {
		
	}
}
