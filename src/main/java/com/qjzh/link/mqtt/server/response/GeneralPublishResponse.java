package com.qjzh.link.mqtt.server.response;

import com.qjzh.link.mqtt.base.PublishResponse;

public class GeneralPublishResponse extends PublishResponse {
	
	//客户端超时未处理
	public static final Integer TIMEOUT = 501;
	//服务端主动取消
	public static final Integer CANCEL = 502;
	
}
