package com.zsb.services;

import java.util.List;

public interface IMsgConsumer {

	void init();

	void close();

	boolean isAlive();
	
	List<?> getConsumerStream();
}
