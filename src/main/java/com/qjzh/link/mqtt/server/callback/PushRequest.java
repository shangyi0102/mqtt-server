package com.qjzh.link.mqtt.server.callback;

import com.qjzh.link.mqtt.model.device.DeviceSubject;

/**
 * @DESC: 设备上报请求数据
 * @author LIU.ZHENXING
 * @date 2020年09月15日下午6:17:23
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class PushRequest {

	private String tenandIdent;
	
	private String appIdent;
	
	private String prodIdent;
	
	private DeviceSubject devSubject;
	
	public PushRequest(String tenandIdent, String appIdent, String prodIdent) {
		this.tenandIdent = tenandIdent;
		this.appIdent = appIdent;
		this.prodIdent = prodIdent;
	}

	public String getTenandIdent() {
		return tenandIdent;
	}

	public void setTenandIdent(String tenandIdent) {
		this.tenandIdent = tenandIdent;
	}

	public String getAppIdent() {
		return appIdent;
	}

	public void setAppIdent(String appIdent) {
		this.appIdent = appIdent;
	}

	public String getProdIdent() {
		return prodIdent;
	}

	public void setProdIdent(String prodIdent) {
		this.prodIdent = prodIdent;
	}

	public DeviceSubject getDevSubject() {
		return devSubject;
	}

	public void setDevSubject(DeviceSubject devSubject) {
		this.devSubject = devSubject;
	}
	
}
