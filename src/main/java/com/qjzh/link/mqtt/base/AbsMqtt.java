package com.qjzh.link.mqtt.base;

public abstract class AbsMqtt {
	
	protected INet net;
	

	public AbsMqtt(INet net) {
		this.net = net;
	}

	public INet getNet() {
		return net;
	}
	
}
