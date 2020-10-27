package com.qjzh.link.mqtt.base;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

import com.qjzh.link.mqtt.model.device.ConnectState;

/**
 * @DESC: 连接接口
 * @author LIU.ZHENXING
 * @date 2020年8月9日下午3:31:44
 * @version 1.0.0
 * @copyright www.7g.com
 */
public interface IMqttNet {
	
	public IMqttAsyncClient getClient();
	
	public int getTimeToWait();
	
	public ConnectState getConnectState();
	
}
