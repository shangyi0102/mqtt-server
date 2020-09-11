package com.qjzh.link.mqtt.channel;

import com.qjzh.link.mqtt.base.PublishResponse;

public interface IOnRpcResponseHandle {
	
	void onResponse(PublishResponse response);
	
}

