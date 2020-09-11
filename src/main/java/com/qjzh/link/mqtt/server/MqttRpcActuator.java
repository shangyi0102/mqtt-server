package com.qjzh.link.mqtt.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MqttRpcActuator implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(MqttRpcActuator.class);
	
	public static final Map<String, MqttPublishRpc> FUTURES = new ConcurrentHashMap<>(64);
	// 锁
    private static final Lock lock = new ReentrantLock();
    // 空
    private static final Condition empty = lock.newCondition();
    
    public static void signal() {
		// 获得锁
		lock.lock();
		empty.signalAll();
		lock.unlock();
	}

	public void scan() {
		// 获得锁
		lock.lock();
		try {
			while (true) {
				if (FUTURES.size() == 0) {
					empty.await();
				}
				FUTURES.values().removeIf(value -> (value == null || value.isTimeout()));
				//每500ms扫一次
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			logger.error("thread interrupted!", e);
		}finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		scan();
	}
    
}
