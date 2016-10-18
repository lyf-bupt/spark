package com.mxgraph.examples.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mxgraph.examples.db.MysqlUtil;
import com.mxgraph.examples.subscribe.singleSubscribe;
import com.mxgraph.examples.util.GetAllTopic;

public class TopicServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			response.setContentType("text/html;charset=gb2312");
		
		     PrintWriter out = response.getWriter();
		
		     request.setCharacterEncoding("gb2312");
				
				String topics = "";
				MysqlUtil db = new MysqlUtil();
				String sources = db.getAllDataSource();

		    	 out.println(sources);
			     out.close();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
		{
			String type = request.getParameter("type");
			String source = request.getParameter("source");
//			System.out.println(type);
			if("view".equals(type)){
				int dataSource = Integer.parseInt(source);
				MysqlUtil db = new MysqlUtil();
				String fields = db.getModels(dataSource);
				response.setContentType("text/html;charset=GB2312");
				PrintWriter out = response.getWriter();

				out.write(fields);
				out.flush();
				out.close();
			}else if("example".equals(type)){
				int dataSource = Integer.parseInt(source);
				MysqlUtil db = new MysqlUtil();
				String fields = db.getFields(dataSource);
				String exam = db.getExamples(dataSource);
				String ans = fields+";"+exam;
				response.setContentType("text/html;charset=GB2312");
				PrintWriter out = response.getWriter();

				out.write(ans);
				out.flush();
				out.close();
			}else{
				int dataSource = Integer.parseInt(source);
				MysqlUtil db = new MysqlUtil();
				String fields = db.getFields(dataSource);
				response.setContentType("text/html;charset=GB2312");
				PrintWriter out = response.getWriter();

				out.write(fields);
				out.flush();
				out.close();
			}
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}
}
