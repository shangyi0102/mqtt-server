package com.qjzh.link.mqtt.server.request;

import com.qjzh.link.mqtt.base.QJRequest;

/**
 * @DESC: RPC 发送请求参数
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午4:29:30
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttRrpcRequest extends QJRequest {
	
	private String topic;
	
	private String replyTopic;

	public void setTopic(String topic) {
		this.topic = topic;
		this.replyTopic = topic + "_reply";
	}

	public String getTopic() {
		return topic;
	}

	public String getReplyTopic() {
		return replyTopic;
	}
	
}

