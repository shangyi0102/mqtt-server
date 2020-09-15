package com.qjzh.link.mqtt.server.channel;

import com.qjzh.link.mqtt.base.PublishResponse;

public interface IReplyResponseHandler {
	
	void onResponse(PublishResponse response);
	
}

