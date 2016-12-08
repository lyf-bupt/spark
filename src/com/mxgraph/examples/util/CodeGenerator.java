package com.mxgraph.examples.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.dom4j.Element;

public class CodeGenerator {
	// 对外接口的代码模板
	HashMap<String, String> map = null;

	public CodeGenerator(HashSet<String> set) {
		map = new HashMap<String, String>();
		String code = "";
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

	public String genneratePretreatment(List<Element> children, Element input, Element flatMap, Element reduce,
			Element output) {
		String currentNodeId = input.attributeValue("id");
		String code = "";
		if (flatMap != null) {
			// 有flatMap
			// 有input（有源）
			// 计算从source到faltMap
			if (input.attributeValue("value") != null && input.attributeValue("value") != "") {
				while (!currentNodeId.equals(flatMap.attributeValue("id"))) {
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
										code += ".map(x=>{\n" + " var value = " + map.get(type) + "(\"" + url + "\",\""
												+ method + "\"," + "x.split(\"@\")(1))\n"
												+ " var name = x.split(\"@\")(0)\n" + "name+\"@\"+value\n" + "})";
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
												+ "x.toString).toInt)\n";
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
											code += ".map(x=>{\n" + " var value =" + map.get(type) + "(\"" + url
													+ "\",\"" + method + "\"," + "x.split(\"@\")(1))\n"
													+ " var name = x.split(\"@\")(0)\n" + "name+\"@\"+value\n" + "})";
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
											code += ".map(x=>{\n" + " var value =" + map.get(type) + "(\"" + url
													+ "\",\"" + method + "\"," + "x.split(\"@\")(1))\n"
													+ " var name = x.split(\"@\")(0)\n" + "name+\"@\"+value\n" + "})";
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
											code += ".map(x =>" + map.get(type) + "(\"" + url + "\",\"" + method + "\","
													+ "x.toString).toInt)\n";
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
											code += ".map(x => " + map.get(type) + "(\"" + url + "\",\"" + method
													+ "\"," + "x.toString).toInt)\n";
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
				branchEnd = output;
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
												&& "triangle".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += ".reduceByKey(_" + ele1.attributeValue("value") + "_)";
										}
										if (ele1.attributeValue("style") != null
												&& "ellipse;shape=cloud".equals(ele1.attributeValue("style").toString())
												&& currentNodeId.equals(ele1.attributeValue("id"))) {
											code += "\n";
											String[] attris = ele1.attributeValue("value").split("~");
											String ans = "res"+i+".foreachRDD(rdd => {\n"
													+"	val y = rdd.collect()\n"
												    +"	y.foreach( pair =>{\n"
												    +"		val uid = pair._1\n"
												    +"		val clickCount = pair._2\n"
												    +"		val value=\""+attris[3]+"\"\n"
												    +"		val arg = uid+\"-\"+clickCount+\"-\"+value\n"
												    +"		var rate = 1.0\n"	
												    +"		rate = service.invokeWebService(\""+attris[1]+"\",\""+attris[2]+"\",arg).toDouble\n"
												    +"		out.write(uid+\"结果为：\"+ rate*100+\"%\\n\")\n"
												    +"		out.flush()\n"
												    +"	})\n"
												    +"})\n";
											code += ans;
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
			}
		}
		return code;
	}

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
