package com.qjzh.link.mqtt.utils;

public class MqttUtils {

	public static String getMatchId(String topic, String msgId){
		return topic + ",msgId=" + msgId;
	}
	
}
