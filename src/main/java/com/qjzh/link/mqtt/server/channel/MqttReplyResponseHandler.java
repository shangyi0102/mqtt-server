package com.qjzh.link.mqtt.server.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.PublishResponse;

@Component
public class MqttReplyResponseHandler implements IReplyResponseHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void onResponse(PublishResponse response) {
		logger.info("PublishResponse : {}", JSON.toJSONString(response));
	}
}
