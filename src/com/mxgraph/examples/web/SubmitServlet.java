package com.mxgraph.examples.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mxgraph.examples.util.BlancerUtil;
import com.mxgraph.examples.util.FileTransmissionUtil;
import com.mxgraph.examples.util.HttpMethod;
import com.mxgraph.examples.util.SubmitThread;

import sun.misc.Contended;

/**spark提交服务
 * spark
 * @author spark
 *
 */
public class SubmitServlet extends HttpServlet{
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
//		System.out.println(new Date());
		HttpSession session = request.getSession();
		String fileName = request.getParameter("filename");
		String projectName = (String)session.getAttribute("project");
		response.setContentType("text/html;charset=GB2312");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
//		String status = Constants.SESSION_STATUS.get(projectName);
		String status = Constants.SESSION_MANAGER.get(projectName).get("status");
		if(status.contains("打包成功")){
			//打包成功后进行spark的提交
//			Constants.SESSION_STATUS.put(projectName, "提交中...");
			//没有用
			BlancerUtil blancer = new BlancerUtil();
			String node = blancer.caculateNode();
			if(node!=null && node!=""){
				Constants.SESSION_MANAGER.get(projectName).put("master", node);
			}
			Constants.SESSION_MANAGER.get(projectName).put("status", "提交中...");
			
			//启动提交线程
			SubmitThread thread = new SubmitThread(fileName, projectName,null);
			//这块主要给演示用，这个是强制写死的不好，用于将应用提交到没有正在跑程序的集群
			boolean ans = HttpMethod.sendGet("http://localhost:4040/api/v1/applications", null)!=""
					|| HttpMethod.sendGet("http://localhost:4041/api/v1/applications", null)!="" ;
			if(!ans){
				SubmitThread thread1 = new SubmitThread(fileName, projectName, "10.108.167.33");
				Future future1 = Constants.POOL.submit(thread1);
			}
			Future future = Constants.POOL.submit(thread);
			//向前端返回“提交中”，前端收到信息后隔一段时间又访问本服务
			out.write("提交中...");
			//将plan中提到的新上传的SCA模型使用scp上传到tuscany集群的指定目录，集群中有自动部署程序会对传过去的SCA模型进行部署
			FileTransmissionUtil util = new FileTransmissionUtil();
			String jarsString = Constants.SESSION_MANAGER.get(projectName).get("jars");
			if(jarsString != null && jarsString != ""){
				String[] jars = Constants.SESSION_MANAGER.get(projectName).get("jars").split(";");
				for(String jar:jars){
					util.transfor(jar, "/home/spark/tuscany/domain/", node, "spark", "123456");
				}
			}	
		}else{
			////返回打包的状态，若是“提交中”，前端收到信息后隔一段时间又访问本服务，如果是“提交完成”，前端弹出窗口进行通知并去除gif遮罩
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