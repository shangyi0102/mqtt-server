package com.qjzh.link.mqtt.server.callback;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

/**
 * @DESC: mqtt Reply回调监听
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:26:10
 * @version 1.0.0
 * @copyright www.7g.com
 */
@Component
public class ReplyMessageListener implements IMqttMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IReplyResponseHandler replyResponseHandler;
	
	public ReplyMessageListener(IReplyResponseHandler replyResponseHandler) {
		this.replyResponseHandler = replyResponseHandler;
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//logger.debug("response reply topic:{}", topic);
		int qos = message.getQos();
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) || !JSON.isValid(payload)) {
			logger.error("parameter is invalid!!! topic = [{}], msg = [{}]", topic, payload);
			return;
		}
		
		logger.info("response reply topic = [{}], msg = [{}]", topic, payload);
		
		ResponseData responseData = JSON.parseObject(payload, ResponseData.class);
		String msgId = responseData.getMsgId();
		if (StringUtils.isEmpty(msgId)) {
			logger.error("request msgId is null!!!");
			return;
		}
		
		String [] split = topic.split("/");
		//产品标识符
		String productId = split[2];
		//设备标识符
		String deviceId = split[3];
		//服务标识符
		String serviceId = null;
		if (split[4].equals("service")) serviceId = split[5];
		//检查设备信息是否已注册
		Device device = ProductDeviceMemory.getDevice(productId, deviceId);
		if (device == null) {
			logger.error("this device information does not exist!");
			return ;
		}
		
		//校验产品服务标识符 是否合法
		if (!validateServiceIdent(productId, serviceId, device)) {
			logger.error("product service identifier is invalid!");
			return ;
		} 
		
		PublishResponse publishResponse = new PublishResponse();
		publishResponse.setReplyTopic(topic);
		publishResponse.setQos(qos);
		publishResponse.setMsgId(msgId);
		publishResponse.setData(responseData);
		
		String matchId = MqttUtils.getMatchId(topic, msgId);
		//RPC同步应答则通知同步调用器
		if (MqttRpcExtractor.get(matchId) != null) {
			MqttPublishRpc.received(publishResponse);
		}
		
		replyResponseHandler.onResponse(publishResponse);
		
	}
	
	/**
	 * DESC: 校验产品服务标识符是否合法
	 * @param productId
	 * @param serviceId
	 * @param device
	 * @return true=合法 ;false=非法
	 */
	private boolean validateServiceIdent(String productId, String serviceId, Device device) {
		
		if (StringUtils.isEmpty(serviceId)) return true;
		/*
		 * 1. 校验产品服务标识符
		 */
		//获取产品信息
		Product product = ProductDeviceMemory.getProduct(device.getTenantIdent(), 
				device.getAppIdent(), productId);
		
		if (product == null) return false;
		
		PService pService = product.getStruct().getServiceByIdent(serviceId);
		
		return (pService != null);
	} 
}

