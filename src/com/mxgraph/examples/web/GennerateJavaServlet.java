package com.mxgraph.examples.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class GennerateJavaServlet extends HttpServlet
{

	/**
	 * 用于将简单SCA模型编辑框内的代码生成真正的webservice
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
			String code = request.getParameter("value");

			if (code != null && code.length() > 0)
			{
				HttpSession session = request.getSession(false); 
				String projectName = (String)session.getAttribute("project"); 
				String filename = code.substring(code.indexOf("class")+6,code.indexOf("{"));
				//生成java文件
				String javaFilePath = Constants.JAVA_FILE_PATH+projectName+"/src/main/java/"+projectName+"/"+filename+".java";
				File file = new File(javaFilePath);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				try {
				   file.createNewFile();
				} catch (IOException e) {
				   e.printStackTrace();
				}
				//写入java代码
				try {
					   FileWriter fw = new FileWriter(file, true);
					   BufferedWriter bw = new BufferedWriter(fw);
					   bw.write("package "+projectName+";\n");
					   bw.write(code);
					   bw.flush();
					   bw.close();
					   fw.close();
					} catch (IOException e) {
					   e.printStackTrace();
					}
				
				//Ant将生成的java文件编译成class文件
				File buildFile=new File(Constants.JAVA_FILE_PATH+projectName+"/build.xml");
                //创建一个ANT项目
                Project p=new Project();
              //创建一个默认的监听器,监听项目构建过程中的日志操作
                DefaultLogger consoleLogger = new DefaultLogger();
                consoleLogger.setErrorPrintStream(System.err);
                consoleLogger.setOutputPrintStream(System.out);
                consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
                p.addBuildListener(consoleLogger);
                try{
                    p.fireBuildStarted();
                    //初始化该项目
                    p.init();
                    ProjectHelper helper=ProjectHelper.getProjectHelper();
                    //解析项目的构建文件
                    helper.parse(p, buildFile);
                    //执行项目的某一个目标
                    p.executeTarget("compile");
                    p.fireBuildFinished(null);
	           }catch(BuildException be){
	                    p.fireBuildFinished(be);
	           }
                
                //运用类加载器动态加载刚才生成的class文件
                String result = "";
                String classFile = Constants.JAVA_FILE_PATH+projectName+"/target/classes/";
                try {
                    URLClassLoader loader=new URLClassLoader(new URL[]{new URL("file:"+classFile)});

                    Object name=loader.loadClass(projectName+"."+filename).newInstance();
                    //读出webservice的方法，并将方法列表返回给前端供用户选择所需调用的方法
                    Method[] methods = name.getClass().getMethods();
                    for(Method method:methods){
                        if(!method.toString().contains("Object")) {
                            result += "	"+method.getReturnType();
                            result += " ";
                            result += method.getName()+"(";       
                            int n = method.getParameterCount();
                            for(int i=0;i<n;i++){
                                String typeName = method.getParameters()[i].getParameterizedType().getTypeName();
                                String paraName = method.getParameters()[i].getName();
                                result += typeName.substring(typeName.lastIndexOf(".")+1)+" "+paraName;
                                if(i!=(n-1)){
                                    result += ",";
                                }
                            }
                            result += ");\n";
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                
                //生成接口文件
                file.delete();
                String interfaceCode = "package "+projectName+";\n"
                		+"public interface "+filename+" {\n"
                		+result+"}";
//                System.out.print(interfaceCode);
                String interfacePath = Constants.JAVA_FILE_PATH+projectName+"/src/main/java/"+projectName+"/"+filename+".java";
				File interfaceFile = new File(interfacePath);
				try {
					interfaceFile.createNewFile();
				} catch (IOException e) {
				   e.printStackTrace();
				}
				try {
					   FileWriter fw = new FileWriter(interfaceFile, true);
					   BufferedWriter bw = new BufferedWriter(fw);
					   bw.write(interfaceCode);
					   bw.flush();
					   bw.close();
					   fw.close();
					} catch (IOException e) {
					   e.printStackTrace();
					}
				code = code.replaceAll(filename, filename+"Impl implements "+filename);
				//加上注解生成真正的webservice
				code = code.replaceAll("public class", "@Service("+filename+".class)\npublic class");
//				System.out.print(code);
				
				//重新生成java实现文件
				javaFilePath = Constants.JAVA_FILE_PATH+projectName+"/src/main/java/"+projectName+"/"+filename+"Impl.java";
				file = new File(javaFilePath);
				try {
				   file.createNewFile();
				} catch (IOException e) {
				   e.printStackTrace();
				}
				//写入java代码
				try {
					   FileWriter fw = new FileWriter(file, true);
					   BufferedWriter bw = new BufferedWriter(fw);
					   bw.write("package "+projectName+";\n");
					   bw.write("import org.osoa.sca.annotations.Service;\nimport org.osoa.sca.annotations.Reference;\n\n");
					   bw.write(code);
					   bw.flush();
					   bw.close();
					   fw.close();
					} catch (IOException e) {
					   e.printStackTrace();
					}

               
                
				response.setContentType("text/html;charset=GB2312");
				response.setStatus(HttpServletResponse.SC_OK);

				PrintWriter out = response.getWriter();
				String encoding = request.getHeader("Accept-Encoding");

				out.write("文件 "+filename+".java 生成成功！！！！");
				out.flush();
				out.close();
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