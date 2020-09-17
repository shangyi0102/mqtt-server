package com.qjzh.link.mqtt.model.product;


import java.math.BigDecimal;
import java.util.List;

/**
 * @DESC: 产品属性定义类
 * @author LIU.ZHENXING
 * @date 2020年2月20日下午4:30:05
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class PAttribute {
	
	private Long id;

	private String attrCategory;

	private String attrDesc;

	private String attrIdentifier;

	private String attrName;

	private String attrType;

	private String createTime;

	private String createUser;

	private String createUserName;
	
	private String dateFormat;

	private List<ParamEnum> enumItems;

	private String keyFlag;

	private BigDecimal max;

	private BigDecimal min;

	private String parentAttrId;

	private Long productId;

	private String productIdentifier;

	private String rwType;

	private List<PAttribute> subAttrs;

	private String unit;

	private PAttribute parentAttr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setAttrCategory(String attrCategory) {
		this.attrCategory = attrCategory;
	}

	public String getAttrCategory() {
		return attrCategory;
	}

	public void setAttrDesc(String attrDesc) {
		this.attrDesc = attrDesc;
	}

	public String getAttrDesc() {
		return attrDesc;
	}

	public void setAttrIdentifier(String attrIdentifier) {
		this.attrIdentifier = attrIdentifier;
	}

	public String getAttrIdentifier() {
		return attrIdentifier;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}

	public String getAttrType() {
		return attrType;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getCreateUserName() {
		return createUserName;
	}


	public void setKeyFlag(String keyFlag) {
		this.keyFlag = keyFlag;
	}

	public String getKeyFlag() {
		return keyFlag;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}


	public void setParentAttrId(String parentAttrId) {
		this.parentAttrId = parentAttrId;
	}

	public String getParentAttrId() {
		return parentAttrId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductIdentifier(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}

	public String getProductIdentifier() {
		return productIdentifier;
	}

	public void setRwType(String rwType) {
		this.rwType = rwType;
	}

	public String getRwType() {
		return rwType;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public List<PAttribute> getSubAttrs() {
		return subAttrs;
	}

	public void setSubAttrs(List<PAttribute> subAttrs) {
		this.subAttrs = subAttrs;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public List<ParamEnum> getEnumItems() {
		return enumItems;
	}

	public void setEnumItems(List<ParamEnum> enumItems) {
		this.enumItems = enumItems;
	}

	public PAttribute getParentAttr() {
		return parentAttr;
	}

	public void setParentAttr(PAttribute parentAttr) {
		this.parentAttr = parentAttr;
	}

	public String getFullNameRoot(){
		String parentAttrName = "";
		if (null != parentAttr) {
			parentAttrName = parentAttr.getFullNameRoot() + "-";
		}
		return parentAttrName + getAttrName();
	}
	
	@Override
	public String toString() {
		return "ProdAttr [attrIdentifier=" + attrIdentifier + ", attrName=" + attrName + ", attrType=" + attrType + "]";
	}
	
}