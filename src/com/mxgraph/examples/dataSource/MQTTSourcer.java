/*
 * MQTT作为数据，生成数据源代码嵌入模板代码之中，目前没有在使用
 * MQTT也是消息中间件，spark streaming原生支持
 * 具体可以参照%spark_home%/examples/src/main/scala/org/apache/spark/examples/streaming/
 */
package com.mxgraph.examples.dataSource;

public class MQTTSourcer implements SourcerInterface{
	String address;
	String topic;
	public MQTTSourcer(String type){
		String[] param = type.split("-");
		this.address = param[1];
		this.topic = param[2];
		
	}

	@Override
	public String getSourceCode() {
		// TODO Auto-generated method stub
		String ans = "val lines = MQTTUtils.createStream(ssc, \""+this.address+"\", \""+this.topic+"\", StorageLevel.MEMORY_AND_DISK_SER)";
		return ans;
	}

}
