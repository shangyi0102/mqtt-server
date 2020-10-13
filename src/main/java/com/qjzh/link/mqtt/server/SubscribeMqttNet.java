package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.callback.RequestMessageListener;
import com.qjzh.link.mqtt.server.channel.IOnSubscribeListener;

public class SubscribeMqttNet extends AbsMqttNet {

	private List<SubscribeRequest> subscribeRequests;
	
	private IOnSubscribeListener subscribeListener;
	@Autowired
	private RequestMessageListener requestMessageListener;
	@Autowired
	private ReplyMessageListener replyMessageListener;
	
	public SubscribeMqttNet(MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		this("subscribe", initParams, taskScheduler);
	}
	
	public SubscribeMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		this(clientId, initParams, taskScheduler, new ArrayList<SubscribeRequest>());
	}
	
	public SubscribeMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler, @NotNull List<SubscribeRequest> subscribeRequests) {
		super(clientId, initParams, taskScheduler);
		this.subscribeRequests = subscribeRequests;
	}
	
	public void connectComplete(boolean reconnect, String serverURI) {
		super.connectComplete(reconnect, serverURI);
		
		for (SubscribeRequest subscribeRequest : subscribeRequests) {
			if (subscribeRequest.isReply()) {
				subscribeReply(subscribeRequest);
			}else{
				subscribe(subscribeRequest);
			}
		}
	}
	
	public void addSubscribeRequest(SubscribeRequest subscribeRequest) {
		this.subscribeRequests.add(subscribeRequest);
		if (subscribeRequest.isReply()) {
			subscribeReply(subscribeRequest);
		}else{
			subscribe(subscribeRequest);
		}
	} 
	
	public void setRequestMessageListener(RequestMessageListener requestMessageListener) {
		this.requestMessageListener = requestMessageListener;
	}
	
	public void setReplyMessageListener(ReplyMessageListener replyMessageListener) {
		this.replyMessageListener = replyMessageListener;
	}

	private void subscribe(SubscribeRequest subscribeRequest) {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				requestMessageListener, subscribeListener);
		mqttSubscribe.receive();
	}
	
	private void subscribeReply(SubscribeRequest subscribeRequest) {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				replyMessageListener, subscribeListener);
		mqttSubscribe.receive();
	}
	
	
}
