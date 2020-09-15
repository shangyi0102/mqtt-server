package com.qjzh.link.mqtt.listener;


import java.util.Map;
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
public class ProductStartListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductStartListener.class);
	
	public Map<String, Product> PRODUCT_CACHES = new ConcurrentHashMap<>(64);
	
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
				analyzeTenant(tenantCode, tenantMap);
			} catch (Exception e) {
				logger.error("同步产品信息配置异常!", e);
			}
		});
	} 
	
	
	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void clearProdData(){
		try {
			//productSubject.clearData();
		} catch (Exception e) {
			logger.error("同步清除失效产品监听队列异常!", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void analyzeTenant(String tenantCode, Map<String, Object> tenantMap){
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
			
			analyzeApp(tenantCode, appCode, appMap);
			
		});
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void analyzeApp(String tenantCode, String appCode, 
			Map<String, Object> appMap){
		
		//检查应用是否禁用
		String appActive = (String) appMap.get("active");
		if ("N".equals(appActive)) {
			return ;
		}
		
		Map<String, Object> mapProds = getRedisMapByKey(QJConstants.getProductPath(tenantCode, appCode));
		mapProds.forEach((keyProd, valProd) -> {
			//构建消费队列
			try {
				Map<String, Object> dataStruct = (Map<String, Object>) valProd;
				ProductMeta prodMeta = JSON.parseObject(JSON.toJSONString(dataStruct), ProductMeta.class);
				Product product = new Product(tenantCode, appCode, keyProd, prodMeta);
				Product productCache = PRODUCT_CACHES.get(QJConstants.genKeyPath(tenantCode, appCode, keyProd));
				/*//校验是否已注册
				if (null != productCache) {
					productCache.setProduct(product);
					return;
				}*/
				
			} catch (Exception ex) {
				logger.error("构建产品中间件消费监听器", ex);
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
		//获取所有租户信息
		return hashOprs.entries(key);
	}
	

}
