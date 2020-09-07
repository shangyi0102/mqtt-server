package com.qjzh.link.mqtt.model.product;

import java.util.List;

/**
 * @DESC: 产品事件
 * @author LIU.ZHENXING
 * @date 2020年4月15日下午3:31:20
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class Event {
	
	private Long productId;

	private String productIdentifier;

	private Long id;
	
	private String eventType;

	private String eventIdentifier;

	private String eventName;
	
	private List<EventParam> eventParams;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
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

	public String getEventIdentifier() {
		return eventIdentifier;
	}

	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public List<EventParam> getEventParams() {
		return eventParams;
	}

	public void setEventParams(List<EventParam> eventParams) {
		this.eventParams = eventParams;
	}
	
}
