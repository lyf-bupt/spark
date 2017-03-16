package com.mxgraph.examples.util;

import java.util.HashMap;
import java.util.HashSet;

import org.dom4j.Element;

import com.mxgraph.examples.web.Constants;

/**
 * 将核心代码嵌入代码模板之中产生完整的spark代码
 * @author spark
 *
 */
public class TemplateGenerator {
	HashSet<String> set = null;
	HashMap<String,String> map = null;
	public TemplateGenerator(HashSet<String> set){
		this.set = set;
		//对于调用外部接口的情况，首先new一个接口的实例
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
	
	/**
	 * 将核心代码填入样板代码中生成完整代码
	 * @param coreCode 生成的核心代码
	 * @param filename 最终生成的scala文件名和最终jar包名字
	 * @param input 输入源
	 * @param reduce 用户计划中的归约算子
	 * @return 完整scala代码
	 */
	public String generateTemplate(String coreCode, String filename, Element input, Element reduce) {
		String Template = "";
		//对于有数据源的情况采用spark streaming进行处理
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
			Template += "	val out = new PrintWriter(\"/home/spark/result/resOfZYJ.txt\")\n"
					+ "	var df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");//设置日期格式\n"
					+ "	val sparkConf = new SparkConf().setAppName(\"NetworkWordCount\")\n"
					+ "	val ssc = new StreamingContext(sparkConf, Seconds(1))\n"
					+ "	val lines = ssc.socketTextStream(\""+Constants.DATA_SOURCE+"\", 9999, StorageLevel.MEMORY_AND_DISK_SER)\n"
					+ "	 val input = lines.flatMap(_.split(\"\\n\"))\n" + "	" + coreCode + "\n"
					+"	 res.foreachRDD(x => {\n"
					+"   	var y = x.collect()\n"
					+"     	y.foreach( z =>{\n"
					+"      	out.println(df.format(new Date())+\"  \"+z._1+\":\"+z._2)\n"
					+"      	out.flush()\n"
					+"     	})\n"
					+"   })\n"
					+ "	ssc.start()\n" + "	ssc.awaitTermination()\n"
					+ "	}\n" + "}";
		} else {
			//没有数据源的一般认为是批处理，目前input逻辑较为简单，就是从1到100000共100000个数
			Template = "import org.apache.spark.{SparkContext, SparkConf}\n\n" + "import java.io.PrintWriter\n"
					+"import JSON_RPC.getjsonRpc\n"
					+ "object " + filename + "{\n" + "	def main(args:Array[String]): Unit ={\n";
			for(String type:this.set){
				Template += "	"+map.get(type);
			}
			Template	+= "		val conf = new SparkConf().setAppName(\"Spark map test\")\n"
					+ "		val spark = new SparkContext(conf)\n" + "		val n =  100000\n"
					+ "		val input = spark.parallelize(1 to n)\n" + "		" + coreCode + "\n"
					+ "		val out = new PrintWriter(\"/home/spark/result/resOfZYJ.txt\")\n"
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
