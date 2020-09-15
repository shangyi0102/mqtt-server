package com.qjzh.link.mqtt.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.qjzh.link.mqtt.base.QJConstants;
import com.qjzh.link.mqtt.base.SubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReplySubscribeRequest;
import com.qjzh.link.mqtt.server.request.ReportSubscribeRequest;

/**
 * @DESC: 物模型订阅请求数据初始化
 * @author LIU.ZHENXING
 * @date 2020年9月7日上午11:03:15
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class ThingsInitData {

	private int qos = 1;
	//产品占位符
	private String prodPlace = "+";
	//设备占位符
	private String devPlace = "+";
	//事件占位符
	private String eventPlace = "+";
	//服务占位符
	private String servicePlace = "+";
	
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
		propReportReq.setTopic(QJConstants.PROP_REPORT.replace(QJConstants.REPLACE_PRODUCTID, prodPlace).replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propReportReq.setQos(qos); 
		
		ReportSubscribeRequest eventReportReq = new ReportSubscribeRequest();
		eventReportReq.setTopic(QJConstants.EVENT_REPORT.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace).replace(QJConstants.REPLACE_EVENTID, eventPlace));
		eventReportReq.setQos(qos);
		
		ReplySubscribeRequest propSetReplyReq = new ReplySubscribeRequest();
		propSetReplyReq.setTopic(QJConstants.PROP_SET_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace).replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propSetReplyReq.setQos(qos); 
		
		ReplySubscribeRequest propReadReplyReq = new ReplySubscribeRequest();
		propReadReplyReq.setTopic(QJConstants.PROP_READ_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace).replace(QJConstants.REPLACE_DEVICEID, devPlace));
		propReadReplyReq.setQos(qos);
		
		ReplySubscribeRequest serviceInvokeReplyReq = new ReplySubscribeRequest();
		serviceInvokeReplyReq.setTopic(QJConstants.SERVICE_INVOKE_REPLY.replace(QJConstants.REPLACE_PRODUCTID, prodPlace)
				.replace(QJConstants.REPLACE_DEVICEID, devPlace).replace(QJConstants.REPLACE_SERVICEID, servicePlace));
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
