package com.qjzh.link.mqtt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @DESC: MQTT 连接 配置类
 * @author LIU.ZHENXING
 * @date 2020年9月11日下午2:34:04
 * @version 1.0.0
 * @copyright www.7g.com
 */
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

	/**
	 * 服务器地址
	 */
	private String[] urls;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;

	/**
	 * 服务质量 0=最多一次，有可能重复或丢失; 1=至少一次，有可能重复; 2=只有一次确保消息只到达一次
	 */
	private int qos = 1;
	
	//等待超时时间(ms)
	private int timeToWait = 5000;

	/**
	 * the default max inflight
	 */
	private int maxInflight = 1000;

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public String getUsername() {
		if (null == username || "".equals(username)) {
			return null;
		}
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		if (null == password || "".equals(password)) {
			return null;
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public int getMaxInflight() {
		return maxInflight;
	}

	public void setMaxInflight(int maxInflight) {
		this.maxInflight = maxInflight;
	}

	public int getTimeToWait() {
		return timeToWait;
	}

	public void setTimeToWait(int timeToWait) {
		this.timeToWait = timeToWait;
	}
	
}
