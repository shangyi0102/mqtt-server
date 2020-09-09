package com.qjzh.link.mqtt.exception;

/**
 * @DESC: 同步调用异常
 * @author LIU.ZHENXING
 * @date 2020年9月9日上午11:05:18
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttRpcException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6796752866447893520L;

	public MqttRpcException() {
		super();
	}

	public MqttRpcException(String message) {
		super(message);
	}
}
