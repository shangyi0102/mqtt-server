package com.qjzh.link.mqtt.server.channel;

import com.qjzh.link.mqtt.base.PublishRequest;

public interface IPushRequestHandler {
	
	Object onCommand(PublishRequest publishRequest) throws Exception;

}
