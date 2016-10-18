package com.mxgraph.examples.util;

import java.util.HashMap;
import java.util.HashSet;

import org.dom4j.Element;

public class TemplateGenerator {
	HashSet<String> set = null;
	HashMap<String,String> map = null;
	public TemplateGenerator(HashSet<String> set){
		this.set = set;
		if(!set.isEmpty()){
			String value="";
			map = new HashMap<String,String>();
			for(String type:set){
				if("JSON".equals(type)){
					value = "";
				}else if("Rest".equals(type)){
					value = "val client = new HttpClient()\n";
				}else if("Soap".equals(type)){
					value = "var service = new MyServiceTest()\n";
				}
				map.put(type, value);
			}
		}
	}
	
	public String generateTemplate(String coreCode, String filename, Element input, Element reduce) {
		String Template = "";
		if (input.attributeValue("value") != null && input.attributeValue("value") != "") {
			Template = "import org.apache.spark.SparkConf\n"
					+ "import org.apache.spark.streaming.{Seconds, StreamingContext}\n"
					+ "import org.apache.spark.storage.StorageLevel\n" + "import java.io.PrintWriter\n"
					+"import JSON_RPC.getjsonRpc\n"
					+ "import java.util.Date\n" + "import java.text.SimpleDateFormat\n" + "object " + filename + "{\n"
					+ "	def main(args: Array[String]) {\n";
			for(String type:this.set){
				Template += "	"+map.get(type);
			}
			Template += "	val out = new PrintWriter(\"/home/zhou/result/resOfZYJ.txt\")\n"
					+ "	var df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");//设置日期格式\n"
					+ "	val sparkConf = new SparkConf().setAppName(\"NetworkWordCount\")\n"
					+ "	val ssc = new StreamingContext(sparkConf, Seconds(1))\n"
					+ "	val lines = ssc.socketTextStream(\"10.108.165.203\", 9999, StorageLevel.MEMORY_AND_DISK_SER)\n"// To-do
																														// 将ip改为从配置中读
					+ "	 val input = lines.flatMap(_.split(\"\\n\"))\n" + "	" + coreCode + "\n"
					+ "	ssc.start()\n" + "	ssc.awaitTermination()\n"
					+ "	}\n" + "}";
		} else {
			Template = "import org.apache.spark.{SparkContext, SparkConf}\n\n" + "import java.io.PrintWriter\n"
					+"import JSON_RPC.getjsonRpc\n"
					+ "object " + filename + "{\n" + "	def main(args:Array[String]): Unit ={\n";
			for(String type:this.set){
				Template += "	"+map.get(type);
			}
			Template	+= "		val conf = new SparkConf().setAppName(\"Spark map test\")\n"
					+ "		val spark = new SparkContext(conf)\n" + "		val n =  100000\n"
					+ "		val input = spark.parallelize(1 to n)\n" + "		" + coreCode + "\n"
					+ "		val out = new PrintWriter(\"/home/zhou/result/resOfZYJ.txt\")\n"
					+ "		out.println(\"hello Spark\")\n";
			if (reduce != null) {
				Template += "		out.println(res)\n";
			} else {
				Template += "		out.println(res.first())\n";
			}
			Template += "		out.close()\n" + "		spark.stop()\n" + "	}\n" + "}";
		}
		return Template;
	}

}
