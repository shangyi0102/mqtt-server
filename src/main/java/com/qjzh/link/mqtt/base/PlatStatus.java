package com.qjzh.link.mqtt.base;


/**
 * @DESC: 平台服务状态消息
 * @author LIU.ZHENXING
 * @date 2020年09月15日下午1:42:52
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class PlatStatus {
	/**
	 * 消息体
	 */
	private Payload payload;

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public static class Payload {
		//角色名称
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
}
