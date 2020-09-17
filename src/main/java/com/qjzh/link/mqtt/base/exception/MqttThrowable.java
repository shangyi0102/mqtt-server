package com.qjzh.link.mqtt.base.exception;

/**
 * @DESC: mqtt自定义异常
 * @author LIU.ZHENXING
 * @date 2020年8月18日上午9:00:17
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttThrowable extends Throwable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String message = null;

	public MqttThrowable(String msg) {
		this.message = msg;
	}
}

