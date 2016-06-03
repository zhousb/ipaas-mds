package com.zsb.services;

/**
 * @date 2016年6月2日
 * @author zhoushanbin
 *
 */
public interface IMsgProduce {
	
	void init();
	
	
	void close();
	
	boolean isAlive();

	void send(String message);
	
	
}
