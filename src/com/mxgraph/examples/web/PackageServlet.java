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

/**
 * 打包类，用于将scala代码和接口调用类打包
 * @author spark
 *
 */
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
			//没有名字自动指定一个uuid
			projectName = UUID.randomUUID().toString().replaceAll("-", "");
			session.setAttribute("project", projectName);
			Constants.SESSION_MANAGER.put(projectName, new HashMap<String,String>());
		}else{
			projectName = (String)project; 
		}
//		String status = Constants.SESSION_STATUS.get(projectName);
		String status = Constants.SESSION_MANAGER.get(projectName).get("status");
		//如果状态为空或者是重试，或者是再打包提交一次的情况
		if(status==null || status=="" || status.indexOf("重试")>-1 || status.indexOf("提交成功")>-1){
//			Constants.SESSION_STATUS.put(projectName, "打包中");
			Constants.SESSION_MANAGER.get(projectName).put("status", "打包中");
			//启动打包线程进行打包
			PackageThread thread = new PackageThread(filename,projectName);
			Future future = Constants.POOL.submit(thread);
			//向前端返回“打包中”，前端收到信息后隔一段时间又访问本服务
			out.write("打包中");
		}else{
			//返回打包的状态，若是“打包中”，前端收到信息后隔一段时间又访问本服务，如果是“打包完成”，前端立即反问spark提交服务
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
