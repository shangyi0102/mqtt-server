package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.qjzh.link.mqtt.base.Constants;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReplySubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReportSubscribeRequest;

public class ThingsInitData {

	private int qos;
	
	private String prodPlace;
	
	private String devPlace;
	
	private String eventPlace;
	
	private String servicePlace;
	
	private List<SubscribeRequest> subscribeRequest = new ArrayList<>();
	
	public ThingsInitData(String prodPlace, String devPlace, 
			String eventPlace, String servicePlace, int qos){
		this.prodPlace = prodPlace;
		this.devPlace = devPlace;
		this.eventPlace = eventPlace;
		this.servicePlace = servicePlace;
		this.qos = qos;
	}
	
	@PostConstruct
	public void init(){
		
		ReportSubscribeRequest propReportReq = new ReportSubscribeRequest();
		propReportReq.setTopic(Constants.PROP_REPORT.replace(Constants.REPLACE_PRODUCTID, prodPlace).replace(Constants.REPLACE_DEVICEID, devPlace));
		propReportReq.setQos(qos); 
		
		ReportSubscribeRequest eventReportReq = new ReportSubscribeRequest();
		eventReportReq.setTopic(Constants.EVENT_REPORT.replace(Constants.REPLACE_PRODUCTID, prodPlace)
				.replace(Constants.REPLACE_DEVICEID, devPlace).replace(Constants.REPLACE_EVENTID, eventPlace));
		eventReportReq.setQos(qos);
		
		ReplySubscribeRequest propSetReplyReq = new ReplySubscribeRequest();
		propSetReplyReq.setTopic(Constants.PROP_SET_REPLY.replace(Constants.REPLACE_PRODUCTID, prodPlace).replace(Constants.REPLACE_DEVICEID, devPlace));
		propSetReplyReq.setQos(qos); 
		
		ReplySubscribeRequest propReadReplyReq = new ReplySubscribeRequest();
		propReadReplyReq.setTopic(Constants.PROP_READ_REPLY.replace(Constants.REPLACE_PRODUCTID, prodPlace).replace(Constants.REPLACE_DEVICEID, devPlace));
		propReadReplyReq.setQos(qos);
		
		ReplySubscribeRequest serviceInvokeReplyReq = new ReplySubscribeRequest();
		serviceInvokeReplyReq.setTopic(Constants.SERVICE_INVOKE_REPLY.replace(Constants.REPLACE_PRODUCTID, prodPlace)
				.replace(Constants.REPLACE_DEVICEID, devPlace).replace(Constants.REPLACE_SERVICEID, servicePlace));
		serviceInvokeReplyReq.setQos(qos);
		
		subscribeRequest.add(propReportReq);
		subscribeRequest.add(eventReportReq);
		subscribeRequest.add(propSetReplyReq);
		subscribeRequest.add(propReadReplyReq);
		subscribeRequest.add(serviceInvokeReplyReq);
		
	}

	public List<SubscribeRequest> getSubscribeRequest() {
		return subscribeRequest;
	}
	
}
