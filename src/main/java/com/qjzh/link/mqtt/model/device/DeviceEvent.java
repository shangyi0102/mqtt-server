package com.qjzh.link.mqtt.model.device;

import com.alibaba.fastjson.JSONObject;

public class DeviceEvent{
	
	private String eventIdent;
	
	private JSONObject eventData;

	public String getEventIdent() {
		return eventIdent;
	}

	public void setEventIdent(String eventIdent) {
		this.eventIdent = eventIdent;
	}

	public JSONObject getEventData() {
		return eventData;
	}

	public void setEventData(JSONObject eventData) {
		this.eventData = eventData;
	}
	
}