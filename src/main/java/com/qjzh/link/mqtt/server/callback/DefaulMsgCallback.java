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
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.channel.IOnRrpcResponseHandle;
import com.qjzh.link.mqtt.channel.IOnSubscribeRpcListener;
import com.qjzh.link.mqtt.channel.MqttEventDispatcher;
import com.qjzh.link.mqtt.server.request.GeneralPublishRequest;
import com.qjzh.link.mqtt.server.request.MqttRpcRequest;
import com.qjzh.link.mqtt.channel.ConnectState;

/**
 * @DESC: mqtt默认回调
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午2:53:56
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class DefaulMsgCallback implements MqttCallbackExtended {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, IOnSubscribeRpcListener> rrpcListenerMap;
	
	private Map<String, IOnSubscribeRpcListener> rrpcPatternListenerMap;

	private INet mqttNet;
	
	public DefaulMsgCallback(INet mqttNet){
		this.mqttNet = mqttNet;
	}
	
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

		if (this.rrpcListenerMap != null && this.rrpcListenerMap.containsKey(topic)) {
			MqttRpcRequest mqttRpcRequest = new MqttRpcRequest();
			mqttRpcRequest.setTopic(topic);
			mqttRpcRequest.setPayload(message.getPayload());
			
			IOnSubscribeRpcListener listener = this.rrpcListenerMap.get(topic);
			handleRrpcRequest(mqttRpcRequest, listener);
		} else if (this.rrpcPatternListenerMap != null && this.rrpcPatternListenerMap.size() > 0) {

			for (String topicPattern : this.rrpcPatternListenerMap.keySet()) {
				if (isTopicMatchForPattern(topicPattern, topic)) {
					logger.info("match pattern, topicPattern={}", topicPattern);
					MqttRpcRequest mqttRpcRequest = new MqttRpcRequest();
					mqttRpcRequest.setTopic(topic);
					mqttRpcRequest.setPayload(message.getPayload());
					IOnSubscribeRpcListener listener = this.rrpcPatternListenerMap.get(topicPattern);
					handleRrpcRequest(mqttRpcRequest, listener);
					break;
				}
			}
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.info("deliveryComplete! "
				+ ((token == null || token.getResponse() == null) ? "null" : token.getResponse().getKey()));
	}

	private void handleRrpcRequest(final MqttRpcRequest request, final IOnSubscribeRpcListener listener) {
		logger.info("handleRrpcRequest()");
		if (listener == null || request == null)
			return;
		if (listener.needUISafety()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					listener.onReceived(request.getTopic(), request,
							new DefaulMsgCallback.RrpcResponseHandle(request.getTopic(), listener));
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

		private IOnSubscribeRpcListener listener;

		public RrpcResponseHandle(String topic, IOnSubscribeRpcListener listener) {
			this.topic = topic;
			this.listener = listener;
		}

		public void onRrpcResponse(String replyTopic, PublishResponse publishResponse) {
			logger.info("reply topic = " + replyTopic);
			GeneralPublishRequest publishRequest = new GeneralPublishRequest();
			publishRequest.setRPC(false) ;
			if (StringUtils.isEmpty(replyTopic)) {
				publishRequest.setTopic(this.topic + "_reply");
			} else {
				publishRequest.setTopic(replyTopic);
			}
			if (publishResponse != null && publishResponse.data != null) {
				publishRequest.setPayload(publishResponse.data);
			}
			mqttNet.send((PublishRequest) publishRequest, new IOnCallListener() {
				public void onSuccess(PublishRequest publishRequest, PublishResponse publishResponse) {
					logger.info("publish succ");
					DefaulMsgCallback.RrpcResponseHandle.this.listener
							.onResponseSuccess(DefaulMsgCallback.RrpcResponseHandle.this.topic);
				}

				public void onFailed(PublishRequest publishRequest, QJError error) {
					logger.info("publish fail");
					DefaulMsgCallback.RrpcResponseHandle.this.listener
							.onResponseFailed(DefaulMsgCallback.RrpcResponseHandle.this.topic, error);
				}

				public boolean needUISafety() {
					return DefaulMsgCallback.RrpcResponseHandle.this.listener.needUISafety();
				}
			});
		}
	}
}

