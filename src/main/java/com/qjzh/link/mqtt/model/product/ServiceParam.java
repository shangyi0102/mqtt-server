package com.qjzh.link.mqtt.model.product;


import java.util.List;

/**
 * @DESC: 服务参数
 * @author LIU.ZHENXING
 * @date 2020年2月20日下午4:35:01
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class ServiceParam {

	private Long id;
	
	private String paramDesc;
	    
    private String paramIdentifier;
    
    private String paramName;
    
    private String paramType;
    
    private String paramTypeName;
	
    private Long parentParamId;
    
    private Long productServiceId;
    
    private String dateFormat;
	
    private String arrayParamType;
    
    private List<ParamEnum> enumItems;
    
    private String inOutFlag;
    
    private Double max;
    
    private Double min;
    
    private Integer mustFlag;
    
    private String unit;
    
    private List<ServiceParam> subParams;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public String getParamIdentifier() {
		return paramIdentifier;
	}

	public void setParamIdentifier(String paramIdentifier) {
		this.paramIdentifier = paramIdentifier;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamTypeName() {
		return paramTypeName;
	}

	public void setParamTypeName(String paramTypeName) {
		this.paramTypeName = paramTypeName;
	}

	public Long getParentParamId() {
		return parentParamId;
	}

	public void setParentParamId(Long parentParamId) {
		this.parentParamId = parentParamId;
	}

	public Long getProductServiceId() {
		return productServiceId;
	}

	public void setProductServiceId(Long productServiceId) {
		this.productServiceId = productServiceId;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getArrayParamType() {
		return arrayParamType;
	}

	public void setArrayParamType(String arrayParamType) {
		this.arrayParamType = arrayParamType;
	}

	public List<ParamEnum> getEnumItems() {
		return enumItems;
	}

	public void setEnumItems(List<ParamEnum> enumItems) {
		this.enumItems = enumItems;
	}

	public String getInOutFlag() {
		return inOutFlag;
	}

	public void setInOutFlag(String inOutFlag) {
		this.inOutFlag = inOutFlag;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Integer getMustFlag() {
		return mustFlag;
	}

	public void setMustFlag(Integer mustFlag) {
		this.mustFlag = mustFlag;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<ServiceParam> getSubParams() {
		return subParams;
	}

	public void setSubParams(List<ServiceParam> subParams) {
		this.subParams = subParams;
	}

}