package com.qjzh.link.mqtt.server.channel;

import com.alibaba.fastjson.JSONObject;
import com.qjzh.link.mqtt.server.callback.PushRequest;

public interface IPushRequestHandler {
	
	JSONObject onCommand(PushRequest pushRequest) throws Exception;

}
