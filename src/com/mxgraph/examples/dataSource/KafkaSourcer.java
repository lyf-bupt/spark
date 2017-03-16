/*
 * kafka作为数据，生成数据源代码嵌入模板代码之中，目前没有在使用
 * kafka是分布式消息中间件，spark原生支持
 * 具体可以参照%spark_home%/examples/src/main/scala/org/apache/spark/examples/streaming/
 */

package com.mxgraph.examples.dataSource;

public class KafkaSourcer implements SourcerInterface{
	String address;
	String group;
	String topic;
	public KafkaSourcer(String type){
		String[] params = type.split("-");
		this.address = params[1];
		this.group = params[2];
		this.topic = params[3];
		
	}

	@Override
	public String getSourceCode() {
		// TODO Auto-generated method stub
		String ans = "val topicMap = \""+this.topic+"\".split(\",\").map((_, numThreads.toInt)).toMap\n"
		+"val lines = KafkaUtils.createStream(ssc, \""+this.address+"\", \""+this.group+"\", topicMap).map(_._2)";
		return ans;
	}

}
