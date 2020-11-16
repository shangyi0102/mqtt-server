package com.qjzh.link.mqtt.model.device;

import com.alibaba.fastjson.JSON;

/**
 * @DESC: 消息格式
 * @author LIU.ZHENXING
 * @date 2020年2月26日下午9:08:55
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class DeviceSubject {
	//设备SN
	private String sn;
	//接收消息时间{时间戳}
	private Long recvTime; 
	//用户上报属性数据
	private Object data;
	//上报事件信息
	private DeviceEvent events;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Long getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(Long recvTime) {
		this.recvTime = recvTime;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public DeviceEvent getEvents() {
		return events;
	}

	public void setEvents(DeviceEvent events) {
		this.events = events;
	}
	
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
