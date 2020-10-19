package com.qjzh.link.mqtt.server.callback;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.server.channel.ConnectState;
import com.qjzh.link.mqtt.server.channel.IOnSubscribeRpcListener;
import com.qjzh.link.mqtt.server.channel.MqttEventDispatcher;

/**
 * @DESC: mqtt默认回调
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午2:53:56
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class DefaulMessageCallback implements MqttCallbackExtended {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private INet mqttNet;
	
	public DefaulMessageCallback(INet mqttNet){
		this.mqttNet = mqttNet;
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		logger.info("connect Lost ----> ");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		logger.info("connect complete ---->{},{} ", reconnect, serverURI);
		
	}
	
	/*private Map<String, IOnSubscribeRpcListener> rrpcListenerMap;
	
	private Map<String, IOnSubscribeRpcListener> rrpcPatternListenerMap;

	
	public void registerRrpcListener(String topic, IOnSubscribeRpcListener listener) {
		logger.debug("topic = " + topic);

		if (StringUtils.isEmpty(topic) || listener == null) {
			logger.error("params error !");
			return;
		}
		if (this.rrpcListenerMap == null)
			this.rrpcListenerMap = new HashMap<>();
		if (this.rrpcPatternListenerMap == null) {
			this.rrpcPatternListenerMap = new HashMap<>();
		}
		if (topic.contains("#") || topic.contains("+")) {
			logger.debug("pattern topic ");
			this.rrpcPatternListenerMap.put(topic, listener);
		} else {
			this.rrpcListenerMap.put(topic, listener);
		}
	}

	public void connectComplete(boolean reconnect, String serverURI) {
		logger.info("befer connect complete ----> " + serverURI);
		mqttNet.setConnectState(ConnectState.CONNECTED);
		MqttEventDispatcher.getInstance().broadcastMessage(1, null, null, null);
		logger.info("after connect complete ---->");
	}

	public void connectionLost(Throwable cause) {
		logger.error("connection lost!!!", cause);
		mqttNet.setConnectState(ConnectState.DISCONNECTED);
		MqttEventDispatcher.getInstance().broadcastMessage(2, null, null, null);
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.info("topic = [{}], msg = [{}]", topic, new String(message.getPayload(), "UTF-8"));
		try {
			MqttEventDispatcher.getInstance().broadcastMessage(3, topic, message.getPayload(), null);
		} catch (Exception e) {
			logger.error("send broadcastMsg error", e);
		}

	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.info("deliveryComplete! "
				+ ((token == null || token.getResponse() == null) ? "null" : token.getResponse().getKey()));
	}*/

}

