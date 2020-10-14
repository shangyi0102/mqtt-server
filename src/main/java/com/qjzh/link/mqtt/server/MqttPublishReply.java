package com.qjzh.link.mqtt.server;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.AbsMqttDriven;
import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.IMqttNet;
import com.qjzh.link.mqtt.base.MqttError;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.exception.BadNetworkException;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.channel.ConnectState;
import com.qjzh.link.mqtt.server.channel.IOnCallReplyListener;

/**
 * @DESC: mqtt发布应答
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublishReply extends AbsMqttDriven implements IMqttActionListener {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//请求应答
	private PublishResponse publishResponse;
	 
	private IOnCallReplyListener callReplyListener = new CallReplyListenerInternal();

	public MqttPublishReply(IMqttNet mqttNet, PublishResponse publishResponse, 
			IOnCallReplyListener callReplyListener) {
		super(mqttNet);
		this.publishResponse = publishResponse;
		if (callReplyListener != null) {
			this.callReplyListener = callReplyListener;
		}
	}

	public PublishResponse getPublishResponse() {
		return publishResponse;
	}

	public void setPublishResponse(PublishResponse publishResponse) {
		this.publishResponse = publishResponse;
	}

	public IOnCallReplyListener getCallReplyListener() {
		return callReplyListener;
	}

	public void send() throws MqttInvokeException {
		
		if (null == this.publishResponse) {
			onFailure(null, new IllegalArgumentException("bad parameters: publishRequest"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
		}
		
		if (!(getMqttNet() instanceof AbsMqttNet)) {
			onFailure(null, new IllegalArgumentException("bad parameter: need AbsMqttNet"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
		}
		
		AbsMqttNet mqttNet = (AbsMqttNet)getMqttNet();
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();
		if (null == mqttAsyncClient || !mqttAsyncClient.isConnected()) {
			onFailure(null, new BadNetworkException());
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "mqtt server not connected!");
		}
		
		Object objPayload = publishResponse.getData();
		String topic = publishResponse.getReplyTopic();
		int qos = publishResponse.getQos();

		if (StringUtils.isEmpty(topic) || objPayload == null) {
			onFailure(null, new NullPointerException("bad parameters: topic or payload is empty"));
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, "bad parameters");
		}
		try {
			byte[] payload = null;
			if (objPayload instanceof String) {
				payload = objPayload.toString().getBytes("UTF-8");
			} else if (objPayload instanceof byte[]) {
				payload = (byte[]) objPayload;
			} else {
				payload = JSON.toJSONString(objPayload).getBytes("UTF-8");
			}
			
			MqttMessage message = new MqttMessage(payload);
			message.setQos(qos);

			mqttAsyncClient.publish(topic, message, null, this).waitForCompletion(mqttNet.getTimeToWait());
			
		} catch (Exception ex) {
			logger.error("send publish error! ", ex);
			onFailure(null, ex);
			throw new MqttInvokeException(ErrorCode.RPC_CLIENT_HANDLE, ex.getMessage());
		}
		
	}
	
	public void onSuccess(IMqttToken asyncActionToken) {
		this.callReplyListener.onSuccess(this.publishResponse);
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
		
		String msg = (null != exception) ? exception.getMessage() : "publish reply failed: unknown error";
		
		if (exception instanceof BadNetworkException) {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_NET);
			error.setMsg("bad network");
			this.callReplyListener.onFailed(this.publishResponse, error);
		} else {
			MqttError error = new MqttError();
			error.setCode(ErrorCode.INVOKE_SERVER);
			error.setMsg(msg);
			this.callReplyListener.onFailed(this.publishResponse, error);
		}
	}
	
	class CallReplyListenerInternal implements IOnCallReplyListener {

		@Override
		public void onSuccess(PublishResponse response) {
			logger.info("publish reply succ, topic = [{}], qos=[{}], msg = [{}]", response.getReplyTopic(), 
					response.getQos() ,JSON.toJSONString(response.getData()));
		}
		@Override
		public void onFailed(PublishResponse response, MqttError error) {
			logger.error("publish reply fail, topic = [{}], error=[{}]", response.getReplyTopic(), 
					error.toString());
		}
		
    }
	
}

