package com.qjzh.link.mqtt.server.request;

import com.qjzh.link.mqtt.base.SubscribeRequest;

/**
 * @DESC: 订阅请求参数
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午2:28:17
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class GeneralSubscribeRequest implements SubscribeRequest {
	
	//订阅主题
	private String topic;
	//服务质量
	private int qos = 0;
	//是否取消订阅
	private boolean isSubscribe = true;
	
	private boolean isReply = false;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isSubscribe() {
		return isSubscribe;
	}

	@Override
	public boolean isReply() {
		return isReply;
	}
	
	public void setSubscribe(boolean isSubscribe) {
		this.isSubscribe = isSubscribe;
	}
	
	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	@Override
	public void check() throws IllegalStateException {
		// TODO Auto-generated method stub
		
	}

	
	
}
