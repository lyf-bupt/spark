package com.mxgraph.examples.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * 新建项目后台相应，就是第一个对话弹出后后台的响应程序
 * @author spark
 *
 */
public class AddProjectServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;

	/**
	 * 新建项目后台响应程序
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
			String projectName = request.getParameter("project");
			//会话管理中新建一个会话
			Constants.SESSION_MANAGER.put(projectName, new HashMap<String,String>());

			if (projectName != null && projectName.length() > 0) {
				response.setContentType("text/html;charset=GB2312");
				response.setStatus(HttpServletResponse.SC_OK);

				PrintWriter out = response.getWriter();
				String encoding = request.getHeader("Accept-Encoding");
				
				//新建一个以项目名命名的文件夹
				String projectFolder = Constants.JAVA_FILE_PATH + "/" + projectName + "/";
				System.out.println(projectName);
				//复制tuscany文件到新建的文件夹中
				String tuscanyTmp = "tuscany_template/";
				File sourceFile = new File(tuscanyTmp);
				File file = new File(projectFolder);
				// 如果文件夹不存在则创建
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
				} else {
					out.write("项目名称重复，请重新输入！！");
				}
//				copyFolder(sourceFile, file);
				HttpSession session = request.getSession();// 没有Session就新建一个
				//这个好像后来没在用了
				session.setAttribute("project", projectName);// 在服务器端存储"键-值对"

				out.write("ok");
				out.flush();
				out.close();
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}
	
	/**
	 * 深度复制文件夹
	 * @param src 源文件夹
	 * @param dest 目的文件夹
	 * @throws IOException
	 */
	private void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// 递归复制
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}

}