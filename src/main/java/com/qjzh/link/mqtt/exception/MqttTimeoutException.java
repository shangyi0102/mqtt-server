/*
 * *****************************************************
 * *****************************************************
 * Copyright (C), 2018-2020, panda-fa.com
 * FileName: com.panda.mqtt.exception.MqttTimeoutException
 * Author:   丁许
 * Date:     2019/3/11 20:09
 * *****************************************************
 * *****************************************************
 */
package com.qjzh.link.mqtt.exception;

/**
 * @DESC: 同步调用超时异常
 * @author LIU.ZHENXING
 * @date 2020年9月9日上午11:06:45
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttTimeoutException extends MqttRpcException {

	private static final long serialVersionUID = -4541083227944111553L;

	public MqttTimeoutException() {
		super();
	}

	public MqttTimeoutException(String message) {
		super(message);
	}
}
