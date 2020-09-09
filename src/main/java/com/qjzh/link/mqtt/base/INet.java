package com.qjzh.link.mqtt.base;

import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.channel.IOnSubscribeRpcListener;

/**
 * @DESC: 连接接口
 * @author LIU.ZHENXING
 * @date 2020年8月9日下午3:31:44
 * @version 1.0.0
 * @copyright www.7g.com
 */
public interface INet {
	
	void init();

	void setConnectState(ConnectState connectState);
	
	ConnectState getConnectState();
	
	void publish(PublishRequest publishRequest, 
			IOnCallListener listener);
	
    PublishResponse publishRpc(PublishRequest publishRequest,
			IOnCallListener listener);
	
    PublishResponse publishRpc(PublishRequest publishRequest, 
			int timeout, IOnCallListener listener);

	void subscribe(SubscribeRequest subscribeRequest, IOnSubscribeListener listener);
	
	//void subscribeRpc(SubscribeRequest subscribeRequest, IOnSubscribeRpcListener onSubscribeRpcListener);

	void unSubscribe(String topic, IOnSubscribeListener onSubscribeListener);
	
	void destroy();

	
}
