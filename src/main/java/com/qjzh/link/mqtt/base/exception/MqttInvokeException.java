package com.qjzh.link.mqtt.base.exception;

/**
 * @DESC: 同步调用异常
 * @author LIU.ZHENXING
 * @date 2020年9月9日上午11:05:18
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttInvokeException extends Exception {

	private static final long serialVersionUID = -6796752866447893520L;

	private Integer code;
	
	public MqttInvokeException(int code, String message) {
		super(message);
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

}
