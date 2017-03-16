/*
 * 数据库的method的模型类
 */
package com.mxgraph.examples.db;

public class MethodModel {
	private int id; 
	private String name;     //方法中文名
	private String URL;
	private String method;   //方法名
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
}
