package com.qjzh.link.mqtt.jsonschema.type;


import java.util.HashMap;

/**
 * @DESC: Json Schema 抽象属性类型
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午4:43:09
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public abstract class AbsSchema extends HashMap<String, Object>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ID
	private String $id = "";
	//标题
	private String title;
	//描述
	private String description;
	
	private String examples;

	private String type;

	public String get$id() {
		return $id;
	}

	public void set$id(String $id) {
		this.$id = $id;
		this.put("$id", $id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.put("type", type);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.put("title", title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		this.put("description", description);
	}

	public String getExamples() {
		return examples;
	}

	public void setExamples(String examples) {
		this.examples = examples;
		this.put("examples", examples);
	}

}
