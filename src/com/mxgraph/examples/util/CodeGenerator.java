package com.mxgraph.examples.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.dom4j.Element;
/**
* 生成核心代码
*/
public class CodeGenerator {
	/** 记录对外的接口的类型 */
	HashMap<String, String> map = null;

	public CodeGenerator(HashSet<String> set) {
		map = new HashMap<String, String>();
		String code = "";
		//遍历之前解析的xml取出对外接口的类型并将类型和对应的代码存入map中
		for (String type : set) {
			if ("JSON".equals(type)) {
				code = "getjsonRpc";
			} else if ("Rest".equals(type)) {
				code = "client.getValue";
			} else if ("Soap".equals(type)) {
				code = "service.invokeWebService";
			}
			map.put(type, code);
		}
	}
	
	 /**
	 * 生成从数据源到分叉的代码
	 * @param children
	 * ：所有的节点
	 * @param input
	 * ：数据源
	 * @param flatMap
	 * ：分叉节点
	 * @param reduce
	 * ：归约节点
	 * @param output
	 *  ：数据输出节点
	 *
	 */
	public String genneratePretreatment(List<Element> children, Element input, Element flatMap, Element reduce,
			Element output) {
		String currentNodeId = input.attributeValue("id");
		String code = "";
		if (flatMap != null) {
			// 有flatMap
			// 有input（有源）
			// 计算从source到faltMap
			if (input.attributeValue("value") != null && input.attributeValue("value") != "") {
				//当前指针不指向分叉节点就沿着连线进行遍历
				//现在的时间复杂度是n×n，可以改良一下
				while (!currentNodeId.equals(flatMap.attributeValue("id"))) {
					for (Element ele : children) {
						if (ele.attribute("source") != null) {
							if (currentNodeId.equals(ele.attributeValue("source").toString())) {
								currentNodeId = ele.attributeValue("target");
								for (Element ele1 : children) {
									//菱形，map节点
									if (ele1.attributeValue("style") != null
											&& "rhombus".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										code += ".map(x=>{\n" + " var value = x.split(\"@\")(1).toInt"
												+ ele1.attributeValue("value") + "\n"
												+ " var name = x.split(\"@\")(0)\n" + "name+\"@\"+value\n" + "})";
									}
									//web service，webservice节点，接口调用外面使用map进行包装
									if (ele1.attributeValue("style") != null
											&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										String type = ele1.attributeValue("value").split("~")[0];
										String url = ele1.attributeValue("value").split("~")[1];
										String method = ele1.attributeValue("value").split("~")[2];
										code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
												+ "x.toString))\n";
									}
								}
							}
						}
						if (currentNodeId.equals(flatMap.attributeValue("id")))
							break;
					}
				}
			} else {
				// 有flatMap
				// 没有input（没有源）
				// 从source计算到flatMap
				while (!currentNodeId.equals(flatMap.attributeValue("id"))) {
					for (Element ele : children) {
						if (ele.attribute("source") != null) {
							if (currentNodeId.equals(ele.attributeValue("source").toString())) {
								currentNodeId = ele.attributeValue("target");
								for (Element ele1 : children) {
									if (ele1.attributeValue("style") != null
											&& "rhombus".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										code += ".map(_" + ele1.attributeValue("value") + ")";
									}
									if (ele1.attributeValue("style") != null
											&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										String type = ele1.attributeValue("value").split("~")[0];
										String url = ele1.attributeValue("value").split("~")[1];
										String method = ele1.attributeValue("value").split("~")[2];
										code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
												+ "x.toString))\n";
									}
								}
							}
						}
						if (currentNodeId.equals(flatMap.attributeValue("id")))
							break;
					}
				}
			}
		} else {
			if (input.attributeValue("value") != null && input.attributeValue("value") != "") {
				// 没有flatMap
				// 有input
				// 没有reduce
				// 从source计算到output
				if (reduce == null) {
					while (!currentNodeId.equals(output.attributeValue("id"))) {
						for (Element ele : children) {
							if (ele.attribute("source") != null) {
								if (currentNodeId.equals(ele.attributeValue("source").toString())) {
									currentNodeId = ele.attributeValue("target");
									for (Element ele1 : children) {
										if (ele1.attributeValue("style") != null
												&& "rhombus".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".map(x=>{\n" + " var value = x.split(\"@\")(1).toInt"
													+ ele1.attributeValue("value") + "\n"
													+ " var name = x.split(\"@\")(0)\n" + "name+\"@\"+value\n" + "})";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											String type = ele1.attributeValue("value").split("~")[0];
											String url = ele1.attributeValue("value").split("~")[1];
											String method = ele1.attributeValue("value").split("~")[2];
											code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString))\n";
										}
									}
								}
							}
							if (currentNodeId.equals(output.attributeValue("id"))) {
								code += ".map(x => (x.split(\"@\")(0),x.split(\"@\")(1).toInt))";
								break;
							}
							;
						}
					}
				} else {
					// 没有flatMap
					// 有input
					// 带reduce的情况
					// 生成resource到reduce的代码
					while (!currentNodeId.equals(reduce.attributeValue("id"))) {
						for (Element ele : children) {
							if (ele.attribute("source") != null) {
								if (currentNodeId.equals(ele.attributeValue("source").toString())) {
									currentNodeId = ele.attributeValue("target");
									for (Element ele1 : children) {
										if (ele1.attributeValue("style") != null
												&& "rhombus".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".map(" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "shape=xor".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".filter(" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											String type = ele1.attributeValue("value").split("~")[0];
											String url = ele1.attributeValue("value").split("~")[1];
											String method = ele1.attributeValue("value").split("~")[2];
											code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString))\n";
										}
									}
								}
							}
							if (currentNodeId.equals(reduce.attributeValue("id")))
								break;
						}
					}
				}

			} else {
				// 没有flatMap
				// 没有input（无源）
				// 没有reduce
				// 从source到output
				if (reduce == null) {
					while (!currentNodeId.equals(output.attributeValue("id"))) {
						for (Element ele : children) {
							if (ele.attribute("source") != null) {
								if (currentNodeId.equals(ele.attributeValue("source").toString())) {
									currentNodeId = ele.attributeValue("target");
									for (Element ele1 : children) {
										if (ele1.attributeValue("style") != null
												&& "rhombus".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".map(_" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											String type = ele1.attributeValue("value").split("~")[0];
											String url = ele1.attributeValue("value").split("~")[1];
											String method = ele1.attributeValue("value").split("~")[2];
											code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString))\n";
										}
									}
								}
							}
							if (currentNodeId.equals(output.attributeValue("id")))
								break;
						}
					}
				} else {
					// 没有flatMap
					// 没有input（无源）
					// 有reduce
					// 从source到reduce
					while (!currentNodeId.equals(reduce.attributeValue("id"))) {
						for (Element ele : children) {
							if (ele.attribute("source") != null) {
								if (currentNodeId.equals(ele.attributeValue("source").toString())) {
									currentNodeId = ele.attributeValue("target");
									for (Element ele1 : children) {
										if (ele1.attributeValue("style") != null
												&& "rhombus".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".map(_" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											String type = ele1.attributeValue("value").split("~")[0];
											String url = ele1.attributeValue("value").split("~")[1];
											String method = ele1.attributeValue("value").split("~")[2];
											code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString))\n";
										}
									}
								}
							}
							if (currentNodeId.equals(reduce.attributeValue("id")))
								break;
						}
					}
				}
			}
		}
		return code;
	}
	
	/**
	 * 生成分叉部分的代码
	 * @param children
	 * ：所有的节点
	 * @param input
	 * ：数据源
	 * @param flatMap
	 * ：分叉节点
	 * @param reduce
	 * ：归约节点
	 *
	 */
	public String generateBranch(List<Element> children, Element input, Element output, Element flatMap,
			Element reduce) {
		// 生成每条支路上的代码
		String code = "";
		// 每条支路上的外部接口
		HashMap<String, Vector<Element>> hash = new HashMap<String, Vector<Element>>();
		if (flatMap != null) {
			String currentNodeId = flatMap.attributeValue("id");
			// 计算每条支路的第一个节点
			List<Element> casesNodes = new Vector<Element>();
			for (Element ele : children) {
				if (ele.attributeValue("source") != null) {
					if (ele.attributeValue("source").equals(currentNodeId)) {
						for (Element ele1 : children) {
							if (ele1.attributeValue("id").equals(ele.attributeValue("target"))) {
								String value = ele1.attributeValue("value");
								casesNodes.add(ele1);
							}
						}
					}
				}
			}
			// for(Element ele:casesNodes){
			// System.out.println(ele.attributeValue("value"));
			// }
			// 每条支路的最后一个节点要么时reduce节点，要么是output节点
			if (casesNodes.size() != 0) {
				String casesOps = "";
				Element branchEnd = null;
				if(reduce != null){
					branchEnd = reduce;
				}else{
					branchEnd = output;
				}
				// 计算每条支路的代码
				for (int i = 0; i < casesNodes.size(); i++) {
					code += "val res" + i + "=input";
					currentNodeId = casesNodes.get(i).attributeValue("id");
					String type = casesNodes.get(i).attributeValue("value").split(":")[0];
					Vector<Element> list = new Vector<Element>();
					// if("rhombus".equals(casesNodes.get(i).attributeValue("style").toString())){
					// casesOps += casesNodes.get(i).attributeValue("value");
					// }else
					// if("ellipse;shape=cloud".equals(casesNodes.get(i).attributeValue("style").toString())){
					// casesOps +=
					// casesNodes.get(i).attributeValue("value").split(":")[0]+":";
					// casesNodes.get(i).setAttributeValue("value",
					// casesNodes.get(i).attributeValue("value").substring(casesNodes.get(i).attributeValue("value").indexOf(':')+1));
					// list.addElement(casesNodes.get(i));
					// }
					if (casesNodes.get(i).attributeValue("style") != null
							&& "rhombus".equals(casesNodes.get(i).attributeValue("style").toString())
							&& currentNodeId.equals(casesNodes.get(i).attributeValue("id"))) {
						code += ".map(" + casesNodes.get(i).attributeValue("value") + ")";
					}
					if (casesNodes.get(i).attributeValue("style") != null
							//月牙状的是fiter算子
							&& "shape=xor".equals(casesNodes.get(i).attributeValue("style").toString())
							&& currentNodeId.equals(casesNodes.get(i).attributeValue("id"))) {
						code += ".filter(" + casesNodes.get(i).attributeValue("value") + ")";
					}
					while (!currentNodeId.equals(branchEnd.attributeValue("id"))) {
						for (Element ele : children) {
							if (ele.attribute("source") != null) {
								if (currentNodeId.equals(ele.attributeValue("source").toString())) {
									currentNodeId = ele.attributeValue("target");
									for (Element ele1 : children) {
										if (ele1.attributeValue("style") != null
												&& "rhombus".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".map(" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "shape=xor".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".filter(" + ele1.attributeValue("value") + ")";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += "\n";
											String type1 = ele1.attributeValue("value").split("~")[0];
											String url = ele1.attributeValue("value").split("~")[1];
											String method = ele1.attributeValue("value").split("~")[2];
											code += ".map(x => " + map.get(type1) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString))\n";
										}
									}
								}
							}
							if (currentNodeId.equals(branchEnd.attributeValue("id"))) {
								if (!list.isEmpty()) {
									hash.put(type, list);
								}
								break;
							}
							;
						}
					}
					casesOps += i == (casesNodes.size() - 1) ? "" : ";";
					code += "\n";
				}
				// if(casesOps!=""&&casesOps!=null){
				// System.out.println(casesOps);
				// ContentParser parser= new ContentParser(hash,map);
				// String res =
				// parser.flatMapParser(flatMap.attributeValue("value"),casesOps);
				// code += ".map("+res+")";
				// }
				code += "val res=res0";
				for(int i=1;i<casesNodes.size();i++){
					code += ".union(res"+i+")";
				}
			}
		}
		return code;
	}
	
	/**
	 * 生成归约到输出的代码
	 * @param children
	 * ：所有的节点
	 * @param input
	 * ：数据源
	 * @param reduce
	 * ：归约节点
	 * @param output
	 *  ：数据输出节点
	 *
	 */
	public String generateConcourse(List<Element> children, Element input, Element reduce, Element output) {
		String code = "";
		String currentNodeId = "";
		String lastCode = "";
		Vector<Element> vector = new Vector<Element>();
		// 生成reduce和reduce到output的代码
		// input不为null
		if (input.attributeValue("value") != null && input.attributeValue("value") != "") {
			if (reduce != null) {
				code += ".reduceByKey(_" + reduce.attributeValue("value") + "_)";
				currentNodeId = reduce.attributeValue("id");
				while (!currentNodeId.equals(output.attributeValue("id"))) {
					for (Element ele : children) {
						if (ele.attribute("source") != null) {
							if (currentNodeId.equals(ele.attributeValue("source").toString())) {
								currentNodeId = ele.attributeValue("target");
								for (Element ele1 : children) {
									if (ele1.attributeValue("style") != null
											&& "rhombus".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										lastCode += ele1.attributeValue("value");
									}
									if (ele1.attributeValue("style") != null
											&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										vector.addElement(ele1);
									}
								}
							}
						}
					}
					if (currentNodeId.equals(output.attributeValue("id")))
						break;
				}
//				code += ".map( x =>( x._1, x._2" + lastCode + "))\n";
				if (!vector.isEmpty()) {
					for (Element element : vector) {
						String type = element.attributeValue("value").split("~")[0];
						String url = element.attributeValue("value").split("~")[1];
						String method = element.attributeValue("value").split("~")[2];
						code += "\n res.foreachRDD(rdd=>{\n 	rdd.foreach(x=>(" + map.get(type) + "(\"" + url + "\",\"" + method
								+ "\",x._1+\"-\"+x._2)))\n})\n";
					}
				}
			}
		} else {
			// input为null
			if (reduce != null) {
				code += ".reduce(_" + reduce.attributeValue("value") + "_)";
				currentNodeId = reduce.attributeValue("id");
				while (!currentNodeId.equals(output.attributeValue("id"))) {
					for (Element ele : children) {
						if (ele.attribute("source") != null) {
							if (currentNodeId.equals(ele.attributeValue("source").toString())) {
								currentNodeId = ele.attributeValue("target");
								for (Element ele1 : children) {
									if (ele1.attributeValue("style") != null
											&& "rhombus".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										code += ele1.attributeValue("value");
									}
									if (ele1.attributeValue("style") != null
											&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
											&& currentNodeId.equals(ele1.attributeValue("id"))) {
										vector.addElement(ele1);
									}
								}
							}
						}
					}
					if (currentNodeId.equals(output.attributeValue("id"))) {
						code += "\n";
						if (!vector.isEmpty()) {
							for (Element element : vector) {
								String type = element.attributeValue("value").split("~")[0];
								String url = element.attributeValue("value").split("~")[1];
								String method = element.attributeValue("value").split("~")[2];
								code += "res =" + map.get(type) + "(\"" + url + "\",\"" + method
										+ "\",res.toString).toInt))\n";
							}
						}
						break;
					}
				}
			}
		}
		return code;
	}

}
