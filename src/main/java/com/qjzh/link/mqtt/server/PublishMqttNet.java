package com.qjzh.link.mqtt.server;

import org.springframework.scheduling.TaskScheduler;

import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.server.channel.IOnCallListener;
import com.qjzh.link.mqtt.server.channel.IOnCallReplyListener;
import com.qjzh.link.mqtt.server.interim.MqttPublish;
import com.qjzh.link.mqtt.server.interim.MqttPublishReply;
import com.qjzh.link.mqtt.server.interim.MqttPublishRpc;

public class PublishMqttNet extends AbsMqttNet {
	
	private IOnCallListener callListener;
	
	private IOnCallReplyListener callReplyListener;
	
	public PublishMqttNet( MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		super("publish", initParams, taskScheduler);
	}
	
	public PublishMqttNet(String clientId, MqttInitParams initParams, 
			TaskScheduler taskScheduler) {
		super(clientId, initParams, taskScheduler);
	}
	
	public void setCallListener(IOnCallListener callListener) {
		this.callListener = callListener;
	}

	public void setCallReplyListener(IOnCallReplyListener callReplyListener) {
		this.callReplyListener = callReplyListener;
	}
	
	public void publish(PublishRequest publishRequest) throws MqttInvokeException{
		MqttPublish publish = new MqttPublish(this, publishRequest, callListener);
		publish.send();
	}

	public PublishResponse publishRpc(PublishRequest publishRequest) throws MqttInvokeException {
		MqttPublishRpc publishRpc = new MqttPublishRpc(this, publishRequest, callListener);
		return publishRpc.sendRpc();
	}
	
	public PublishResponse publishRpc(PublishRequest publishRequest, int timeout) throws MqttInvokeException {
		MqttPublishRpc publishRpc = new MqttPublishRpc(this, publishRequest, timeout, callListener);
		return publishRpc.sendRpc();
	}

	public void publishReply(PublishResponse publishResponse) throws MqttInvokeException {
		MqttPublishReply publishReply = new MqttPublishReply(this, publishResponse, callReplyListener);
		publishReply.send();
	}
	
}
