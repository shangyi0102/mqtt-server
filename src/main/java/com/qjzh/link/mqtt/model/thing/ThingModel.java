package com.qjzh.link.mqtt.model.thing;

import java.util.List;

/**
 * @DESC: 物模型定义详情
 * @author LIU.ZHENXING
 * @date 2020年8月20日下午4:15:40
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class ThingModel{

	private String identifier;
	
	private String name;
	
	private String type;
	
	private String parentIdentifier;
	
	private List<PropertyModel> properties;
	
	private List<EventModel> events;
	
	private List<ServiceModel> services;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public List<PropertyModel> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyModel> properties) {
		this.properties = properties;
	}

	public List<EventModel> getEvents() {
		return events;
	}

	public void setEvents(List<EventModel> events) {
		this.events = events;
	}

	public List<ServiceModel> getServices() {
		return services;
	}

	public void setServices(List<ServiceModel> services) {
		this.services = services;
	}
}
