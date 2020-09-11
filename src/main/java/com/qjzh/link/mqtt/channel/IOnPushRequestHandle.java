package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.PublishRequest;

public interface IOnPushRequestHandle {
	
	Object onCommand(PublishRequest publishRequest) throws Exception;

}
