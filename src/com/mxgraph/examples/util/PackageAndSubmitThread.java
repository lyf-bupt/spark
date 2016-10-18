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

public class PackageAndSubmitThread implements Callable<Object>{
	private Boolean successFlag;
	private String result;
	private String filename;
	private PrintWriter out;
	
	 public PackageAndSubmitThread(String filename,PrintWriter out){
		 this.successFlag = false;
		 this.filename = filename;
		 this.out = out;
	 }

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		//创建日志文件
		String logPath = Constants.LOG_PATH+filename+".txt";
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
		    Process p=r.exec("/home/zhou/shell/packageSpark.sh");
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
				//写入Log文件
				bw.write(result+"\n");
            	bw.flush();
            	if(result.contains("success")){
            		successFlag = true;
            	}else if(result.contains("error")){
            		successFlag = false;
            	}
            }
			if(successFlag){
				System.out.println("打包成功!");
				String[] cmd ={"/bin/sh", "-c", "spark-submit --jars $(echo /home/zhou/genSpark/test/lib/*.jar | tr ' ' ',')  --class \""+filename+"\" --master "+Constants.MASTER+" "+Constants.JAR_LOCATION};
				String cmd1 = "ifconfig";
				Runtime run=Runtime.getRuntime();
				Process pro=run.exec(cmd);
//				pro.waitFor();
			    InputStream in2 = pro.getInputStream();  
			    InputStream in3 = pro.getErrorStream();
	            BufferedReader read2 = new BufferedReader(new InputStreamReader(in3)); 
	          
	            String result2 = null;
	            int exceptionFlag = 0;
	            int flag = 0;
	            while(null!=( result2=read2.readLine())){
	            	bw.write(result2+"\n");
	            	bw.flush();
	            	if(result2.contains("Exception")){
	            		exceptionFlag=1;
	            		break;
	            	}
	            	if(result2.contains("Finished job streaming")){
	            		flag=0;
	            		result = "打包成功！！运行成功！！";
	            		out.println(result);
	            		out.flush();
	            		out.close();
	            		if(!Constants.GENERATE_RESULT.contains("成功")){
	            			Constants.GENERATE_RESULT = result;
	            		}
	            	}
	            }
	            flag = pro.waitFor();
	            if(exceptionFlag==1)
	            	flag=1;
	            if(flag!=1){
	            	result = "打包成功！！运行成功！！";
	            }
	            else{
	            	Constants.GENERATE_RESULT =  "打包成功但运行失败！！请联系Spark管理员！！";
	            }
			}else{
				result = " 打包失败！";
				result +="无法生成jar包！请联系管理员或服务提供商！！";
			}
			bw.close();
			fw.close();
			Constants.GENERATE_RESULT = result;
			return result;
	}

}
