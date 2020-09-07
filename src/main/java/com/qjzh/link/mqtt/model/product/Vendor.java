package com.qjzh.link.mqtt.model.product;


public class Vendor {

	private String accessType;
	
	private String manufacturer;
	
	private String model;
	
	private String modelIdentifier;
	
	private Integer reportPeriod;

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

	public String getModelIdentifier() {
		return modelIdentifier;
	}

	public Integer getReportPeriod() {
		return reportPeriod;
	}

	public void setReportPeriod(Integer reportPeriod) {
		this.reportPeriod = reportPeriod;
	}

}
