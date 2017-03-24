package com.mxgraph.examples.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mxgraph.examples.db.MethodModel;
import com.mxgraph.examples.db.MongoDbUtil;
import com.mxgraph.examples.db.MysqlUtil;
import com.mxgraph.examples.db.WebServiceDbModel;
import com.mxgraph.examples.util.GetAllTopic;

import net.sf.json.JSONObject;

/**
 * webservice服务，用于用户在定义webservice算子时选择已经入库了的webservice
 * @author spark
 *
 */
public class WebServiceServlet extends HttpServlet {

	/**
	 * 从数据库中读取出webservice列表
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String result = "";
		response.setContentType("text/html;charset=gb2312");

		PrintWriter out = response.getWriter();

		request.setCharacterEncoding("gb2312");

		MysqlUtil db = new MysqlUtil();
		db.getConn();
		//从数据库中读取出webservice列表
		Vector<WebServiceDbModel> ne = db.getAllType();
		int i = 0;
		for (WebServiceDbModel obj : ne) {
			JSONObject json = JSONObject.fromObject(obj);// 将java对象转换为json对象
			String str = json.toString();// 将json对象转换为字符串
			result += str;
			i++;
			if (i < ne.size())
				result += "*";
		}
		db.close();
		if (result != null && result != "") {
			out.println(result);
		} else {
			out.println("error!");
		}

		out.close();
	}
	
	/**
	 * 从数据库中读取出已选的webservice对应的方法列表
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
			String result = "";
			String service = request.getParameter("serviceId");
			response.setContentType("text/html;charset=GB2312");
			PrintWriter out = response.getWriter();
			int serviceId = Integer.parseInt(service);
			MysqlUtil db = new MysqlUtil();
			db.getConn();
			Vector<MethodModel> ne = db.getMethods(serviceId);
			int i = 0;
			for (MethodModel obj : ne) {
				JSONObject json = JSONObject.fromObject(obj);// 将java对象转换为json对象
				String str = json.toString();// 将json对象转换为字符串
				result += str;
				i++;
				if (i < ne.size())
					result += "*";
			}
			db.close();
			if (result != null && result != "") {
				out.println(result);
			} else {
				out.println("error!");
			}
			out.close();
		}
	}
}
