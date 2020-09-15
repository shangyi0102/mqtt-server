package com.qjzh.link.mqtt.server;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReplySubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReportSubscribeRequest;

@Component
public class ThingsDrivenChannel {

	@Autowired
	private INet mqttNet;
	
	@Autowired
	private ThingsInitData thingsInitData;
	
	@PostConstruct
	public void init(){
		List<SubscribeRequest> subscribeRequests = thingsInitData.getSubscribeRequest();
		for (SubscribeRequest subscribeRequest : subscribeRequests) {
			if (subscribeRequest instanceof ReportSubscribeRequest) {
				mqttNet.subscribe(subscribeRequest);
			}else if(subscribeRequest instanceof ReplySubscribeRequest){
				mqttNet.subscribeReply(subscribeRequest);
			}
		}
	}
}
