package com.qjzh.link.mqtt.server;

import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.server.request.GeneralSubscribeRequest;

/**
 * @DESC: mqtt发送响应线程
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午3:20:29
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttSendResponseRunnable implements Runnable {
	
	public static final byte MSG_PUBLISH_SUCCESS = 1;
	
	public static final byte MSG_PUBLISH_FAILED = 2;
	
	public static final byte MSG_PUBLISH_BADNET = 3;
	
	public static final byte MSG_SUBSRIBE_SUCCESS = 4;
	
	public static final byte MSG_SUBSRIBE_FAILED = 5;
	
	public static final byte MSG_SUBSRIBE_BADNET = 6;
	
	private MqttPublish sendObj = null;
	
	private byte msgType = 0;
	
	private String errorMsg = null;

	public MqttSendResponseRunnable(MqttPublish send, byte type, String errorMsg) {
		this.sendObj = send;
		this.msgType = type;
		this.errorMsg = errorMsg;
	}

	public void run() {
		QJError error;
		QJError error1;
		if (null == this.sendObj) {
			return;
		}
		switch (this.msgType) {

		case 1:
			if (this.sendObj.getListener() == null)
				return;
			this.sendObj.getListener().onSuccess(this.sendObj.getRequest(), this.sendObj.getResponse());
			break;
		case 2:
		case 3:
			if (this.sendObj.getListener() == null) {
				return;
			}
			error = new QJError();
			if (this.msgType == 3) {
				error.setCode(4101);
			} else {
				error.setCode(4201);
			}
			error.setMsg(this.errorMsg);
			this.sendObj.getListener().onFailed(this.sendObj.getRequest(), error);
			break;

		case 4:
			if (this.sendObj.getSubscribeListener() == null)
				return;
			this.sendObj.getSubscribeListener().onSuccess(((GeneralSubscribeRequest) this.sendObj.getRequest()).getTopic());
			break;
		case 5:
		case 6:
			if (this.sendObj.getSubscribeListener() == null) {
				return;
			}
			error1 = new QJError();
			if (this.msgType == 3) {
				error1.setCode(4101);
			} else {
				error1.setCode(4201);
			}
			error1.setMsg(this.errorMsg);
			this.sendObj.getSubscribeListener().onFailed(((GeneralSubscribeRequest) this.sendObj.getRequest()).getTopic(),
					error1);
			break;
		}
	}
}

