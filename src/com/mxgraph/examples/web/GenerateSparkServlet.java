package com.mxgraph.examples.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mxgraph.examples.util.CodeGenerator;
import com.mxgraph.examples.util.ContentParser;
import com.mxgraph.examples.util.PackageAndSubmitThread;
import com.mxgraph.examples.util.ParseSCAModelUtil;
import com.mxgraph.examples.util.TemplateGenerator;

public class GenerateSparkServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
		{
			String filename = request.getParameter("filename");
			String xml = request.getParameter("xml");
			HttpSession session = request.getSession();
			Object project = session.getAttribute("project");
			String projectName;
			if(project==null){
				projectName = UUID.randomUUID().toString().replaceAll("-", "");
				session.setAttribute("project", projectName);
				Constants.SESSION_MANAGER.put(projectName, new HashMap<String,String>());
			}else{
				projectName = (String)project; 
			}
			xml = xml.replaceAll("localhost", "10.109.253.71");
			
			if (xml != null && xml.length() > 0)
			{	
				//解析前端传输的xml
				Constants.FILE_NAME = filename;
				Constants.GENERATE_RESULT = "";
				byte[] xml1 = URLDecoder.decode(xml, "UTF-8").getBytes("UTF-8");
				System.out.println(new String(xml1));
				Document document = null;
				Element root = null;
				Element input = null;
				Element output = null;
				Element flatMap = null;
				Element reduce = null;
				HashSet<String> wsType = new HashSet<String>();
				HashSet<String> wsURL = new HashSet<String>();
				List<Element> children = null;
				SAXReader saxReader = new SAXReader(); 
				try {
					document = saxReader.read(new ByteArrayInputStream(xml1));
					root = document.getRootElement();
					children = root.elements();
					for(Element ele:children){
						if(ele.attribute("style")!=null){
							if("ellipse".equals(ele.attributeValue("style").toString())){
								input = ele;
							}else if("ellipse;shape=doubleEllipse".equals(ele.attributeValue("style").toString())){
								output = ele;
							}else if("shape=step".equals(ele.attributeValue("style").toString())){
								flatMap = ele;
							}else if("triangle".equals(ele.attributeValue("style").toString())){
								reduce = ele;
							}else if("ellipse;shape=cloud".equals(ele.attributeValue("style").toString())){
								String type = ele.attributeValue("value").toString().split("~")[0];
								String URL = ele.attributeValue("value").toString().split("~")[1];
								if(!wsType.contains(type)){
									wsType.add(type);
								}
								if(URL.contains("10.109.253.71") && !wsURL.contains(URL)){
									wsURL.add(URL.replaceAll("10.109.253.71", "localhost"));
								}
							}
						}
					}
//				System.out.println(new String(xml1));
					if (flatMap != null) {
						System.out.println("flatMap：" + flatMap.attributeValue("id"));
					}
					if (reduce != null) {
						System.out.println("reduce: " + reduce.attributeValue("id"));
					}
					if (!wsType.isEmpty()) {
						for (String str : wsType) {
//							System.out.println("type：" + str);
						}
					}
					System.out.println("input id:" + input.attributeValue("id") + " 源地址："
							+ input.attributeValue("value") + " output id:" + output.attributeValue("id") + " 输出目录："
							+ output.attributeValue("value"));		
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				
				//解析代码中含有的SCA模型并存储到sessionManager中
				if(!wsURL.isEmpty()){
					ParseSCAModelUtil modelUtil = new ParseSCAModelUtil(wsURL, projectName);
					modelUtil.parseModels();
				}
				
				//生成core code
				String coreCode = "var res = input";
				CodeGenerator gen = new CodeGenerator(wsType);
				String pre = gen.genneratePretreatment(children, input, flatMap, reduce, output);
				if(pre!=null&&pre!=""){
					coreCode += gen.genneratePretreatment(children, input, flatMap, reduce, output);
				}else{
					coreCode = "";
				}
				coreCode += gen.generateBranch(children, input, output,flatMap, reduce);
				coreCode +=gen.generateConcourse(children, input, reduce, output);
				System.out.println(coreCode);
				
				
				//scala模板
				TemplateGenerator tGen = new TemplateGenerator(wsType);
				String template = tGen.generateTemplate(coreCode, filename, input, reduce);
//				System.out.println(template);
				String scalaFilePath = Constants.SCALA_FILE_PATH+filename+".scala";
				File file = new File(scalaFilePath);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				try {
				   file.createNewFile();
				} catch (IOException e) {
				   e.printStackTrace();
				}
				
				//写scala文件
				try {
				   FileWriter fw = new FileWriter(file, true);
				   BufferedWriter bw = new BufferedWriter(fw);
				   bw.write(template);
				   bw.flush();
				   bw.close();
				   fw.close();
				} catch (IOException e) {
				   e.printStackTrace();
				}
				
				//创建日志文件
				//陈德冲学长毕设封闭 2016/6/7
//				String logPath = Constants.LOG_PATH+filename+".txt";
//				File logFile = new File(logPath);
//				if (!logFile.getParentFile().exists()) {
//					logFile.getParentFile().mkdirs();
//				}
//				try {
//					logFile.createNewFile();
//				} catch (IOException e) {
//				   e.printStackTrace();
//				}
				
				response.setContentType("text/html;charset=GB2312");
				PrintWriter out = response.getWriter();
				
				out.println("代码生成成功，打包中...");
				out.close();
				if(Constants.GENERATE_RESULT.contains("失败")){
//					file.delete();
				}
//				out.println(Constants.GENERATE_RESULT);
				System.out.println(Constants.GENERATE_RESULT);
				out.close();

//				//打包并写日志,已废除
//				//暂时封闭by zhou in 11-9
//				try{
//					Runtime r=Runtime.getRuntime();
//					//用shell脚本打包
//				    Process p=r.exec("/home/zhou/shell/packageSpark.sh");
//				    InputStream in = p.getInputStream();  
//		            BufferedReader read = new BufferedReader(new InputStreamReader(in)); 
//		            String result = null;
//					Boolean successFlag = false;
//					FileWriter fw = new FileWriter(logFile, true);
//					BufferedWriter bw = new BufferedWriter(fw);
//		            while(null!=( result=read.readLine())){
//						//去除终端返回的无效字符
//						result = result.replace("[0m", "");
//						result = result.replace("[2K", "");
//						result = result.replace("[33m", "");
//						result = result.replace("[31m", "");
//						result = result.replace("[32m", "");
//						result = result.replace("[A", "");
//						//写入Log文件
//						bw.write(result+"\n");
//		            	bw.flush();
//		            	System.out.println(result);
//		            	if(result.contains("success")){
//		            		successFlag = true;
//		            	}else if(result.contains("error")){
//		            		successFlag = false;
//		            	}
//		            }
//		            bw.close();
//					fw.close();
//					response.setContentType("text/html;charset=GB2312");
//    				PrintWriter out = response.getWriter();
//    				if(successFlag){
//    					System.out.println("打包成功");
//    					String[] cmd ={"/bin/sh", "-c", "spark-submit --class \""+filename+"\" --master "+Constants.MASTER+" "+Constants.JAR_LOCATION};
//    					String cmd1 = "ifconfig";
//    					Runtime run=Runtime.getRuntime();
//    					Process pro=run.exec(cmd);
////    					pro.waitFor();
//    				    InputStream in2 = pro.getInputStream();  
//    				    InputStream in3 = pro.getErrorStream();
//    		            BufferedReader read2 = new BufferedReader(new InputStreamReader(in3)); 
//    		          
//    		            String result2 = null;
//    		            int exceptionFlag = 0;
//    		            int flag = 0;
//    		            while(null!=( result2=read2.readLine())){
//    		            	System.out.println(result2);
//    		            	if(result2.contains("Exception")){
//    		            		exceptionFlag=1;
//    		            		break;
//    		            	}
//    		            	if(result2.contains("Finished job streaming")){
//    		            		flag=0;
//    		            		out.println("打包成功！！运行成功！！");
//    		            		out.close();
//    		            	}
//    		            }
//    		            pro.waitFor();
//    		            if(exceptionFlag==1)
//    		            	flag=1;
//    		            if(flag!=1){
//    		            	out.println("打包成功！！运行成功！！");
//    		            }
//    		            else{
//    		            	out.println("打包成功但运行失败！！请联系Spark管理员！！");
//    		            }
//    				}else{
//    					out.println(" 打包失败！");
//    					out.println("无法生成jar包！请联系管理员或服务提供商！！");
//    					file.delete();
//    				}
//    				out.close();
//				}catch (Exception e) {  
//		            e.printStackTrace();  
//		        } 

			}
			else
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}

}
