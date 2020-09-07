package com.qjzh.link.mqtt.base;

public abstract class QJSend {
	
	protected INet mqttNet;
	
	protected final QJRequest request;
	
	protected QJResponse response = null;
	
	protected ISendStatus status = null;

	protected final IOnCallListener listener;

	public QJSend(INet mqttNet, QJRequest request, IOnCallListener listener) {
		this.mqttNet = mqttNet;
		this.request = request;
		this.listener = listener;
		this.status = QJSendStatus.waitingToSend;
		this.response = new QJResponse();
	}

	public QJRequest getRequest() {
		return this.request;
	}

	public QJResponse getResponse() {
		return this.response;
	}

	public IOnCallListener getListener() {
		return this.listener;
	}

	public ISendStatus getStatus() {
		return this.status;
	}

	public INet getINet() {
		return mqttNet;
	}
	
}
