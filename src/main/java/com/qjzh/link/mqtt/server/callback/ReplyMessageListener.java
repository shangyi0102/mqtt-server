package com.qjzh.link.mqtt.server.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.channel.IOnRpcResponseHandle;
import com.qjzh.link.mqtt.model.ResponseModel;
import com.qjzh.link.mqtt.server.MqttPublishRpc;
import com.qjzh.link.mqtt.server.response.GeneralPublishResponse;
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
	
	
	private static Map<String, MqttPublishRpc> RPC_REPLY = new ConcurrentHashMap<>(64);
	@Autowired
	private IOnRpcResponseHandle onRpcResponseHandle;
	
	public ReplyMessageListener(IOnRpcResponseHandle onRpcResponseHandle) {
		this.onRpcResponseHandle = onRpcResponseHandle;
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//logger.debug("response reply topic:{}", replyTopic);
		int qos = message.getQos();
		String payload = new String(message.getPayload(), "UTF-8");
		payload = StringUtils.trim(payload);
		logger.info("receive topic = [{}], msg = [{}]", topic, payload);
		if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(payload) 
				|| !JSON.isValid(payload)) {
			return;
		}
		
		ResponseModel response = JSON.parseObject(payload, ResponseModel.class);
		String msgId = response.getMsgId();
		if (StringUtils.isEmpty(msgId)) {
			return;
		}
		
		GeneralPublishResponse publishResponse = new GeneralPublishResponse();
		publishResponse.setReplyTopic(topic);
		publishResponse.setQos(qos);
		publishResponse.setMsgId(msgId);
		publishResponse.setData(response);
		
		String matchId = MqttUtils.getMatchId(topic, msgId);
		//RPC同步应答则通知同步调用器
		if (RPC_REPLY.remove(matchId) != null) {
			MqttPublishRpc.received(publishResponse);
		}
		
		onRpcResponseHandle.onResponse(publishResponse);
		
	}
	
	public static void putPublishRpc(MqttPublishRpc publishRpc){
		RPC_REPLY.put(publishRpc.getMatchId(), publishRpc);
	}
}

