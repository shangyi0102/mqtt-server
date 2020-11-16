package com.qjzh.link.mqtt.server.callback;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.model.RequestData;
import com.qjzh.link.mqtt.model.ResponseData;
import com.qjzh.link.mqtt.model.device.Device;
import com.qjzh.link.mqtt.model.device.DeviceEvent;
import com.qjzh.link.mqtt.model.device.DeviceSubject;
import com.qjzh.link.mqtt.model.product.PEvent;
import com.qjzh.link.mqtt.model.product.Product;
import com.qjzh.link.mqtt.server.ProductDeviceMemory;
import com.qjzh.link.mqtt.server.PublishMqttNet;
import com.qjzh.link.mqtt.server.channel.IPushRequestHandler;

/**
 * @DESC: mqtt 请求回调监听
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:26:10
 * @version 1.0.0
 * @copyright www.7g.com
 */
@Component
public class RequestMessageListener implements IMqttMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PublishMqttNet publishMqttNet;
	@Autowired
	private IPushRequestHandler pushRequestHandler;
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		int qos = message.getQos();
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) || !JSON.isValid(payload)) {
			logger.error("parameter is invalid!!! topic = [{}], msg = [{}]", topic, payload);
			return;
		}
		
		logger.info("topic = [{}], msg = [{}]", topic, payload);
		//json 字符串转请求对象
		RequestData requestData = JSON.parseObject(payload, RequestData.class);
		//获取请求消息ID
		String msgId = requestData.getMsgId();
		JSONObject jsonParams = requestData.getParams();
		if (StringUtils.isEmpty(msgId) || null == jsonParams || jsonParams.isEmpty()) {
			logger.error("request msgId or params is null!!!");
			return;
		}
		
		String [] split = topic.split("/");
		//产品标识符
		String productId = split[2];
		//设备标识符
		String deviceId = split[3];
		//事件标识符
		String eventIdent = null;
		if (split[4].equals("event")) eventIdent = split[5];
		
		Device device = ProductDeviceMemory.getDevice(productId, deviceId);
		if (device == null) {
			logger.error("device information does not exist! prod=[{}],device=[{}]", productId, deviceId);
			return ;
		}
		
		DeviceSubject devSubject = new DeviceSubject();
		devSubject.setSn(deviceId);
		devSubject.setRecvTime(System.currentTimeMillis());
		
		//判断产品属性结构信息是否合法
		if (split[4].equals("property")) {
			if (!validateProps(productId, jsonParams, device)) {
				logger.error("Illegal product properties! prod=[{}], event=[{}]", productId);
				return ;
			}
			devSubject.setData(jsonParams);
		}else{
			//判断产品事件标识符和事件结构是否合法
			if (!validateEvent(productId, eventIdent, jsonParams, device)) {
				logger.error("Illegal product event! prod=[{}], event=[{}]", productId, eventIdent);
				return ;
			}
			DeviceEvent devEvent = new DeviceEvent();
			devEvent.setEventIdent(eventIdent);
			devEvent.setEventData(jsonParams);
			
			devSubject.setEvents(devEvent);
		}
		
		PushRequest pushRequest = new PushRequest(device.getTenantIdent(), device.getAppIdent(), productId);
		pushRequest.setDevSubject(devSubject);
		
		ResponseData responseData = new ResponseData();
		responseData.setMsgId(msgId);
		
		try {
			//请求调用处理
			JSONObject jsonData = pushRequestHandler.onCommand(pushRequest); 
			if (jsonData != null) responseData.setData(jsonData);
			
		} catch (Exception ex) {
			logger.error("device ---> server, 处理设备上报消息异常!", ex);
			responseData.setCode(500);
			responseData.setMessage("消息存储处理失败!");
		}
		
		PublishResponse response = new PublishResponse();
		response.setData(responseData);
		response.setQos(qos);
		response.setReplyTopic(topic + "_reply");
		
		try {
			publishMqttNet.publishReply(response);
		} catch (MqttInvokeException ex) {
			logger.error("server ---> device, 发送应答消息异常!", ex);
		}
		
	}
	
	/**
	 * DESC: 校验产品事件标识符是否合法
	 * @param productId
	 * @param eventId
	 * @param device
	 * @return true=合法 ;false=非法
	 */
	private boolean validateEvent(String productId, String eventId, 
			JSONObject eventData, Device device){
		/*
		 * 1. 校验产品事件标识符
		 */
		//获取产品信息
		Product product = ProductDeviceMemory.getProduct(device.getTenantIdent(), 
				device.getAppIdent(), productId);
		
		if (product == null) return false;
		
		PEvent pEvent = product.getStruct().getEventByIdent(eventId);
		
		return (pEvent != null);
	}
	
	/**
	 * DESC: 校验产品属性结构是否合法
	 * @param productId
	 * @param eventId
	 * @param device
	 * @return true=合法 ;false=非法
	 */
	private boolean validateProps(String productId, JSONObject propData, Device device){
		/*
		 * 1. 校验产品事件标识符
		 */
		//获取产品信息
		Product product = ProductDeviceMemory.getProduct(device.getTenantIdent(), 
				device.getAppIdent(), productId);
		
		if (product == null) return false;
		
		return true;
	}
	
}

