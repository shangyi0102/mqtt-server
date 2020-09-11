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

	public static final String rootCrt = "/root.crt";

	private String[] serverURIs;

	private String username;

	private String password;
	//客户端标记
	private String clientMark = "Default";

	private boolean isCheckRootCrt = false;

	private InputStream mqttRootCrtFile;

	private boolean cleanSession = true;

	private int keepAliveInterval = 30;
	
	//等待动作完成最大时间	
	private int timeToWait = 5000;

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
	
	public String getClientMark() {
		return clientMark;
	}

	public void setClientMark(String clientMark) {
		this.clientMark = clientMark;
	}
	
	public int getTimeToWait() {
		return timeToWait;
	}

	public void setTimeToWait(int timeToWait) {
		this.timeToWait = timeToWait;
	}

	public boolean checkValid() {
		if ((null == this.serverURIs || this.serverURIs.length == 0)
				|| StringUtils.isEmpty(this.username)
				|| StringUtils.isEmpty(this.password)) {
			return false;
		}
		return true;
	}
}
