package com.qjzh.link.mqtt.jsonschema;


import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.jsonschema.internal.JsonFormat;
import com.qjzh.link.mqtt.jsonschema.type.AbsSchema;
import com.qjzh.link.mqtt.jsonschema.type.ArraySchema;
import com.qjzh.link.mqtt.jsonschema.type.EnumSchema;
import com.qjzh.link.mqtt.jsonschema.type.IntegerSchema;
import com.qjzh.link.mqtt.jsonschema.type.NumberSchema;
import com.qjzh.link.mqtt.jsonschema.type.ObjectSchema;
import com.qjzh.link.mqtt.jsonschema.type.StringSchema;
import com.qjzh.link.mqtt.model.product.Attribute;
import com.qjzh.link.mqtt.model.product.DataType;
import com.qjzh.link.mqtt.model.product.Event;
import com.qjzh.link.mqtt.model.product.EventParam;
import com.qjzh.link.mqtt.model.product.ParamEnum;
import com.qjzh.link.mqtt.model.product.ProductMeta;
import com.qjzh.tools.core.collection.CollectionUtil;

/**
 * @DESC: ProdStruct 解析器
 * @author LIU.ZHENXING
 * @date 2020年2月25日上午9:04:17
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class ProductJsonSchemaParser extends AbsJsonSchemaParser{

	private static final String ATTRIBUTE_KEY = "attrs";
	
	private static final String EVENT_KEY = "events";
	
	private static final Logger logger = LoggerFactory.getLogger(ProductJsonSchemaParser.class);
	
	private static final String DEF_REQUIRED_FIELD_DEV_SN = "DEV_SN";
	
	static{
		//sysRequiredFields.add(DEF_REQUIRED_FIELD_DEV_SN);
	}
	
	private ProductMeta prodMeta;
	
	//产品必填属性名
	private List<String> userRequiredFields;
	
	//产品SN属性自定义 Key  
	private String devSnKey;
	
	private ProductJsonSchemaParser(ProductMeta prodMeta, List<String> userRequiredFields){
		this.prodMeta = prodMeta;
		this.userRequiredFields = userRequiredFields;
	}
	
	public static ProductJsonSchemaParser builder(@NotNull ProductMeta prodMeta){
		return builder(prodMeta, null);
	}
	
	public static ProductJsonSchemaParser builder(@NotNull ProductMeta prodMeta, List<String> userRequiredFields){
		ProductJsonSchemaParser prodSchemaResolver = new ProductJsonSchemaParser(prodMeta, userRequiredFields);
		return prodSchemaResolver.build();
	}
	
	private ProductJsonSchemaParser build(){
		if (prodMeta == null) {
			return this;
		}
		try {
			/*
			 * 0. 构建产品JSON数据格式
			 */
			objectSchema = new ObjectSchema(prodMeta.getProductIdentifier());
			/*
			 * 1. 解析属性JSON数据格式
			 */
			ObjectSchema objectSchemaAttrs = new ObjectSchema("属性列表项");
			objectSchemaAttrs.setAdditionalProperties(false);
			//静态属性
			List<Attribute> staticAttrs = prodMeta.getStaticAttrs();
			patternProperty(objectSchemaAttrs, staticAttrs);
			//动态属性
			List<Attribute> dynamicAttrs = prodMeta.getDynamicAttrs();
			patternProperty(objectSchemaAttrs, dynamicAttrs);
			
			objectSchema.addProperties(ATTRIBUTE_KEY, objectSchemaAttrs, false);
			
			/*
			 * 2. 解析事件JSON数据格式
			 */
			List<Event> lstProdEvents = prodMeta.getProdEvents();
			if (!CollectionUtil.isEmpty(lstProdEvents)) {
				//事件
				ObjectSchema objectSchemaEvents = new ObjectSchema("事件列表项");
				objectSchemaEvents.setAdditionalProperties(false);
				
				lstProdEvents.forEach(prodEvent ->{
					//事件单项
					String eventField = prodEvent.getEventIdentifier();
					List<EventParam> lstEventParams = prodEvent.getEventParams();
					if (StringUtils.isEmpty(eventField) 
							|| CollectionUtil.isEmpty(lstEventParams)) {
						return ;
					}
					
					ObjectSchema objectSchemaEvent = new ObjectSchema(prodEvent.getEventName());
					objectSchemaEvent.setAdditionalProperties(false);
					
					patternEvent(objectSchemaEvent, lstEventParams);
					
					objectSchemaEvents.addProperties(eventField, objectSchemaEvent, false);
				});
				
				objectSchema.addProperties(EVENT_KEY, objectSchemaEvents, false);
			}
			
			
		} catch (Exception e) {
			logger.error("构建 Json Schema 异常", e);
		}
		
		return this;
	}
	
	/**
	 * DESC: 获取设备唯一标识符 Key
	 * @return
	 */
	public String getDevSNKey(){
		return devSnKey;
	} 
	
	/**
	 * DESC: 获取必填属性个数
	 * @return
	 */
	public Integer getRequiredFieldCount(){
		int count = 0;
		if (!CollectionUtil.isEmpty(sysRequiredFields)) {
			count += sysRequiredFields.size();
		}
		
		if (!CollectionUtil.isEmpty(userRequiredFields)) {
			count += userRequiredFields.size();
		}
		return count;
	}
	
	/**
	 * DESC: 属性格式化
	 * @return
	 */
	private void patternProperty(ObjectSchema objectSchema, List<Attribute> lstProdAttrs){
		
		if (CollectionUtil.isEmpty(lstProdAttrs)) {
			return;
		}
		//将 ProdAttrs 转化为  AbsSchema
		lstProdAttrs.forEach(prodAttr ->{
			
			String keyFlag = prodAttr.getKeyFlag(); 
			String field = prodAttr.getAttrIdentifier();
			AbsSchema attrSchema = getSchema(prodAttr);
			
			if (StringUtils.isEmpty(field) || attrSchema == null) {
				return;
			}
			boolean required = false;
			if (!StringUtils.isEmpty(keyFlag)){
				required = isSysRequiredField(keyFlag);
				if (DEF_REQUIRED_FIELD_DEV_SN.equals(keyFlag)) {
					this.devSnKey = field;
				}
				
			}else{
				required = isUserRequiredField(field);
			}
			objectSchema.addProperties(field, attrSchema, required);
		});
	}
	
	/**
	 * DESC: 事件格式化
	 * @return
	 */
	private void patternEvent(ObjectSchema objectSchema, List<EventParam> lstEventParams){
		
		if (CollectionUtil.isEmpty(lstEventParams)) {
			return;
		}
		//将 ProdEvent 转化为  AbsSchema
		lstEventParams.forEach(eventParam ->{
			String field = eventParam.getParamIdentifier();
			Integer required = eventParam.getMustFlag();
			AbsSchema eventSchema = getSchema(eventParam);
			if (StringUtils.isEmpty(field) || eventSchema == null) {
				return;
			}
			objectSchema.addProperties(field, eventSchema, ((required!=null && required==1) ? true : false));
			
		});
	}
	
	
	private boolean isSysRequiredField(String field){
	
		if (CollectionUtil.isEmpty(sysRequiredFields)) {
			return false;
		}
		
		return sysRequiredFields.contains(field);
	}
	
	private boolean isUserRequiredField(String field){
		
		if (CollectionUtil.isEmpty(userRequiredFields)) {
			return false;
		}
		
		return userRequiredFields.contains(field);
	}
	
	/**
	 * DESC: 根据属性信息格式化 Json Schema
	 * @param prodAttr 属性定义信息
	 * @return
	 */
	private AbsSchema getSchema(Attribute prodAttr) {
		AbsSchema attrSchema = null;
		String attrType = prodAttr.getAttrType();
		if (attrType.equalsIgnoreCase(DataType.INT.getCode())){
			BigDecimal min = prodAttr.getMin();
			BigDecimal max = prodAttr.getMax();
			IntegerSchema intSchema = new IntegerSchema();
			if (null != min) {
				intSchema.setMinimum(min.intValue());
			}
			if (null != max) {
				intSchema.setMaximum(max.intValue());
			}
			attrSchema = intSchema;
			
		} else if (attrType.equalsIgnoreCase(DataType.FLOAT.getCode())
				|| attrType.equalsIgnoreCase(DataType.DOUBLE.getCode())){
			BigDecimal min = prodAttr.getMin();
			BigDecimal max = prodAttr.getMax();
			NumberSchema numberSchema = new NumberSchema();
			if (null != min) {
				numberSchema.setMinimum(min);
			}
			if (null != max) {
				numberSchema.setMaximum(max);
			}
			attrSchema = numberSchema;
			
		} else if (attrType.equalsIgnoreCase(DataType.ENUM.getCode())){
			EnumSchema<String> enumSchema = null;
			List<ParamEnum> lstEnum = prodAttr.getEnumItems();
			if (!CollectionUtil.isEmpty(lstEnum)) {
				String[] astrValue = new String[lstEnum.size()];
				for (int i = 0; i < lstEnum.size(); i++) {
					astrValue[i] = lstEnum.get(i).getItemCode();
				}
				enumSchema = new EnumSchema<>(astrValue);
			}
			if (enumSchema == null) {
				enumSchema = new EnumSchema<>(new String[]{"-1"});
			}
			attrSchema = enumSchema;
			
		} else if (attrType.equalsIgnoreCase(DataType.BOOL.getCode())){
			EnumSchema<Integer> enumSchema = new EnumSchema<>(new Integer[]{0,1});
			attrSchema = enumSchema;
		} else if (attrType.equalsIgnoreCase(DataType.TEXT.getCode())){
			StringSchema strSchema = new StringSchema();
			BigDecimal maxLength = prodAttr.getMax();
			if (null != maxLength) {
				strSchema.setMaxLength(maxLength.intValue());
			}
			attrSchema = strSchema;
			
		} else if (attrType.equalsIgnoreCase(DataType.DATE.getCode())){
			StringSchema strSchema = new StringSchema();
			strSchema.setFormat(JsonFormat.DATE);
			attrSchema = strSchema;
		}else if (attrType.equalsIgnoreCase(DataType.DATETIME.getCode())){
			StringSchema strSchema = new StringSchema();
			strSchema.setFormat(JsonFormat.DATETIME_C);
			attrSchema = strSchema;
		} else if (attrType.equalsIgnoreCase(DataType.STRUCT.getCode())){
			List<Attribute> lstSubAttrs = prodAttr.getSubAttrs();
			ObjectSchema objectSchema = new ObjectSchema(prodAttr.getAttrIdentifier());
			objectSchema.setAdditionalProperties(false);
			
			patternProperty(objectSchema, lstSubAttrs);
			
			attrSchema = objectSchema;
		}
		
		return attrSchema;
	}

	/**
	 * DESC: 根据事件信息格式化 Json Schema
	 * @param eventParam 事件定义信息
	 * @return
	 */
	private AbsSchema getSchema(EventParam eventParam) {
		AbsSchema eventSchema = null;
		String paramType = eventParam.getParamType();
		if (paramType.equalsIgnoreCase(DataType.INT.getCode())){
			BigDecimal min = eventParam.getMin();
			BigDecimal max = eventParam.getMax();
			IntegerSchema intSchema = new IntegerSchema();
			if (null != min) {
				intSchema.setMinimum(min.intValue());
			}
			if (null != max) {
				intSchema.setMaximum(max.intValue());
			}
			eventSchema = intSchema;
			
		} else if (paramType.equalsIgnoreCase(DataType.FLOAT.getCode())
				|| paramType.equalsIgnoreCase(DataType.DOUBLE.getCode())){
			BigDecimal min = eventParam.getMin();
			BigDecimal max = eventParam.getMax();
			NumberSchema numberSchema = new NumberSchema();
			if (null != min) {
				numberSchema.setMinimum(min);
			}
			if (null != max) {
				numberSchema.setMaximum(max);
			}
			eventSchema = numberSchema;
			
		} else if (paramType.equalsIgnoreCase(DataType.ENUM.getCode())){
			EnumSchema<String> enumSchema = null;
			List<ParamEnum> lstEnum = eventParam.getEnumItems();
			if (!CollectionUtil.isEmpty(lstEnum)) {
				String[] astrValue = new String[lstEnum.size()];
				for (int i = 0; i < lstEnum.size(); i++) {
					astrValue[i] = lstEnum.get(i).getItemCode();
				}
				enumSchema = new EnumSchema<>(astrValue);
			}
			if (enumSchema == null) {
				enumSchema = new EnumSchema<>(new String[]{"-1"});
			}
			eventSchema = enumSchema;
			
		} else if (paramType.equalsIgnoreCase(DataType.BOOL.getCode())){
			EnumSchema<Integer> enumSchema = new EnumSchema<>(new Integer[]{0,1});
			eventSchema = enumSchema;
		} else if (paramType.equalsIgnoreCase(DataType.TEXT.getCode())){
			StringSchema strSchema = new StringSchema();
			BigDecimal maxLength = eventParam.getMax();
			if (null != maxLength) {
				strSchema.setMaxLength(maxLength.intValue());
			}
			eventSchema = strSchema;
			
		} else if (paramType.equalsIgnoreCase(DataType.DATE.getCode())){
			StringSchema strSchema = new StringSchema();
			strSchema.setFormat(JsonFormat.DATE);
			eventSchema = strSchema;
		} else if (paramType.equalsIgnoreCase(DataType.DATETIME.getCode())){
			StringSchema strSchema = new StringSchema();
			strSchema.setFormat(JsonFormat.DATETIME_C);
			eventSchema = strSchema;
		} else if (paramType.equalsIgnoreCase(DataType.STRUCT.getCode())){
			List<EventParam> lstEventParams = eventParam.getSubParams();
			ObjectSchema objectSchema = new ObjectSchema(eventParam.getParamIdentifier());
			objectSchema.setAdditionalProperties(false);
			
			patternEvent(objectSchema, lstEventParams);
			
			eventSchema = objectSchema;
		} else if (paramType.equalsIgnoreCase(DataType.ARRAY.getCode())){
			ArraySchema arraySchema = new ArraySchema();
			arraySchema.setAdditionalItems(false);
			Integer arrayLength = eventParam.getArrayLength();
			if (null != arrayLength) {
				arraySchema.setMaxItems(eventParam.getArrayLength());
			}
			
			AbsSchema itemSchema = null;
			String arrayParamType = eventParam.getArrayParamType();
			if (arrayParamType.equalsIgnoreCase(DataType.STRUCT.getCode())){
				ObjectSchema objItemSchema = new ObjectSchema(eventParam.getParamIdentifier());
				objItemSchema.setAdditionalProperties(false);
				patternEvent(objItemSchema, eventParam.getSubParams());
				itemSchema = objItemSchema;
			} else{
				eventParam.setParamType(arrayParamType);
				itemSchema = getSchema(eventParam);
			}
			arraySchema.setItem(itemSchema);
			eventSchema = arraySchema;
		}
		
		return eventSchema;
	}
	
	
	
}
