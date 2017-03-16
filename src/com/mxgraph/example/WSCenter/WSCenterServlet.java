/*
 * 读取数据库中的web service列表
 * 页面中选取web service时先选取webservice再选取相应的方法
 * 对应到程序就是先使用get得到webservice列表，再用post得到所选webservice的方法列表
 */
package com.mxgraph.example.WSCenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.util.JSON;
import com.mxgraph.examples.db.MethodModel;
import com.mxgraph.examples.db.MysqlUtil;
import com.mxgraph.examples.db.WebServiceDbModel;
import com.mxgraph.examples.web.Constants;
import com.zhou.redic.client.RedisClient;


public class WSCenterServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			MysqlUtil mysql = new MysqlUtil();						  //新建mysql工具类
			Vector<WebServiceDbModel> result =  mysql.getAllType();   //数据库中取出所有的webService
			String outputStr = "[";
			for(WebServiceDbModel ws:result){
				String str = "{\n"                            //构造json数据
						+"\"id\":\""+ws.getId()+"\",\n"
						+"\"name\":\""+ws.getName()+"\",\n"
						+"\"type\":\""+ws.getType()+"\",\n"
						+"\"URL\":\""+ws.getURL()+"\",\n"
						+"\"parentid\":\""+ws.getParentid()+"\",\n"
						+"\"state\":\"stop\"\n}";
				outputStr += str+",";
			}
			if(result.size()>0){
				outputStr = outputStr.substring(0,outputStr.length()-1);
			}
			outputStr += "]";
			response.setContentType("text/JSON;charset=GB2312");
			PrintWriter out = response.getWriter();
			out.println(outputStr);
			out.close();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			String parentId = request.getParameter("serviceId");
			int id = Integer.parseInt(parentId);
			MysqlUtil mysql = new MysqlUtil();
			Vector<MethodModel> result =  mysql.getMethods(id);     //得到所选webservice的方法列表
			String outputStr = "[";
			for(MethodModel ws:result){
				if(ws.getId() != -1 ){
					String str = "{\n"
							+"\"id\":\""+ws.getId()+"\",\n"
							+"\"name\":\""+ws.getName()+"\",\n"
							+"\"Method\":\""+ws.getMethod()+"\",\n"
							+"\"URL\":\""+ws.getURL()+"\",\n"
							+"\"state\":\"stop\"\n}";
					outputStr += str+",";
				}
			}
			if(result.size()>0){
				outputStr = outputStr.substring(0,outputStr.length()-1);
			}
			outputStr += "]";
			response.setContentType("text/JSON;charset=GB2312");
			PrintWriter out = response.getWriter();
			out.println(outputStr);
			out.close();
	}
}
