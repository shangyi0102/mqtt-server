package com.qjzh.link.mqtt.jsonschema.type;


import java.util.ArrayList;
import java.util.List;

/**
 * @DESC: 数组类型
 * @author LIU.ZHENXING
 * @date 2020年2月25日下午6:00:00
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class ArraySchema extends AbsSchema{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4108038556545652657L;
	//集合数据类型
	private List<AbsSchema> items;
	
	private Integer minItems;
	
	private Integer maxItems;
	
	private Boolean additionalItems;
	
	public ArraySchema() {
		setType("array");
	}

	public List<AbsSchema> getItems() {
		return items;
	}

	public void setItems(List<AbsSchema> items) {
		this.items = items;
		this.put("items", items);
	}
	
	
	public void setItem(AbsSchema item) {
		this.items = new ArrayList<AbsSchema>();
		this.items.add(item);
		this.put("items", item);
	}
	
	public AbsSchema getItem() {
		return items.get(0);
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
		this.put("minItems", minItems);
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
		this.put("maxItems", maxItems);
	}

	public Boolean getAdditionalItems() {
		return additionalItems;
	}

	public void setAdditionalItems(Boolean additionalItems) {
		this.additionalItems = additionalItems;
		this.put("additionalItems", additionalItems);
	}
	
}
