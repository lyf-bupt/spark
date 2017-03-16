package com.mxgraph.examples.util;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Element;

/**
 * 内容解析类，现在已经不再使用
 *
 */
public class ContentParser {
	// 每条支路上面的外部接口
	HashMap<String, Vector<Element>> hash = null;
	// 外部接口模板
	HashMap<String, String> map = null;

	public ContentParser(HashMap<String, Vector<Element>> hash, HashMap<String, String> map) {
		this.hash = hash;
		this.map = map;
	}

	public String flatMapParser(String value, String cases) {
		// case1:奇数&#xa;case2:偶数
		// case 马甸:+2;case 方丹苑:+3;case 总部:+4;case 万科:+6
		String result = null;
		if (value.contains("%")) {
			int casesNum = Integer.parseInt(value.substring(value.indexOf("%") + 1));
			result = "x => x" + value + " match {\n";
			String options = cases;
			String[] ops = options.split(";");
			for (int i = 0; i < ops.length; i++) {
				String casename = ops[i].substring(0, ops[i].indexOf(":"));
				String op = ops[i].substring(ops[i].indexOf(":") + 1);
				String code = "x" + op;
				Vector<Element> list = hash.get(casename);
				if (list != null) {
					for (Element ele : list) {
						String type = ele.attributeValue("value").split("~")[0];
						String url = ele.attributeValue("value").split("~")[1];
						String method = ele.attributeValue("value").split("~")[2];
						code = map.get(type) + "(\"" + url + "\",\"" + method + "\",(" + code + ").toString).toInt";
					}
				}
				result += "	" + casename + " => " + code + "\n";
			}
			result += "	case _  => " + " x\n";
			result += "}";
		} else {
			// 处理文字时消息一定是String@value的形式
			result = " x =>x.split(\"@\")(0) match {\n";
			String options = cases;
			String[] ops = options.split(";");
			for (int i = 0; i < ops.length; i++) {
				String casename = ops[i].substring(ops[i].indexOf("case ") + 5, ops[i].indexOf(":"));
				String op = ops[i].substring(ops[i].indexOf(":") + 1);
				String code = "x.split(\"@\")(1).toInt" + op;
				Vector<Element> list = hash.get("case " + casename);
				if (list != null) {
					for (Element ele : list) {
						String type = ele.attributeValue("value").split("~")[0];
						String url = ele.attributeValue("value").split("~")[1];
						String method = ele.attributeValue("value").split("~")[2];
						code = map.get(type) + "(\"" + url + "\",\"" + method + "\",(" + code + ").toString).toInt";
					}
				}
				result += "	case \"" + casename + "\" => " + " (x.split(\"@\")(0)," + code + ")\n";
			}
			result += "	case _  => " + " (x.split(\"@\")(0),x.split(\"@\")(1).toInt)\n";
			result += "}";
		}
		return result;
	}

	public static void main(String args[]) {
		HashMap<String, Vector<Element>> hash = new HashMap<String, Vector<Element>>();
		HashMap<String, String> map = new HashMap<String, String>();
		ContentParser parser = new ContentParser(hash, map);
		String res = parser.flatMapParser("位置", "case 马甸:+2;case 方丹苑:+3;case 总部:+4;case 万科:+6");
		System.out.print(res);
	}
}
