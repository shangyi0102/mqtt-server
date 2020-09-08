package com.qjzh.link.mqtt.base;

public abstract class AbsQJSend {
	
	protected INet mqttNet;
	
	protected final QJRequest qJRequest;
	
	protected QJResponse qJResponse = null;
	
	protected ISendStatus status = null;

	protected final IOnCallListener listener;

	public AbsQJSend(INet mqttNet, QJRequest qJRequest, IOnCallListener listener) {
		this.mqttNet = mqttNet;
		this.qJRequest = qJRequest;
		this.listener = listener;
		this.status = QJSendStatus.waitingToSend;
		this.qJResponse = new QJResponse();
	}

	public QJRequest getRequest() {
		return this.qJRequest;
	}

	public QJResponse getResponse() {
		return this.qJResponse;
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
