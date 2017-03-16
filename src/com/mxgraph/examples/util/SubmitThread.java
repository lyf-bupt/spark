package com.mxgraph.examples.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import com.mxgraph.examples.web.Constants;

/**
 * 提交应用线程
 * @author spark
 *
 */
public class SubmitThread implements Callable<Object>{
	private Boolean successFlag;
	private String result;
	private String filename;
	private String project;
	private String master;
	
	public SubmitThread(String filename,String project,String master) {
		// TODO Auto-generated constructor stub
		this.successFlag = false;
		this.result = "";
		this.filename = filename;
		this.project = project;
		//master节点要么是从外面传进来要么是从配置文件里面读取
		this.master = master!=null?master:Constants.SESSION_MANAGER.get(this.project).get("master");
	}
	/**
	 * 取得运行时并执行shell语句提交spark应用
	 */
	@Override
	public Object call() throws Exception {
		String node = "spark://"+this.master+":7077";
		String[] cmd ={"/bin/sh", "-c", "spark-submit --jars $(echo /home/spark/genSpark/test/lib/*.jar | tr ' ' ',')  --class \""+this.filename+"\" --master "+node+" "+Constants.JAR_LOCATION};
		System.out.println("spark-submit --jars $(echo /home/spark/genSpark/test/lib/*.jar | tr ' ' ',')  --class \""+this.filename+"\" --master "+node+" "+Constants.JAR_LOCATION);
		String logPath = Constants.LOG_PATH+filename+"("+node+").txt";
		File logFile = new File(logPath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		try {
			logFile.createNewFile();
		} catch (IOException e) {
		   e.printStackTrace();
		}
		//写日志
		FileWriter fw = new FileWriter(logFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		Runtime run=Runtime.getRuntime();
//		LoadBlancer blancer = new LoadBlancer();
//		blancer.process(null, null, false);
		Process pro=run.exec(cmd);
//		pro.waitFor();
	    InputStream in2 = pro.getInputStream();  
	    InputStream in3 = pro.getErrorStream();
        BufferedReader read2 = new BufferedReader(new InputStreamReader(in3)); 
      
        String result2 = null;
        int exceptionFlag = 0;
        int flag = 0;
        while(null!=( result2=read2.readLine())){
        	bw.write(result2+"\n");
        	bw.flush();
        	if(result2.contains("Exception")|| result2.contains("Caused by")){
        		exceptionFlag=1;
        		break;
        	}
        	if(result2.contains("Starting job")&&!successFlag){
        		flag=0;
        		successFlag = true;
        		//成功后在会话管理中添加提交状态
        		Constants.SESSION_MANAGER.get(this.project).put("status","提交成功!");
//        		Constants.SESSION_STATUS.put(this.project, "提交成功！！");
        	}
        }
        flag = pro.waitFor();
		return null;
	}

}
