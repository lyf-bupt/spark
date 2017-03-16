package com.mxgraph.examples.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import com.mxgraph.examples.web.Constants;
/**
* 使用另一个线程启动生成的webservice，也就是简单的SCA模型

*/
public class BootWSTask implements Callable<Integer> {
    private int i = 0;
    String dir = "";
    String classname = "";
    
    public BootWSTask(String dir,String classname){
    	this.dir  =  dir;
    	this.classname = classname;
    }

    // 与run()方法不同的是，call()方法具有返回值
    /**
    * 启动生成的webservice，也就是简单的SCA模型
    *
    */
    @Override
    public Integer call() {
        // 8.运行程序
        Runtime run = Runtime.getRuntime();  //得到系统运行时
//        dir = dir +projectName+"/"+ filename;
        System.out.println("java -cp " + this.dir+" "+this.classname);
        try{
	        Process process = run.exec("java -cp " + this.dir+" "+this.classname);
	        InputStream in = process.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        String info  = "";
	        Constants.processVec.add(process);
	        while ((info = reader.readLine()) != null) {
	            System.out.println(info);
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        return 1;
    }

}