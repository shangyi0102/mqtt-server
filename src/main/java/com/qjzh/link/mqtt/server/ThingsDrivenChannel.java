package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.qjzh.link.mqtt.base.QJConstants;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.request.GeneralSubscribeRequest;

public class ThingsDrivenChannel {

	private int qos = 1;
	//产品占位符
	private String prodPlace = "+";
	//设备占位符
	private String devPlace = "+";
	//事件占位符
	private String eventPlace = "+";
	//服务占位符
	private String servicePlace = "+";
	@Autowired
	private SubscribeMqttNet subscribeMqttNet;
	
	public ThingsDrivenChannel(String prodPlace, String devPlace, 
			String eventPlace, String servicePlace, int qos){
		this.prodPlace = prodPlace;
		this.devPlace = devPlace;
		this.eventPlace = eventPlace;
		this.servicePlace = servicePlace;
		this.qos = qos;
	}
	
	@PostConstruct
	public void init(){
		//初始化订阅请求数据
		List<SubscribeRequest> subscribeRequests = initSubscribe();
		for (SubscribeRequest subscribeRequest : subscribeRequests) {
			try {
				subscribeMqttNet.addSubscribeRequest(subscribeRequest);
			} catch (MqttInvokeException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<SubscribeRequest> initSubscribe(){
		
		List<SubscribeRequest> subscribeRequests = new ArrayList<>();
		
		GeneralSubscribeRequest propReportReq = new GeneralSubscribeRequest();
		propReportReq.setTopic(QJConstants.PROP_REPORT.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propReportReq.setQos(qos); 
		
		GeneralSubscribeRequest eventReportReq = new GeneralSubscribeRequest();
		eventReportReq.setTopic(QJConstants.EVENT_REPORT.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace).replace(QJConstants.REPLACE_EVENTID, eventPlace));
		eventReportReq.setQos(qos);
		
		GeneralSubscribeRequest propSetReplyReq = new GeneralSubscribeRequest();
		propSetReplyReq.setReply(true);
		propSetReplyReq.setTopic(QJConstants.PROP_SET_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propSetReplyReq.setQos(qos); 
		
		GeneralSubscribeRequest propReadReplyReq = new GeneralSubscribeRequest();
		propSetReplyReq.setReply(true);
		propReadReplyReq.setTopic(QJConstants.PROP_READ_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propReadReplyReq.setQos(qos);
		
		GeneralSubscribeRequest serviceInvokeReplyReq = new GeneralSubscribeRequest();
		propSetReplyReq.setReply(true);
		serviceInvokeReplyReq.setTopic(QJConstants.SERVICE_INVOKE_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace).replace(QJConstants.REPLACE_SERVICEID, servicePlace));
		serviceInvokeReplyReq.setQos(qos);
		
		subscribeRequests.add(propReportReq);
		subscribeRequests.add(eventReportReq);
		subscribeRequests.add(propSetReplyReq);
		subscribeRequests.add(propReadReplyReq);
		subscribeRequests.add(serviceInvokeReplyReq);
		
		return subscribeRequests;
	}
	
}
