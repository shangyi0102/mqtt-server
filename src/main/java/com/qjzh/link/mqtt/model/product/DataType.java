package com.qjzh.link.mqtt.model.product;

/**
 * @DESC: 属性类型
 * @author LIU.ZHENXING
 * @date 2020年2月25日下午4:47:21
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public enum DataType {

	//整数
	INT("INT"),
	//单精度
	FLOAT("FLOAT"),
	//双精度
	DOUBLE("DOUBLE"),
	//枚举
	ENUM("ENUM"),
	//布尔
	BOOL("BOOL"),
	//字符串
	TEXT("TEXT"),
	//日期
	DATE("DATE"),
	//日期时间
	DATETIME("DATETIME"),
	//结构体
	STRUCT("STRUCT"),
	//数组
	ARRAY("ARRAY"),
	//数组-数字
	ARRAY_NUMBER("ARRAY_NUMBER"),
	//数组-字符串
	ARRAY_TEXT("ARRAY_TEXT"),
	//数组-日期
	ARRAY_DATETIME("ARRAY_DATETIME");
	
	private String code;
	
	DataType(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	public static DataType getArrayInParamType(DataType inParamType){
		DataType arrayInParam = null;
		switch (inParamType) {
		case INT:
			arrayInParam = DataType.ARRAY_NUMBER;
			break;
		case FLOAT:
			arrayInParam = DataType.ARRAY_NUMBER;
			break;
		case DOUBLE:
			arrayInParam = DataType.ARRAY_NUMBER;
			break;
		case BOOL:
			arrayInParam = DataType.ARRAY_NUMBER;
			break;
		case ENUM:
			arrayInParam = DataType.ARRAY_TEXT;
			break;
		case DATE:
			arrayInParam = DataType.ARRAY_DATETIME;
			break;
		case DATETIME:
			arrayInParam = DataType.ARRAY_DATETIME;
			break;
		case TEXT:
			arrayInParam = DataType.ARRAY_TEXT;
			break;	
		default:
			break;
		}
		
		return arrayInParam;
	}
	
}
