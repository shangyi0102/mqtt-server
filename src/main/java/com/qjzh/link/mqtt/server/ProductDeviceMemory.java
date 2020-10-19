package com.qjzh.link.mqtt.server;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qjzh.link.mqtt.base.QJConstants;
import com.qjzh.link.mqtt.model.device.Device;
import com.qjzh.link.mqtt.model.product.Product;
import com.qjzh.link.mqtt.model.product.ProductMeta;
import com.qjzh.tools.core.map.MapUtil;

/**
 * @DESC: 
 * @author LIU.ZHENXING
 * @date 2020年2月4日下午12:01:39
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
@Component
@EnableScheduling
public class ProductDeviceMemory {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductDeviceMemory.class);
	
	public static final Map<String, Product> PRODUCT_CACHES = new ConcurrentHashMap<>(64);
	
	public static final Map<String, Device> DEVICE_CACHES = new ConcurrentHashMap<>(64);
	
	@Autowired
    private RedisTemplate<String, Object> tenantRedisTemplate;
	
	@SuppressWarnings("unchecked")
	@Scheduled(initialDelay = 2000, fixedDelay = 5000)
	public void syncProdData(){
		//获取所有租户信息
		Map<String, Object> mapTenants = getRedisMapByKey(QJConstants.TENANT_LIST_KEY);
		if (MapUtil.isEmpty(mapTenants)) { 
			return;
		}
		mapTenants.forEach((tenantCode, tenantInfo) -> {
			//获取租户信息
			Map<String, Object> tenantMap = (Map<String, Object>)tenantInfo;
			try {
				loadTenant(tenantCode, tenantMap);
			} catch (Exception e) {
				logger.error("同步产品信息配置异常!", e);
			}
		});
	} 
	
	
	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void clearData(){
		try {
			Set<String> devKeys = new HashSet<>(DEVICE_CACHES.keySet());
			for (String devKey : devKeys) {
				Device device = DEVICE_CACHES.get(devKey);
				String tenantCode =  device.getTenantIdent();
				String appCode =  device.getAppIdent();
				String prodCode =  device.getProductIdentifier();
				String devSn = device.getSn();
				Map<String, Object> devMap =  getMapByValueKey(
						QJConstants.getDevicePath(tenantCode, appCode, prodCode), devSn);
				if (MapUtil.isEmpty(devMap)) {
					DEVICE_CACHES.remove(devKey);
				}
			}
			
			Set<String> prodKeys = new HashSet<>(PRODUCT_CACHES.keySet());
			for (String prodKey : prodKeys) {
				Product product = PRODUCT_CACHES.get(prodKey);
				String tenantCode =  product.getTenantCode();
				String appCode =  product.getAppCode();
				String prodCode =  product.getProdCode();
				Map<String, Object> prodMap =  getMapByValueKey(
						QJConstants.getProductPath(tenantCode, appCode), prodCode);
				if (MapUtil.isEmpty(prodMap)) {
					PRODUCT_CACHES.remove(prodKey);
				}
			}
			
		} catch (Exception e) {
			logger.error("同步清除失效产品和设备失败!", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadTenant(String tenantCode, Map<String, Object> tenantMap){
		//获取租户信息
		String tenantActive = (String) tenantMap.get("active");
		//检查是否禁用
		if ("N".equals(tenantActive)) {
			return ;
		}
		Map<String, Object> mapApps = getRedisMapByKey(QJConstants.getAppPath(tenantCode));
		if (MapUtil.isEmpty(mapApps)) { 
			return;
		}
		//遍历应用信息
		mapApps.forEach((appCode, appInfo) -> {
			//获取应用信息
			Map<String, Object> appMap = (Map<String, Object>)appInfo;
			
			loadApp(tenantCode, appCode, appMap);
			
		});
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void loadApp(String tenantCode, String appCode, 
			Map<String, Object> appMap){
		
		//检查应用是否禁用
		String appActive = (String) appMap.get("active");
		if ("N".equals(appActive)) {
			return ;
		}
		
		Map<String, Object> mapProds = getRedisMapByKey(QJConstants.getProductPath(tenantCode, appCode));
		mapProds.forEach((prodCode, prodInfo) -> {
			try {
				Map<String, Object> dataStruct = (Map<String, Object>) prodInfo;
				ProductMeta prodMeta = JSON.parseObject(JSON.toJSONString(dataStruct), ProductMeta.class);
				Product product = new Product(tenantCode, appCode, prodCode, prodMeta);
				String prodKey = QJConstants.getPordKeyPath(tenantCode, appCode, prodCode);
				PRODUCT_CACHES.put(prodKey, product);
				loadDevice(tenantCode, appCode, prodCode);
			} catch (Exception ex) {
				logger.error("同步产品缓存信息失败!", ex);
			}
		});
	}
	
	
	private void loadDevice(String tenantCode, String appCode, String prodCode){
		
		Map<String, Object> mapDevices = getRedisMapByKey(QJConstants.getDevicePath(tenantCode, appCode, prodCode));
		mapDevices.forEach((devSn, devInfo) -> {
			try {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> dataStruct = (Map<String, Object>) devInfo;
				Device device = JSON.parseObject(JSON.toJSONString(dataStruct), Device.class);
				device.setTenantIdent(tenantCode);
				device.setAppIdent(appCode);
				device.setProductIdentifier(prodCode);
				device.setSn(devSn);
				String devKey = QJConstants.getDevKeyPath(prodCode, devSn);
				DEVICE_CACHES.put(devKey, device);
				
			} catch (Exception ex) {
				logger.error("同步设备缓存信息失败!", ex);
			}
		});
	}
	
	
	/**
	 * DESC: 根据KEY获取redis缓存Map数据
	 * @param key
	 * @return
	 */
	private Map<String, Object> getRedisMapByKey(String key){
		HashOperations<String, String, Object> hashOprs = 
				tenantRedisTemplate.opsForHash();
		//获取所有map信息
		return hashOprs.entries(key);
	}
	
	
	/**
	 * DESC: 根据KEY valueKey获取缓存Map数据
	 * @param key
	 * @param valueKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getMapByValueKey(String key, String valueKey){
		HashOperations<String, String, Object> hashOprs = tenantRedisTemplate.opsForHash();
		//获取Map value信息
		return (Map<String, Object>)hashOprs.get(key, valueKey);
	}
	
	
	public static Device getDevice(String prodCode, String devSn){
		return DEVICE_CACHES.get(QJConstants.getDevKeyPath(prodCode, devSn));
	}

	public static Product getProduct(String tenantCode, String appCode, String prodCode){
		return PRODUCT_CACHES.get(QJConstants.getPordKeyPath(tenantCode, appCode, prodCode));
	}
	
}
