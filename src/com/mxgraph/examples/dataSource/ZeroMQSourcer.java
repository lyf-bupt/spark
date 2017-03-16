/*
 * ZeroMQ作为数据，生成数据源代码嵌入模板代码之中，目前没有在使用
 * zeroMQ是消息中间件，spark原生支持
 * 具体可以参照%spark_home%/examples/src/main/scala/org/apache/spark/examples/streaming/
 */
package com.mxgraph.examples.dataSource;

public class ZeroMQSourcer implements SourcerInterface{
	String address;
	String topic;
	public ZeroMQSourcer(String type){
		String[] param = type.split("-");
		this.address = param[1];
		this.topic = param[2];
	}

	@Override
	public String getSourceCode() {
		// TODO Auto-generated method stub
		String ans = "val url=\""+this.address+"\"\n val topic = \""+this.topic+"\"\n"
				+ "def bytesToStringIterator(x: Seq[ByteString]): Iterator[String] = x.map(_.utf8String).iterator"
						+"val lines = ZeroMQUtils.createStream(ssc, url, Subscribe(topic), bytesToStringIterator _)";
		return ans;
	}

}
