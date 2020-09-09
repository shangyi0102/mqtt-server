package com.qjzh.link.mqtt.server.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qjzh.link.mqtt.model.ResponseModel;
import com.qjzh.link.mqtt.server.MqttPublish;
import com.qjzh.link.mqtt.server.request.GeneralPublishRequest;
import com.qjzh.link.mqtt.utils.MqttProtocolHelper;

/**
 * @DESC: mqtt RPC回调监听
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:26:10
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class RpcMsgCallback implements IMqttMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String replyTopic;
	
	public RpcMsgCallback(String replyTopic) {
		this.replyTopic = replyTopic;
	}
	
	public RpcMsgCallback(String topic, MqttPublish mqttPublish) {
		/*if (messageMap == null) {
			messageMap = new HashMap<>();
		}
		if (mqttSend == null || StringUtils.isEmpty(topic) || mqttSend.getRequest() == null
				|| !(mqttSend.getRequest() instanceof MqttPublishRequest)
				|| StringUtils.isEmpty(((MqttPublishRequest) mqttSend.getRequest()).getMsgId())) {
			return;
		}
		messageMap.put(topic + ",id=" + ((MqttPublishRequest) mqttSend.getRequest()).getMsgId(), mqttSend); */
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		logger.info("topic = [{}], msg = [{}]", topic, payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) 
				|| !JSON.isValid(payload)) {
			return;
		}
		
		ResponseModel response = JSON.parseObject(payload, ResponseModel.class);
		String msgId = response.getMsgId();
		if (StringUtils.isEmpty(msgId)) {
			return;
		}
		
		
		//logger.info("match Id = <" + topic + ",id=" + msgid + ">");
		/*MqttSend mqttSend = messageMap.get(topic + ",id=" + msgid);
		mqttSend.rpcMessageArrived(topic, message);
		messageMap.remove(topic + ",id=" + msgid);*/
	}
}

