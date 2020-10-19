package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.callback.RequestMessageListener;
import com.qjzh.link.mqtt.server.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.server.interim.MqttSubscribe;

public class SubscribeMqttNet extends AbsMqttNet {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

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
	
	public void addSubscribeRequest(SubscribeRequest subscribeRequest) throws MqttInvokeException {
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

	@Override
	public void connectSuccess() {
		for (SubscribeRequest subscribeRequest : subscribeRequests) {
			try {
				if (subscribeRequest.isReply()) {
					subscribeReply(subscribeRequest);
				}else{
					subscribe(subscribeRequest);
				}
			} catch (MqttInvokeException ex) {
				logger.error("订阅主题失败!", ex);
			}
		}
	}
	
	
	private void subscribe(SubscribeRequest subscribeRequest) throws MqttInvokeException {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				requestMessageListener, subscribeListener);
		mqttSubscribe.subscribe();
	}
	
	private void subscribeReply(SubscribeRequest subscribeRequest) throws MqttInvokeException {
		MqttSubscribe mqttSubscribe = new MqttSubscribe(this, subscribeRequest, 
				replyMessageListener, subscribeListener);
		mqttSubscribe.subscribe();
	}
	
}
