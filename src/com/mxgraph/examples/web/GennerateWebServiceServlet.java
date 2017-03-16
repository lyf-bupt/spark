package com.mxgraph.examples.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.zip.GZIPOutputStream;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.xml.ws.Endpoint;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.mxgraph.examples.util.BootWSTask;
import com.mxgraph.examples.util.Utills;


public class GennerateWebServiceServlet extends HttpServlet
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
			String code = request.getParameter("value");

			if (code != null && code.length() > 0)
			{
				HttpSession session = request.getSession(false); 
				String projectName = (String)session.getAttribute("project"); 
				String filename = code.substring(code.indexOf("class")+6,code.indexOf("{"));
				String URL = "http://"+Constants.LOCAL_HOST+":"+(19098+Constants.processVec.size())+"/"+projectName+"/"+filename;
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
                String methodString = "";
                String classFile = Constants.JAVA_FILE_PATH+projectName+"/target/classes/";
                try {
                    URLClassLoader loader=new URLClassLoader(new URL[]{new URL("file:"+classFile)});

                    Object name=loader.loadClass(projectName+"."+filename).newInstance();
                    Method[] methods = name.getClass().getMethods();
                    for(Method method:methods){
                    	result = "";
                        if(!method.toString().contains("Object")) {               
                            result += method.getReturnType()+" "+method.getName();
//                            System.out.println(code.indexOf(result));
                            String param = code.substring(code.indexOf(result)+result.length()+1,Utills.indexOfFromCurrent(code, code.indexOf(result)+result.length()+1, ")"));
                            String subMethod = code.substring(Utills.indexOfBeforeCurrent(code, code.indexOf(result), '\n')+1,Utills.indexOfFromCurrent(code, code.indexOf(result)+result.length(), "}"));
                            String[] params = param.split(",");
                            String replacement = "";
                            for(int i=0;i < params.length;i++){
                            	String str= params[i];
                            	replacement += "  @WebParam(name = \"in"+i+"\") "+str+",";
                            }
                            replacement = replacement.substring(0, replacement.length()-1);
//                            System.out.println(replacement);
                            String methodReplacement = subMethod.replace(param, replacement);
                            methodReplacement = "@WebMethod\n"+methodReplacement;
                            code = code.replace(subMethod, methodReplacement);
                            methodString += method.getName()+"/";
                        }                     
                    }
                    code = code.substring(0,code.length()-1)+"  public static void main(String[] args) {\n"+
                    				"   "+filename+" texi = new "+filename+"();\n"+
                    				"    Endpoint endpoint = Endpoint.publish(\""+URL+"\", texi);\n"+
                					"  }\n"+"}";
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                
                file.delete();
				//重新生成java实现文件
				javaFilePath = Constants.JAVA_FILE_PATH+projectName+"/src/main/java/"+projectName+"/"+filename+".java";
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
					   bw.write("import javax.jws.WebParam;\nimport javax.jws.WebMethod;\nimport javax.jws.WebService;\nimport javax.xml.ws.Endpoint;\n\n@WebService\n");
					   bw.write(code);
					   bw.flush();
					   bw.close();
					   fw.close();
					} catch (IOException e) {
					   e.printStackTrace();
					}
				
				 JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			        // 4.获取一个文件管理器
			    StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
			        // 5.文件管理器根与文件连接起来
			    Iterable it = javaFileManager.getJavaFileObjects(file);
			        // 6.创建编译任务
			    final String dir = Constants.JAVA_FILE_PATH+projectName+"/src/main/java/";
			    CompilationTask task = javaCompiler.getTask(null, javaFileManager, null, Arrays.asList("-d", dir), null, it);
			        // 7.执行编译
			    task.call();
			    javaFileManager.close();
			    
			    //执行class文件
			    final String className = projectName+"."+filename;
			    Callable<Integer> myCallable = new BootWSTask(dir,className);    // 创建MyCallable对象
		        FutureTask<Integer> ft = new FutureTask<Integer>(myCallable); //使用FutureTask来包装MyCallable对象 
		        Thread thread = new Thread(ft);
		        thread.start();
		        
		        if(!Constants.hookFlag){
			        Runtime.getRuntime().addShutdownHook(new Thread() {
			            public void run() {
			            	System.out.println("close processs");
			                for(Process process:Constants.processVec){
			                	process.destroy();
			                }
			            }
			        });
			        Constants.hookFlag = true;
		        }
		        URL = "Soap~"+URL+"~"+methodString;
		        URL = URL.substring(0, URL.length()-1);
                
				response.setContentType("text/html;charset=GB2312");
				response.setStatus(HttpServletResponse.SC_OK);
				PrintWriter out = response.getWriter();
				out.write(URL);
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