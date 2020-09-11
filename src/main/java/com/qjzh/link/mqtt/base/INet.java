package com.qjzh.link.mqtt.base;

import com.qjzh.link.mqtt.channel.ConnectState;
import com.qjzh.link.mqtt.channel.IOnCallListener;
import com.qjzh.link.mqtt.channel.IOnCallReplyListener;
import com.qjzh.link.mqtt.channel.IOnSubscribeListener;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.callback.RequestMessageListener;

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
	
	void publish(PublishRequest publishRequest);
	
    PublishResponse publishRpc(PublishRequest publishRequest);
	
    PublishResponse publishRpc(PublishRequest publishRequest, int timeout);

    void publishReply(PublishResponse publishResponse);
    
	void subscribe(SubscribeRequest subscribeRequest);
	
	public void subscribeReply(SubscribeRequest subscribeRequest);
	
	void destroy();
	
	void setCallListener(IOnCallListener callListener);

	void setCallReplyListener(IOnCallReplyListener callReplyListener);

	void setSubscribeListener(IOnSubscribeListener subscribeListener);

	public void setRequestMessageListener(RequestMessageListener requestMessageListener);

	public void setReplyMessageListener(ReplyMessageListener replyMessageListener);
	
}
