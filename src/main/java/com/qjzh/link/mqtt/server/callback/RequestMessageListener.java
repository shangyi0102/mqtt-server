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
import com.qjzh.link.mqtt.model.PushRequest;
import com.qjzh.link.mqtt.model.RequestData;
import com.qjzh.link.mqtt.model.ResponseData;
import com.qjzh.link.mqtt.model.device.Device;
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
		if (StringUtils.isEmpty(msgId)) {
			logger.error("request msgId is null!!!");
			return;
		}
		
		String [] split = topic.split("/");
		//产品标识符
		String prodIdent = split[2];
		//设备标识符
		String devSn = split[3];
		//通道
		String channel = split[4];
		//事件标识符
		String eventIdent = split[5];
		
		//校验产品，设备，服务标识符 是否合法
		if (!validateDevice(prodIdent, devSn, channel, eventIdent)) return;
		
		PushRequest pushRequest = new PushRequest("", "", prodIdent, devSn);
		pushRequest.setRequestData(requestData);
		
		ResponseData responseData = new ResponseData();
		responseData.setMsgId(msgId);
		
		try {
			//请求调用处理
			JSONObject payloadData = pushRequestHandler.onCommand(pushRequest);
			responseData.setData(payloadData);
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
	
	
	private boolean validateDevice(String productId, String deviceId, 
			String channel, String eventId) {
		/*
		 * 1. 验证设备是否存在
		 */
		Device device = ProductDeviceMemory.getDevice(productId, deviceId);
		if (device == null) {
			return false;
		}
		
		/*
		 * 2. 获取产品信息
		 */
		Product product = ProductDeviceMemory.getProduct(device.getTenantIdent(), 
				device.getAppIdent(), productId);
		if (product == null) {
			return false;
		}
		//如果不为事件应答则直接返回
		if (!channel.equals("event")) {
			return true;
		}
		
		PEvent pEvent = product.getStruct().getEventByIdent(eventId);
		
		if (pEvent == null) {
			return false;
		}
		return true;
	} 
	
}

