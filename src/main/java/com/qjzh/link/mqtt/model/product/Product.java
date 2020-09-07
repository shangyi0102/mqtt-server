package com.qjzh.link.mqtt.model.product;


import java.util.List;

import com.qjzh.tools.core.bean.BeanUtil;
import com.qjzh.tools.core.collection.CollectionUtil;

/**
 * @DESC: 产品结构定义类
 * @author LIU.ZHENXING
 * @date 2020年2月20日下午4:20:27
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class Product {

	private Long id;

	private String productIdentifier;

	private String productName;

	private String productType;
	
	private Long parentProduct;
	
	private String parentProductIdentifier;
	
	private List<Vendor> productModels;

	private List<Attribute> staticAttrs;

	private List<Attribute> dynamicAttrs;
	
	private List<Event> events;

	private List<Service> services;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProductIdentifier(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}

	public String getProductIdentifier() {
		return productIdentifier;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductType() {
		return productType;
	}

	public Long getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(Long parentProduct) {
		this.parentProduct = parentProduct;
	}

	public String getParentProductIdentifier() {
		return parentProductIdentifier;
	}

	public void setParentProductIdentifier(String parentProductIdentifier) {
		this.parentProductIdentifier = parentProductIdentifier;
	}

	public List<Vendor> getProductModels() {
		return productModels;
	}

	public void setProductModels(List<Vendor> productModels) {
		this.productModels = productModels;
	}

	public List<Attribute> getStaticAttrs() {
		return staticAttrs;
	}

	public void setStaticAttrs(List<Attribute> staticAttrs) {
		this.staticAttrs = staticAttrs;
	}

	public List<Attribute> getDynamicAttrs() {
		return dynamicAttrs;
	}

	public void setDynamicAttrs(List<Attribute> dynamicAttrs) {
		this.dynamicAttrs = dynamicAttrs;
	}

	public List<Service> getProdServices() {
		return services;
	}

	public void setProdServices(List<Service> services) {
		this.services = services;
	}
	
	public List<Event> getProdEvents() {
		return events;
	}

	public void setProdEvents(List<Event> events) {
		this.events = events;
	}

	
	public EventParam getEventByTag(String eventIdent, String attrTag){
		Event event = null;
		if (!CollectionUtil.isEmpty(events)) {
			for (Event objEvent : events) {
				if (eventIdent.equals(objEvent.getEventIdentifier())) {
					event = objEvent;
					break;
				}
			}
		}
		if (null != event) {
			return getEventParamByTag(attrTag, event.getEventParams(), null);
		}
		
		return null;
	}
	
	private EventParam getEventParamByTag(String attrTag, 
			List<EventParam> lstEventParam, EventParam parentEventParam){
		
		String[] attrTags = attrTag.split("\\.");
		for (EventParam eventParam : lstEventParam) {
			String paramIdentifier = eventParam.getParamIdentifier();
			DataType paramType = DataType.valueOf(eventParam.getParamType());
			if (paramIdentifier.equals(attrTags[0])) {
				eventParam.setParentParam(parentEventParam);
				if (paramType == DataType.STRUCT) {
					return getEventParamByTag(attrTag.substring(attrTag.indexOf(".")+1), eventParam.getSubParams(), eventParam);
				}else if(paramType == DataType.ARRAY) {
					DataType arrayParamType = DataType.valueOf(eventParam.getArrayParamType());
					if (arrayParamType == DataType.STRUCT) {
						EventParam subParam = getEventParamByTag(attrTag.substring(attrTag.indexOf(".")+1), 
								eventParam.getSubParams(), eventParam);
						if (subParam == null) {
							return null;
						}
						//ProdParamType aItemType = ProdParamType.getArrayInParamType(ProdParamType.valueOf(subParam.getParamType()));
						EventParam aItemEventParam = new EventParam();
						BeanUtil.copyProperties(subParam, aItemEventParam);
						aItemEventParam.setParamType(subParam.getParamType());
						return aItemEventParam;
					}
					
					//ProdParamType arrayType = ProdParamType.getArrayInParamType(ProdParamType.valueOf(eventParam.getArrayParamType()));
					
					EventParam arrayEventParam = new EventParam();
					BeanUtil.copyProperties(eventParam, arrayEventParam);
					arrayEventParam.setParamType(eventParam.getArrayParamType());
					
					return arrayEventParam;
				}
				return eventParam;
			}
		}
		
		return null;
	}
	
	
	public Attribute getAttrByTag(String attrTag){
		Attribute attribute = null;
		if (!CollectionUtil.isEmpty(staticAttrs)) {
			attribute = getAttrByTag(attrTag, staticAttrs, null);
		}

		if (attribute == null && !CollectionUtil.isEmpty(dynamicAttrs)) {
			attribute = getAttrByTag(attrTag, dynamicAttrs, null);
		}
		
		return attribute;
	}
	
	
	private Attribute getAttrByTag(String attrTag, List<Attribute> lstAttrs, Attribute parentAttr){
		
		String[] attrTags = attrTag.split("\\.");
		for (Attribute attribute : lstAttrs) {
			String attrIdentifier = attribute.getAttrIdentifier();
			if (attrIdentifier.equals(attrTags[0])) {
				DataType attrType = DataType.valueOf(attribute.getAttrType());
				attribute.setParentAttr(parentAttr);
				if (attrType == DataType.STRUCT) {
					return getAttrByTag(attrTag.substring(attrTag.indexOf(".")+1), attribute.getSubAttrs(), attribute);
				}
				return attribute;
			}
		}
		
		return null;
	}
	
	public String getSerCodeBySerId(Long serId){
		
		if (!CollectionUtil.isEmpty(services)) {
			for (Service service : services) {
				Long serIdentifier = service.getId();
				if (serIdentifier == serId) {
					return service.getServiceIdentifier();
				}
			}
		}
		return null;
	} 
	
}