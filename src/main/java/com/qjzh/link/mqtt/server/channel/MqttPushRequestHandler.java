package com.qjzh.link.mqtt.server.channel;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.qjzh.link.mqtt.model.PushRequest;

@Component
public class MqttPushRequestHandler implements IPushRequestHandler{

	@Override
	public JSONObject onCommand(PushRequest pushRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
