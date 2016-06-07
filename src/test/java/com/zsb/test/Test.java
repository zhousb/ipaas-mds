package com.zsb.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgProduce;
import com.zsb.utils.MsgConsumerUtils;
import com.zsb.utils.MsgProduceUtils;

public class Test {
	
	public static  Map<String, String> map = new ConcurrentHashMap<String, String>();
	public static AtomicInteger count = new AtomicInteger(0);
	//public static  Map<String, String> map = new HashMap<String, String>();
	public static void main(String args[]) throws Exception {
		// testProducer();
		// testConsumer();
		test();
		//System.out.println(String.valueOf(-1));
	}

	public static void testProducer() {
		MdsParam param = new MdsParam();
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("kafka.topic", "mytest");
		conf.put(Consts.KafkaConst.METADATA_BROKER_LIST,
				"node01:9092,node02:9092,node03:9092");
		conf.put(Consts.KafkaConst.REQUEST_REQUIRED_ACKS, "1");
		conf.put(Consts.KafkaConst.SERIALIZER_CLASS,
				"kafka.serializer.StringEncoder");
		param.setConf(conf);
		IMsgProduce producer = MsgProduceUtils.getProducer(param);
		producer.send("test2");
		System.out.println("end!!!!!");
	}

	public static void testConsumer() {
		
		MdsParam param = new MdsParam();
		MsgConsumerUtils.getConsumer(param).getConsumerStream();
		
		MsgConsumerUtils.getKafkaConsumer(param).getConsumerStream();
	}

	public static void test() throws InterruptedException {
		
		//Collections.synchronizedMap(map);
		//Map<String, String> map2 = new HashMap<String, String>();
		//map.put("1", "q");
		for (int i = 0; i < 1000000; i++) {
			map.put(String.valueOf(-i), String.valueOf(-i));
		}
		System.out.println(map.size());
		int c = 502;
		CountDownLatch latch = new CountDownLatch(c);
		for (int i = 0; i < c-2; i++) {
			
			new ThreadTest(latch).start();
			
		}
		
		new RdThreadTest(latch).start();
		new VwThreadTest(latch).start();
		latch.await();
		Gson gson = new Gson();
		System.out.println(map.size()+count.get());
		//System.out.println(gson.toJson(map));
		
		
		
	}

	
}
class ThreadTest extends Thread{
	private CountDownLatch latch;
	public ThreadTest(CountDownLatch latch){
		this.latch = latch;
	}
	@Override
	public void run() {
		
		for (int i = 0; i < 1000; i++) {
			//System.out.println("put");
			Random rn = new Random();
			int j = rn.nextInt(Integer.MAX_VALUE);
			
			//rn.nextDouble();
			if(Test.map.containsKey(String.valueOf(j))){
				Test.count.getAndIncrement();
			}
			Test.map.put(String.valueOf(j), String.valueOf(rn.nextDouble()));
			//System.out.println(i);
		}
		
		latch.countDown();
	}
} 
class RdThreadTest extends Thread{
	private CountDownLatch latch;
	public RdThreadTest(CountDownLatch latch){
		this.latch = latch;
	}
	@Override
	public void run() {
		
		//for (int i = 0; i < 100; i++) {
			//System.out.println("put");
		//	Random rn = new Random();
			//int j = rn.nextInt(Integer.MAX_VALUE);
			for (int ii = 0; ii < 1000000; ii++) {
				Test.map.remove(String.valueOf(-ii));
			}
	//	}
		
		latch.countDown();
	}
}

class VwThreadTest extends Thread{
	private CountDownLatch latch;
	public VwThreadTest(CountDownLatch latch){
		this.latch = latch;
	}
	@Override
	public void run() {
		
		for(Entry entry:Test.map.entrySet()){
			//Test.map.remove(entry.getKey());
		}
		
		latch.countDown();
	}
}
