package com.zsb.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgConsumer;

public class KafkaMsgConsumerImpl implements IMsgConsumer{

	private static final Logger LOG = LoggerFactory.getLogger(KafkaMsgConsumerImpl.class);
	
	private boolean active = true;
	
	private MdsParam param;
	
	private ConsumerConfig configer;
	
	private Properties pro;
	
	private String topic;
	
	private Integer threadCount;
	
	public KafkaMsgConsumerImpl(MdsParam param){
		
		this.param = param;
		init();
	}
	
	
	@Override
	public void init() {
		try{
			pro = new Properties();
			pro.putAll(param.getConf());
			topic = pro.getProperty(Consts.KafkaConst.TOPIC);
			String count = pro.getProperty(Consts.KafkaConst.CONSUMER_THREAD_COUNT, "1");
		
			threadCount = Integer.parseInt(count.trim());
			
			configer = new ConsumerConfig(pro);
			
		}
		catch(Exception e){
			LOG.error("",e);
		}
	}

	
	@Override
	public void close() {
	
	}

	@Override
	public boolean isAlive() {
		
		return active;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * 获取消费流(根据线程数返回消费流)
	 * @return
	 */
	public List<KafkaStream<byte[], byte[]>> getConsumerStream(){
		ConsumerConnector con = Consumer.createJavaConsumerConnector(configer);
		Map<String,Integer> topicCountMap = new HashMap<String,Integer>();
		if(threadCount == 0){
			threadCount = 1;
		}
		topicCountMap.put(topic,threadCount);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = con.createMessageStreams(topicCountMap);
		return consumerMap.get(topic);
	}
	
	
}
