package com.qjzh.link.mqtt.server;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * @DESC: mqtt 连接配置
 * @author LIU.ZHENXING
 * @date 2020年8月17日下午1:53:17
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttInitParams {

	private String[] serverURIs;

	private String username;

	private String password;
	
	private Will will;

	private boolean isCheckRootCrt = false;

	private InputStream mqttRootCrtFile;

	private boolean cleanSession = true;

	private int keepAliveInterval = 30;
	//默认等待动作完成最大时间	
	private int timeToWait = 5000;
	
	private int maxInflight = 100;

	public MqttInitParams(String[] serverURIs, String username, String password) {
		this.serverURIs = serverURIs;
		this.username = username;
		this.password = password;
	}

	public String[] getServerURIs() {
		return serverURIs;
	}

	public void setServerURIs(String[] serverURIs) {
		this.serverURIs = serverURIs;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isCheckRootCrt() {
		return isCheckRootCrt;
	}

	public void setCheckRootCrt(boolean isCheckRootCrt) {
		this.isCheckRootCrt = isCheckRootCrt;
	}

	public InputStream getMqttRootCrtFile() {
		return mqttRootCrtFile;
	}

	public void setMqttRootCrtFile(InputStream mqttRootCrtFile) {
		this.mqttRootCrtFile = mqttRootCrtFile;
	}

	public boolean getCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}
	
	public int getTimeToWait() {
		return timeToWait;
	}

	public void setTimeToWait(int timeToWait) {
		this.timeToWait = timeToWait;
	}

	public int getMaxInflight() {
		return maxInflight;
	}

	public void setMaxInflight(int maxInflight) {
		this.maxInflight = maxInflight;
	}
	
	public Will getWill() {
		return will;
	}

	public void setWill(Will will) {
		this.will = will;
	}

	public boolean checkValid() {
		if ((null == this.serverURIs || this.serverURIs.length == 0)
				|| StringUtils.isEmpty(this.username)
				|| StringUtils.isEmpty(this.password)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @DESC: MQTT遗愿类
	 * @author LIU.ZHENXING
	 * @date 2020年8月14日上午10:29:38
	 * @version 1.0.0
	 * @copyright www.7g.com
	 */
	public static class Will {

		private final String topic;

		private final byte[] payload;

		private final int qos;

		private final boolean retained;

		public Will(String topic, byte[] payload, int qos, boolean retained) {
			this.topic = topic;
			this.payload = payload; 
			this.qos = qos;
			this.retained = retained;
		}

		protected String getTopic() {
			return this.topic;
		}

		protected byte[] getPayload() {
			return this.payload; //NOSONAR
		}

		protected int getQos() {
			return this.qos;
		}

		protected boolean isRetained() {
			return this.retained;
		}

	}
	
}
