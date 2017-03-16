package com.mxgraph.examples.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mxgraph.examples.util.PackageAndSubmitThread;
import com.mxgraph.examples.util.PackageThread;

public class PackageServlet extends HttpServlet{
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
//		System.out.println(new Date());
		HttpSession session = request.getSession();
		String filename = request.getParameter("filename");
		response.setContentType("text/html;charset=GB2312");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		Object project = session.getAttribute("project");
		String projectName;
		if(project==null){
			projectName = UUID.randomUUID().toString().replaceAll("-", "");
			session.setAttribute("project", projectName);
			Constants.SESSION_MANAGER.put(projectName, new HashMap<String,String>());
		}else{
			projectName = (String)project; 
		}
//		String status = Constants.SESSION_STATUS.get(projectName);
		String status = Constants.SESSION_MANAGER.get(projectName).get("status");
		if(status==null || status=="" || status.indexOf("重试")>-1 || status.indexOf("提交成功")>-1){
//			Constants.SESSION_STATUS.put(projectName, "打包中");
			Constants.SESSION_MANAGER.get(projectName).put("status", "打包中");
			PackageThread thread = new PackageThread(filename,projectName);
			Future future = Constants.POOL.submit(thread);
			out.write("打包中");
		}else{
			out.write(status);
			if(status.contains("失败")){
				Constants.SESSION_MANAGER.get(projectName).put("status", "待重试");
			}
		}
		
		out.flush();
		out.close();
//		System.out.println(new Date());
	}

}
