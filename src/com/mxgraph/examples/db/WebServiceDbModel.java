/*
 * webService的模型类
 */
package com.mxgraph.examples.db;

public class WebServiceDbModel {
	private int id;
	/** 名字 */
	private String name;  
	/** 父ID */
	private int parentid; 
	/** 类型，包括SOAP、REST、jsonRPC */
	private String type; 
	private String URL;
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentid() {
		return this.parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	public String getURL() {
		return this.URL;
	}
	public void setURL(String uRL) {
		this.URL = uRL;
	}
	
	

}
