package com.qjzh.link.mqtt.server.request;

import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.server.response.GeneralPublishResponse;
/**
 * @DESC: 发布请求参数
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午5:32:18
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class GeneralPublishRequest implements PublishRequest {
	
	private String msgId;
	
	private String topic;
	
	private boolean isRPC = true;
	
	private String replyTopic;
	
	private int qos = 0;
	
	private Object payload;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public boolean isRPC() {
		return isRPC;
	}

	public void setRPC(boolean isRPC) {
		this.isRPC = isRPC;
	}

	public String getReplyTopic() {
		return replyTopic;
	}

	public void setReplyTopic(String replyTopic) {
		this.replyTopic = replyTopic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}
	
	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	@Override
	public void check() throws IllegalStateException {
		
	}

	@Override
	public Class<GeneralPublishResponse> getResponseClass() {
		return GeneralPublishResponse.class;
	}

}

