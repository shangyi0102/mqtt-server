package com.qjzh.link.mqtt.server.callback;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.model.RequestModel;
import com.qjzh.link.mqtt.server.PublishMqttNet;
import com.qjzh.link.mqtt.server.channel.IPushRequestHandler;
import com.qjzh.link.mqtt.server.request.GeneralPublishRequest;
import com.qjzh.link.mqtt.server.response.GeneralPublishResponse;

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
		//logger.debug("Request topic:{}", this.topic);
		int qos = message.getQos();
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		logger.info("topic = [{}], msg = [{}]", topic, payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) 
				|| !JSON.isValid(payload)) {
			return;
		}
		
		RequestModel request = JSON.parseObject(payload, RequestModel.class);
		String msgId = request.getMsgId();
		if (StringUtils.isEmpty(msgId)) {
			return;
		}
		
		GeneralPublishRequest publishRequest = new GeneralPublishRequest();
		publishRequest.setMsgId(msgId);
		publishRequest.setPayload(request);
		publishRequest.setTopic(topic);
		publishRequest.setQos(qos);
		
		try {
			Object data = pushRequestHandler.onCommand(publishRequest);
			if (data == null) {
				return;
			}
			
			GeneralPublishResponse response = new GeneralPublishResponse();
			response.setData(data);
			response.setQos(qos);
			response.setReplyTopic(publishRequest.getReplyTopic());
			
			publishMqttNet.publishReply(response);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}

