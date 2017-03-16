package com.mxgraph.examples.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhou.redic.client.RedisClient;;

public class CodeAndResultServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;
	private long lastTimeFileSize = 0;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
			String codeFile = null;
			String outputFile = null;
			if (Constants.FILE_NAME != null) {
				outputFile = Constants.OUTPUT_PATH;
				// 陈德冲学长毕设
//				Constants.FILE_NAME = "F99";
				codeFile = Constants.SCALA_FILE_PATH + Constants.FILE_NAME + ".scala";
			}
			String type = request.getParameter("type");
			response.setContentType("text/html;charset=GB2312");
			PrintWriter out = response.getWriter();
			if (("init").equals(type)) {
				Constants.FILE_NAME = null;
			} else if (("code").equals(type)) {
				if (codeFile != null) {
					File file = new File(codeFile);
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(file));
						String tempString = null;
						// 一次读入一行，直到读入null为文件结束
						out.println("<p>");
						while ((tempString = reader.readLine()) != null) {
							// 显示行号
							out.println(tempString + "</br>");
						}
						out.println("</p>");
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException e1) {
							}
						}
					}
				} else {
					out.println("未生成代码，请先点击文件->生成jar包之后重试");
				}
			} else if (("result").equals(type)) {
				if (outputFile != null) {
					File file = new File(outputFile);
					RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
					randomAccessFile.seek(lastTimeFileSize);
					try {
						
						String tmp = "";
	                    while ((tmp = randomAccessFile.readLine()) != null) {
	                        // 输出文件内容
	                        out.print(new String(tmp.getBytes("ISO8859-1")));
	                        out.print("<br>");
	                    }	
	                    lastTimeFileSize = randomAccessFile.length();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (randomAccessFile != null) {
							try {
								randomAccessFile.close();
							} catch (IOException e1) {
							}
						}
					}
				} else {
					out.println("未生成并执行jar包，请先点击文件->生成jar包之后重试");
				}
			} else if (("visio").equals(type)) {
				String time = request.getParameter("time");
				if(time!=null){
					Map<String, String> map = RedisClient.getClient().hgetAll(time);
					for (String key : map.keySet()) {
						out.println(key+"-"+map.get(key));
//						out.println("=>" + rate*100+"%");
					}
				}else{
					Set<String> keys = RedisClient.getClient().keys("*");
					for (String key : keys) {
						out.println(key);
//						out.println("=>" + rate*100+"%");
					}
				}
			}
			out.close();
		}
	}
}
