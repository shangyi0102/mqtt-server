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
public class ProductMeta {

	private Long id;

	private String productIdentifier;

	private String productName;

	private String productType;
	
	private Long parentProduct;
	
	private String parentProductIdentifier;
	
	private List<PVendor> productModels;

	private List<PAttribute> staticAttrs;

	private List<PAttribute> dynamicAttrs;
	
	private List<PEvent> pEvents;

	private List<PService> pServices;
	
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

	public List<PVendor> getProductModels() {
		return productModels;
	}

	public void setProductModels(List<PVendor> productModels) {
		this.productModels = productModels;
	}

	public List<PAttribute> getStaticAttrs() {
		return staticAttrs;
	}

	public void setStaticAttrs(List<PAttribute> staticAttrs) {
		this.staticAttrs = staticAttrs;
	}

	public List<PAttribute> getDynamicAttrs() {
		return dynamicAttrs;
	}

	public void setDynamicAttrs(List<PAttribute> dynamicAttrs) {
		this.dynamicAttrs = dynamicAttrs;
	}

	public List<PService> getProdServices() {
		return pServices;
	}

	public void setProdServices(List<PService> pServices) {
		this.pServices = pServices;
	}
	
	public List<PEvent> getProdEvents() {
		return pEvents;
	}

	public void setProdEvents(List<PEvent> pEvents) {
		this.pEvents = pEvents;
	}

	
	public PEventParam getEventByTag(String eventIdent, String attrTag){
		PEvent pEvent = null;
		if (!CollectionUtil.isEmpty(pEvents)) {
			for (PEvent objEvent : pEvents) {
				if (eventIdent.equals(objEvent.getEventIdentifier())) {
					pEvent = objEvent;
					break;
				}
			}
		}
		if (null != pEvent) {
			return getEventParamByTag(attrTag, pEvent.getEventParams(), null);
		}
		
		return null;
	}
	
	private PEventParam getEventParamByTag(String attrTag, 
			List<PEventParam> lstEventParam, PEventParam parentEventParam){
		
		String[] attrTags = attrTag.split("\\.");
		for (PEventParam pEventParam : lstEventParam) {
			String paramIdentifier = pEventParam.getParamIdentifier();
			DataType paramType = DataType.valueOf(pEventParam.getParamType());
			if (paramIdentifier.equals(attrTags[0])) {
				pEventParam.setParentParam(parentEventParam);
				if (paramType == DataType.STRUCT) {
					return getEventParamByTag(attrTag.substring(attrTag.indexOf(".")+1), pEventParam.getSubParams(), pEventParam);
				}else if(paramType == DataType.ARRAY) {
					DataType arrayParamType = DataType.valueOf(pEventParam.getArrayParamType());
					if (arrayParamType == DataType.STRUCT) {
						PEventParam subParam = getEventParamByTag(attrTag.substring(attrTag.indexOf(".")+1), 
								pEventParam.getSubParams(), pEventParam);
						if (subParam == null) {
							return null;
						}
						//ProdParamType aItemType = ProdParamType.getArrayInParamType(ProdParamType.valueOf(subParam.getParamType()));
						PEventParam aItemEventParam = new PEventParam();
						BeanUtil.copyProperties(subParam, aItemEventParam);
						aItemEventParam.setParamType(subParam.getParamType());
						return aItemEventParam;
					}
					
					//ProdParamType arrayType = ProdParamType.getArrayInParamType(ProdParamType.valueOf(eventParam.getArrayParamType()));
					
					PEventParam arrayEventParam = new PEventParam();
					BeanUtil.copyProperties(pEventParam, arrayEventParam);
					arrayEventParam.setParamType(pEventParam.getArrayParamType());
					
					return arrayEventParam;
				}
				return pEventParam;
			}
		}
		
		return null;
	}
	
	
	public PAttribute getAttrByTag(String attrTag){
		PAttribute pAttribute = null;
		if (!CollectionUtil.isEmpty(staticAttrs)) {
			pAttribute = getAttrByTag(attrTag, staticAttrs, null);
		}

		if (pAttribute == null && !CollectionUtil.isEmpty(dynamicAttrs)) {
			pAttribute = getAttrByTag(attrTag, dynamicAttrs, null);
		}
		
		return pAttribute;
	}
	
	
	private PAttribute getAttrByTag(String attrTag, List<PAttribute> lstAttrs, PAttribute parentAttr){
		
		String[] attrTags = attrTag.split("\\.");
		for (PAttribute pAttribute : lstAttrs) {
			String attrIdentifier = pAttribute.getAttrIdentifier();
			if (attrIdentifier.equals(attrTags[0])) {
				DataType attrType = DataType.valueOf(pAttribute.getAttrType());
				pAttribute.setParentAttr(parentAttr);
				if (attrType == DataType.STRUCT) {
					return getAttrByTag(attrTag.substring(attrTag.indexOf(".")+1), pAttribute.getSubAttrs(), pAttribute);
				}
				return pAttribute;
			}
		}
		
		return null;
	}
	
	public PService getServiceByIdent(String serviceIdent){
		
		if (!CollectionUtil.isEmpty(pServices)) {
			for (PService pService : pServices) {
				if (serviceIdent.equals(pService.getServiceIdentifier())) {
					return pService;
				}
			}
		}
		return null;
	} 
	
	public PEvent getEventByIdent(String eventIdent){
		if (!CollectionUtil.isEmpty(pEvents)) {
			for (PEvent objEvent : pEvents) {
				if (eventIdent.equals(objEvent.getEventIdentifier())) {
					return objEvent;
				}
			}
		}
		
		return null;
	}
	
	
}