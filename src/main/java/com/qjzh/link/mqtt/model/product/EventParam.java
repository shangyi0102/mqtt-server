package com.qjzh.link.mqtt.model.product;


import java.math.BigDecimal;
import java.util.List;

/**
 * @DESC: 事件参数
 * @author LIU.ZHENXING
 * @date 2020年2月20日下午4:35:01
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class EventParam {

	private Long id;
	
	private String paramDesc;
	    
    private String paramIdentifier;
    
    private String paramName;
    
    private String paramType;
    
    private String paramTypeName;
	
    private Long parentParamId;
    
    private String parentParamIdentifier;
    
    private EventParam parentParam ;
    
    private Long productEventId;
	
    private String arrayParamType;
    
    private List<ParamEnum> enumItems;
    
    private String inOutFlag;
    
    private BigDecimal max;
    
    private BigDecimal min;
    
    private Integer mustFlag;
    
    private String unit;
    
    private Integer arrayLength;
    
    private List<EventParam> subParams;

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

	public String getParentParamIdentifier() {
		return parentParamIdentifier;
	}

	public void setParentParamIdentifier(String parentParamIdentifier) {
		this.parentParamIdentifier = parentParamIdentifier;
	}

	public Long getProductEventId() {
		return productEventId;
	}

	public void setProductEventId(Long productEventId) {
		this.productEventId = productEventId;
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
	
	public Integer getArrayLength() {
		return arrayLength;
	}

	public void setArrayLength(Integer arrayLength) {
		this.arrayLength = arrayLength;
	}

	public List<EventParam> getSubParams() {
		return subParams;
	}

	public void setSubParams(List<EventParam> subParams) {
		this.subParams = subParams;
	}

	public EventParam getParentParam() {
		return parentParam;
	}

	public void setParentParam(EventParam parentParam) {
		this.parentParam = parentParam;
	}

	public String getFullNameRoot(){
		String parentParamName = "";
		if (null != parentParam) {
			parentParamName = parentParam.getFullNameRoot() + "-";
		}
		return parentParamName + getParamName();
	}
	
}