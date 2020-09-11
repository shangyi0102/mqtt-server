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
	//接收超时
	public static final int C_TIMEOUT = 101;
	//处理
	public static final int C_HANDLE = 102;
	//主动取消
	public static final int S_RPC_CANCEL = 202;

	public static final int UNKNOWN = 201;
	
	public static final int INVOKE_NET = 4101;
	
	public static final int INVOKE_SERVER = 4102;
	
	public static final int SERVER_BUSINESS = 4103;
	
	
}

