package com.qjzh.link.mqtt.base;

/**
 * @DESC: 全局提示码 <br>
 *  0：成功提示码
 *  1~100：一般性错误码
 *  101~199：客户端错误码
 *  200~299：服务端错误码
 *  400~499：自定义错误码
 * @author LIU.ZHENXING
 * @date 2020年8月10日上午11:28:40
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class ErrorCode {
	
	//成功
	public static final int SUCCESS_OK = 0;
	//同步-调用超时
	public static final int RPC_CLIENT_TIMEOUT = 101;
	//同步-调用处理错误
	public static final int RPC_CLIENT_HANDLE = 102;
	//同步-线程中断
	public static final int RPC_CLIENT_INTERRUPT = 103;
	//同步-调用主动取消
	public static final int RPC_SERVER_CANCEL = 202;

	public static final int UNKNOWN = 201;
	//网络异常
	public static final int INVOKE_NET = 4101;
	//服务器异常
	public static final int INVOKE_SERVER = 4102;
	
	
	
}

