package com.qjzh.link.mqtt.base;

public abstract class AbsMqttDriven{
	
	protected IMqttNet mqttNet;
	
	public AbsMqttDriven(IMqttNet mqttNet) {
		this.mqttNet = mqttNet;
	}

	public IMqttNet getMqttNet() {
		return mqttNet;
	}
	
}
