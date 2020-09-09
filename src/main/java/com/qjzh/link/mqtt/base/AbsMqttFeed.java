package com.qjzh.link.mqtt.base;

public abstract class AbsMqttFeed {
	
	protected INet net;
	
	protected IMqStatus status;

	public AbsMqttFeed(INet net) {
		this.net = net;
	}

	public IMqStatus getStatus() {
		return this.status;
	}

	public INet getNet() {
		return net;
	}
	
}
