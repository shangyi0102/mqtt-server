package com.qjzh.link.mqtt.model.device;

/**
 * @DESC: 设备信息
 * @author LIU.ZHENXING
 * @date 2020年8月31日上午11:22:05
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class Device {

	private String tenantIdent;

	private String appIdent;

	private Long productId;
	
	private String productIdentifier;
	
	private String productName;
	
	private String productType;
	
	private String accessType;
	
	private String devId;
	
	private String devName;
	
	private String sn;
	
	private String gatewayDeviceId;
	
	private String gatewayDeviceSn;
	
	private String gatewayProductIdentifier;
	
	private String model;
	
	private Long modelId;
	
	private String modelIdentifier;

	public String getTenantIdent() {
		return tenantIdent;
	}

	public void setTenantIdent(String tenantIdent) {
		this.tenantIdent = tenantIdent;
	}

	public String getAppIdent() {
		return appIdent;
	}

	public void setAppIdent(String appIdent) {
		this.appIdent = appIdent;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductIdentifier() {
		return productIdentifier;
	}

	public void setProductIdentifier(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getGatewayDeviceId() {
		return gatewayDeviceId;
	}

	public void setGatewayDeviceId(String gatewayDeviceId) {
		this.gatewayDeviceId = gatewayDeviceId;
	}

	public String getGatewayDeviceSn() {
		return gatewayDeviceSn;
	}

	public void setGatewayDeviceSn(String gatewayDeviceSn) {
		this.gatewayDeviceSn = gatewayDeviceSn;
	}

	public String getGatewayProductIdentifier() {
		return gatewayProductIdentifier;
	}

	public void setGatewayProductIdentifier(String gatewayProductIdentifier) {
		this.gatewayProductIdentifier = gatewayProductIdentifier;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

}
