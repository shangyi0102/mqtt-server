package com.qjzh.link.mqtt.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @DESC: 自定义计数器
 * @author LIU.ZHENXING
 * @date 2018年5月16日下午7:58:56
 * @version 1.0.0
 * @since
 */
public class QjzhAtomicInteger {

	private final AtomicInteger sequence = new AtomicInteger(0);

	private final static QjzhAtomicInteger counter = new QjzhAtomicInteger();
	
	private QjzhAtomicInteger(){}
	
	public static QjzhAtomicInteger getInstance(){
		return counter;
	}
	
	public static int incre(){
		return counter.incrementAndGet();
	}
	
	public final int incrementAndGet() {
		int current;
		int next;
		do {
			current = sequence.get();
			next = current >= 2147483647 ? 0 : current + 1;
		} while (!sequence.compareAndSet(current, next));

		return next;
	}

	public final int decrementAndGet() {
		int current;
		int next;
		do {
			current = sequence.get();
			next = current <= 0 ? 2147483647 : current - 1;
		} while (!sequence.compareAndSet(current, next));

		return next;
	}

	public final void set(int newValue) {
		 sequence.set(newValue);
    }
	
	@Override
	public String toString() {
		return sequence.toString();
	}
	
}
