/*
 * 所有数据源的父类，目前没有在使用
 */
package com.mxgraph.examples.dataSource;

public class Sourcer {
	SourcerInterface source;
	public Sourcer(String type){
		if(type.indexOf("kafka")>-1){
			source = new KafkaSourcer(type);
		}else if(type.indexOf("发布订阅")>-1){
			source = new PubSubSourcer(type);
		}else if(type.indexOf("MQTT")>-1){
			source = new MQTTSourcer(type);
		}else if(type.indexOf("zeroMQ")>-1){
			source = new ZeroMQSourcer(type);
		}else{
			
		}
	}
	
	public String getSourceCode(){
		return this.source.getSourceCode();
	}
	
}
