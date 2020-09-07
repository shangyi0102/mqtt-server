package com.qjzh.link.mqtt.server.callback;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.server.MqttSend;
import com.qjzh.link.mqtt.server.request.MqttPublishRequest;
import com.qjzh.link.mqtt.utils.MqttProtocolHelper;

/**
 * @DESC: mqtt 请求回调监听
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:26:10
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttRequestMsgCallback implements IMqttMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static Map<String, MqttSend> messageMap;
	
	public MqttRequestMsgCallback(String topic, MqttSend mqttSend) {
		if (messageMap == null) {
			messageMap = new HashMap<>();
		}
		if (mqttSend == null || StringUtils.isEmpty(topic) || mqttSend.getRequest() == null
				|| !(mqttSend.getRequest() instanceof MqttPublishRequest)
				|| StringUtils.isEmpty(((MqttPublishRequest) mqttSend.getRequest()).getMsgId())) {
			return;
		}
		messageMap.put(topic + ",id=" + ((MqttPublishRequest) mqttSend.getRequest()).getMsgId(), mqttSend);
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		logger.info("topic = [{}], msg = [{}]", topic, new String(message.getPayload(), "UTF-8"));
		
		if (StringUtils.isEmpty(topic)) {
			return;
		}

		String msgid = MqttProtocolHelper.parseMsgIdFromPayload(message.toString());
		if (StringUtils.isEmpty(msgid) || !messageMap.containsKey(topic + ",id=" + msgid)) {
			return;
		}
		
		logger.info("match Id = <" + topic + ",id=" + msgid + ">");
		MqttSend mqttSend = messageMap.get(topic + ",id=" + msgid);
		mqttSend.rpcMessageArrived(topic, message);
		messageMap.remove(topic + ",id=" + msgid);
	}
}

