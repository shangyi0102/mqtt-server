package com.qjzh.link.mqtt.model.product;


import java.util.List;

/**
 * @DESC: 产品服务定义类
 * @author LIU.ZHENXING
 * @date 2020年2月20日下午4:33:58
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class PService {

	private Long id;

	private Long productId;
	
	private String productIdentifier;

	private String serviceIdentifier;

	private String serviceName;
	
	private List<PServiceParam> inParams;

	private List<PServiceParam> outParams;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<PServiceParam> getInParams() {
		return inParams;
	}

	public void setInParams(List<PServiceParam> inParams) {
		this.inParams = inParams;
	}

	public List<PServiceParam> getOutParams() {
		return outParams;
	}

	public void setOutParams(List<PServiceParam> outParams) {
		this.outParams = outParams;
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

	public void setServiceIdentifier(String serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public String getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

}
