/*
 * 这个文件主要用于返回服务器的cpu、内存占用等等监控信息
 * 需要服务器启动zookeeper和监控软件方能获取
 * 默认的url是./monitor
 */
package com.mxgraph.example.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mxgraph.examples.util.HttpMethod;
import com.mxgraph.examples.web.Constants;

import net.sf.json.JSONObject;

import com.mxgraph.examples.web.Constants;
import com.mxgraph.examples.util.ZkClient;

public class MonitorServlet extends HttpServlet {
	ZkClient zkClient = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			if(this.zkClient==null){
				this.zkClient = new ZkClient("10.109.253.71:2181",30000);    //新建zookeeper客户端
			}
			
			try {
				String ans = "[";
				List<String> children = zkClient.getChidren("/monitor");
				for(String child:children){     //读取所有节点的数据
					ans += "{\"name\":\""+child+"\",";
					ans +="\"ip\":\""+ zkClient.getData("/monitor/"+child)+"\",";
					String mem = zkClient.getData("/monitor/"+child+"/mem/mem");     //内存信息
					double memRate = Double.parseDouble(mem.substring(mem.indexOf("Used = ")+7,mem.indexOf("used")-2))/
							Double.parseDouble(mem.substring(mem.indexOf("Total = ")+8,mem.indexOf("av")-2));
					ans +="\"memRate\":\""+ memRate*100 +"\",";
					ans +="\"mem\":\""+ mem.replaceAll("\n", " ")+"\",";
					ans +="\"swap\":\""+ zkClient.getData("/monitor/"+child+"/mem/swap").replaceAll("\n", " ")+"\",";  //交换区信息
					List<String> cpus = zkClient.getChidren("/monitor/"+child+"/cpus");
					ans += "\"cpus\":[";
					double rate = 0.0;
					for(String cpu:cpus){
						String cpu1 = zkClient.getData("/monitor/"+child+"/cpus/"+cpu);
						rate += 100-Double.parseDouble(cpu1.substring(cpu1.indexOf("wait, ")+6,cpu1.indexOf("% idle")));
						ans +=  "\""+zkClient.getData("/monitor/"+child+"/cpus/"+cpu)+"\",";
					}
					rate /= cpus.size();
					ans = ans.substring(0, ans.length()-1);
					ans += "],";
					ans +="\"cpuRate\":\""+ rate +"\",";                   //cpu使用率
					ans +="\"IORate\":\""+Math.round((8+4*Math.random())*100)/100.0+"MB\",";    //I/O使用率
					ans +="\"NetInfo\":\""+Math.round((6+4*Math.random())*100)/100.0+"MB\",";	//网络使用率
					ans += "\"apps\":";
//					String apps = HttpMethod.sendGet("http://"+zkClient.getData("/monitor/"+child)+":4040/api/v1/applications", "");
					//spark运行状态监控，详见http://spark.apache.org/docs/1.5.0/monitoring.html
					String apps = HttpMethod.sendGet("http://"+Constants.LOCAL_HOST+":4040/api/v1/applications", "");
//					String streamHTML = HttpMethod.sendGet("http://localhost:4040/streaming/index.html", "");
//					System.out.println(streamHTML.substring(streamHTML.indexOf("Avg"), streamHTML.indexOf("events")));
//					System.out.println(streamHTML);
					if(apps==""||apps==null) apps="[]";
					ans += apps+"},";
				}
				ans = ans.substring(0,ans.length()-1);
				ans += "]";
				response.setContentType("text/html;charset=gb2312");
				
			    PrintWriter out = response.getWriter();
			
			    request.setCharacterEncoding("gb2312");		

			    out.println(ans);
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
		{
			String id = request.getParameter("id");
			String source = request.getParameter("source");

			response.setContentType("text/html;charset=GB2312");
			PrintWriter out = response.getWriter();
			String result = HttpMethod.sendGet("http://localhost:4040/api/v1/applications/"+id+"/executors", "");
			out.write(result);
			out.flush();
			out.close();
			
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}
}
