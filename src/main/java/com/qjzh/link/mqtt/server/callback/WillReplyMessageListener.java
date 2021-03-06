package com.qjzh.link.mqtt.server.callback;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.model.ResponseData;
import com.qjzh.link.mqtt.model.device.Device;
import com.qjzh.link.mqtt.model.product.PService;
import com.qjzh.link.mqtt.model.product.Product;
import com.qjzh.link.mqtt.server.ProductDeviceMemory;
import com.qjzh.link.mqtt.server.channel.IReplyResponseHandler;
import com.qjzh.link.mqtt.server.interim.MqttPublishRpc;
import com.qjzh.link.mqtt.server.interim.MqttRpcExtractor;
import com.qjzh.link.mqtt.utils.MqttUtils;
import com.qjzh.tools.core.map.MapUtil;

/**
 * @DESC: mqtt 设备遗愿Reply回调监听
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:26:10
 * @version 1.0.0
 * @copyright www.7g.com
 */

public class WillReplyMessageListener implements IMqttMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IReplyResponseHandler replyResponseHandler;
	
	public WillReplyMessageListener(IReplyResponseHandler replyResponseHandler) {
		this.replyResponseHandler = replyResponseHandler;
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//logger.debug("response reply topic:{}", topic);
		int qos = message.getQos();
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		logger.info("response reply topic = [{}], msg = [{}]", topic, payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) 
				|| !JSON.isValid(payload)) {
			return;
		}
		
		ResponseData response = JSON.parseObject(payload, ResponseData.class);
		String msgId = response.getMsgId();
		Map<String, Object> mapData = response.getData();
		if (StringUtils.isEmpty(msgId) || MapUtil.isEmpty(mapData)) {
			logger.error("response reply data error! msg = [{}]", payload);
			return;
		}
		
		String [] split = topic.split("/");
		//产品标识符
		String productId = split[1];
		//设备标识符
		String deviceId = split[2];
		//通道
		String channel = split[3];
		//服务标识符
		String serviceId = split[4];
		
		//校验产品，设备，服务标识符 是否合法
		if (!validateDevice(productId, deviceId, channel, serviceId)) return ;
		
		PublishResponse publishResponse = new PublishResponse();
		publishResponse.setReplyTopic(topic);
		publishResponse.setQos(qos);
		publishResponse.setMsgId(msgId);
		publishResponse.setData(response);
		
		String matchId = MqttUtils.getMatchId(topic, msgId);
		//RPC同步应答则通知同步调用器
		if (MqttRpcExtractor.get(matchId) != null) {
			MqttPublishRpc.received(publishResponse);
		}
		
		replyResponseHandler.onResponse(publishResponse);
		
	}
	
	private boolean validateDevice(String productId, String deviceId, 
			String channel, String serviceId) {
		/*
		 * 1. 验证设备是否存在
		 */
		Device device = ProductDeviceMemory.getDevice(productId, deviceId);
		if (device == null) {
			return false;
		}
		
		//如果不为服务应答则直接返回
		if (!channel.equals("service")) {
			return true;
		}
		/*
		 * 2. 获取产品信息
		 */
		Product product = ProductDeviceMemory.getProduct(device.getTenantIdent(), 
				device.getAppIdent(), productId);
		if (product == null) {
			return false;
		}
		
		PService pService = product.getStruct().getServiceByIdent(serviceId);
		
		if (pService == null) {
			return false;
		}
		return true;
	} 
}

