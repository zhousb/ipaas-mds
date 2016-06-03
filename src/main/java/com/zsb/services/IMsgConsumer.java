package com.zsb.services;

public interface IMsgConsumer {

	void init();

	void close();

	boolean isAlive();
	
	
}
