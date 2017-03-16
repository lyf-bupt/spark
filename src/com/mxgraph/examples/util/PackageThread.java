package com.mxgraph.examples.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import com.mxgraph.examples.web.Constants;

/**
 * 打包线程
 * @author spark
 *
 */
public class PackageThread implements Callable<Object>{
	
	private Boolean successFlag;
	private String result;
	private String filename;
	private String project;
	
	public PackageThread(String filename,String project) {
		// TODO Auto-generated constructor stub
		this.successFlag = false;
		this.result="";
		this.filename = filename;
		this.project = project;
	}
	
	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		String logPath = Constants.LOG_PATH+filename+".txt";
		//日志文件
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		try {
			logFile.createNewFile();
		} catch (IOException e) {
		   e.printStackTrace();
		}
		//打包并写日志
		//暂时封闭by zhou in 11-9
		Runtime r=Runtime.getRuntime();
		//用shell脚本打包
		Process p=r.exec("/home/spark/shell/packageSpark.sh");
		InputStream in = p.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in)); 
        String result = null;
        FileWriter fw = new FileWriter(logFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
        while(null!=( result=read.readLine())){
			//去除终端返回的无效字符     	
			result = result.replace("[0m", "");
			result = result.replace("[2K", "");
			result = result.replace("[33m", "");
			result = result.replace("[31m", "");
			result = result.replace("[32m", "");
			result = result.replace("[A", "");
			System.out.println(result);
			//写入Log文件
			bw.write(result+"\n");
            bw.flush();
            if(result.contains("success")){
            	successFlag = true;
            	break;
            }else if(result.contains("error")){
            	successFlag = false;
            	break;
            }
        }
        if(successFlag==true){
//        	Constants.SESSION_STATUS.put(this.project, "打包成功，提交中...");
        	Constants.SESSION_MANAGER.get(this.project).put("status", "打包成功，提交中...");
        }else{
//        	Constants.SESSION_STATUS.put(this.project, "打包失败，请联系平台管理员");
        	Constants.SESSION_MANAGER.get(this.project).put("status", "打包失败，请联系平台管理员");
        }
		return successFlag;
	}

}
