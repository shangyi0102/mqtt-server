package com.qjzh.link.mqtt.server;

import com.qjzh.link.mqtt.base.ISendStatus;

/**
 * @DESC: mqtt发送状态
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午2:05:40
 * @version 1.0.0
 * @copyright www.7g.com
 */
public enum MqttSendStatus implements ISendStatus {
	//发送等待
	waitingToSend,
	//订阅回复
	waitingToSubReply, subReplyed, waitingToPublish, published,
	//完成
	waitingToComplete, completed;
}
