package com.qjzh.link.mqtt.utils;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class MqttProtocolHelper {
	
	public static String parseMsgIdFromPayload(String payload) {
		if (StringUtils.isEmpty(payload)) return null;
		JSONObject payloadJson = JSONObject.parseObject(payload);
		return payloadJson.getString("msgId");
	}
}
