package com.qjzh.link.mqtt.server.callback;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.base.QJRequest;
import com.qjzh.link.mqtt.base.QJResponse;
import com.qjzh.link.mqtt.channel.IOnRrpcResponseHandle;
import com.qjzh.link.mqtt.channel.IOnSubscribeRrpcListener;
import com.qjzh.link.mqtt.channel.MqttEventDispatcher;
import com.qjzh.link.mqtt.server.request.MqttPublishRequest;
import com.qjzh.link.mqtt.server.request.MqttRrpcRequest;
import com.qjzh.link.mqtt.channel.ConnectState;

/**
 * @DESC: mqtt默认回调
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午2:53:56
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttDefaulCallback implements MqttCallbackExtended {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, IOnSubscribeRrpcListener> rrpcListenerMap;
	
	private Map<String, IOnSubscribeRrpcListener> rrpcPatternListenerMap;

	private INet mqttNet;
	
	public MqttDefaulCallback(INet mqttNet){
		this.mqttNet = mqttNet;
	}
	
	public void registerRrpcListener(String topic, IOnSubscribeRrpcListener listener) {
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

		if (this.rrpcListenerMap != null && this.rrpcListenerMap.containsKey(topic)) {
			MqttRrpcRequest mqttRrpcRequest = new MqttRrpcRequest();
			mqttRrpcRequest.setTopic(topic);
			mqttRrpcRequest.setPayload(message.getPayload());
			
			IOnSubscribeRrpcListener listener = this.rrpcListenerMap.get(topic);
			handleRrpcRequest(mqttRrpcRequest, listener);
		} else if (this.rrpcPatternListenerMap != null && this.rrpcPatternListenerMap.size() > 0) {

			for (String topicPattern : this.rrpcPatternListenerMap.keySet()) {
				if (isTopicMatchForPattern(topicPattern, topic)) {
					logger.info("match pattern, topicPattern={}", topicPattern);
					MqttRrpcRequest mqttRrpcRequest = new MqttRrpcRequest();
					mqttRrpcRequest.setTopic(topic);
					mqttRrpcRequest.setPayload(message.getPayload());
					IOnSubscribeRrpcListener listener = this.rrpcPatternListenerMap.get(topicPattern);
					handleRrpcRequest(mqttRrpcRequest, listener);
					break;
				}
			}
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.info("deliveryComplete! "
				+ ((token == null || token.getResponse() == null) ? "null" : token.getResponse().getKey()));
	}

	private void handleRrpcRequest(final MqttRrpcRequest request, final IOnSubscribeRrpcListener listener) {
		logger.info("handleRrpcRequest()");
		if (listener == null || request == null)
			return;
		if (listener.needUISafety()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.onReceived(request.getTopic(), request,
							new MqttDefaulCallback.RrpcResponseHandle(request.getTopic(), listener));
				}
			});
		} else {
			listener.onReceived(request.getTopic(), request,
					new RrpcResponseHandle(request.getTopic(), listener));
		}
	}

	private boolean isTopicMatchForPattern(String topicPattern, String topic) {
		if (StringUtils.isEmpty(topicPattern) || StringUtils.isEmpty(topic))
			return false;
		try {
			if (topicPattern.contains("#")) {
				String prefixTopic = topicPattern.split("#")[0];
				if (topic.startsWith(prefixTopic)) {
					return true;
				}
			}
			if (topicPattern.contains("+")) {
				String prefixTopic = topicPattern.split("\\+")[0];
				String endTopic = topicPattern.split("\\+", 2)[1];
				if (topic.startsWith(prefixTopic) && topic.endsWith(endTopic))
					return true;
			}
		} catch (Exception e) {
			logger.error("topic Match For Pattern 异常", e);
		}

		return false;
	}

	private class RrpcResponseHandle implements IOnRrpcResponseHandle {
		private String topic;

		private IOnSubscribeRrpcListener listener;

		public RrpcResponseHandle(String topic, IOnSubscribeRrpcListener listener) {
			this.topic = topic;
			this.listener = listener;
		}

		public void onRrpcResponse(String replyTopic, QJResponse response) {
			logger.info("reply topic = " + replyTopic);
			MqttPublishRequest publishRequest = new MqttPublishRequest();
			publishRequest.setRPC(false) ;
			if (StringUtils.isEmpty(replyTopic)) {
				publishRequest.setTopic(this.topic + "_reply");
			} else {
				publishRequest.setTopic(replyTopic);
			}
			if (response != null && response.data != null) {
				publishRequest.setPayload(response.data);
			}
			mqttNet.asyncSend((QJRequest) publishRequest, new IOnCallListener() {
				public void onSuccess(QJRequest request, QJResponse response) {
					logger.info("publish succ");
					MqttDefaulCallback.RrpcResponseHandle.this.listener
							.onResponseSuccess(MqttDefaulCallback.RrpcResponseHandle.this.topic);
				}

				public void onFailed(QJRequest request, QJError error) {
					logger.info("publish fail");
					MqttDefaulCallback.RrpcResponseHandle.this.listener
							.onResponseFailed(MqttDefaulCallback.RrpcResponseHandle.this.topic, error);
				}

				public boolean needUISafety() {
					return MqttDefaulCallback.RrpcResponseHandle.this.listener.needUISafety();
				}
			});
		}
	}
}

