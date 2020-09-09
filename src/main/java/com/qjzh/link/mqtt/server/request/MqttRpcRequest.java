package com.qjzh.link.mqtt.server.request;

import com.qjzh.link.mqtt.base.PublishRequest;

/**
 * @DESC: RPC 发送请求参数
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午4:29:30
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttRpcRequest implements PublishRequest {
	
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

	@Override
	public int getQos() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMsgId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void check() throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class getResponseClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPayload() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

