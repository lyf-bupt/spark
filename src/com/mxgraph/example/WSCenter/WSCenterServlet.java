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
			MysqlUtil mysql = new MysqlUtil();
			Vector<WebServiceDbModel> result =  mysql.getAllType();
			String outputStr = "[";
			for(WebServiceDbModel ws:result){
				if(ws.getType() !=null ){
					String str = "{\n"
							+"\"name\":\""+ws.getName()+"\",\n"
							+"\"type\":\""+ws.getType()+"\",\n"
							+"\"URL\":\""+ws.getURL()+"\",\n"
							+"\"state\":\"stop\"\n}";
					outputStr += str+",";
				}
			}
			outputStr = outputStr.substring(0,outputStr.length()-1);
			outputStr += "]";
			response.setContentType("text/JSON;charset=GB2312");
			PrintWriter out = response.getWriter();
			out.println(outputStr);
			out.close();
	}
}
