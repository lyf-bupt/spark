package com.mxgraph.examples.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mxgraph.examples.subscribe.singleSubscribe;

public class Constants
{
	/**
	 * Contains an empty image.
	 */
	public static BufferedImage EMPTY_IMAGE;
	
	public static String FILE_NAME = null;   //文件名
	
	public static String LOG_PATH = null;  //日志目录
	
	public static String SUBSCIBE_LOG_PATH = null;//订阅日志目录
	
	public static String SCALA_FILE_PATH=null;  //scala文件目录
	
	public static String OUTPUT_PATH=null;  //输出目录
	
	public static String JAVA_FILE_PATH = null; //java文件目录
	
	public static String MASTER = null;  //spark的maser地址
	
	public static String JAR_LOCATION = null;  //生成的jar包地址
	
	public static String WSN_CORE = null;  //发布订阅主节点地址
	
	public static singleSubscribe SUBSCRIBER = null;  //发布订阅系统，采用单例模式
	
	public static File SUBSCRIBE_LOG_FILE = null;  //发布订阅日志文件
	
	public static String GENERATE_RESULT = "";  //打包生成结果
	
	public static Vector<Process> processVec = null;//启动页面上新建的webservice的进程
	
	public static boolean hookFlag = false;
	
	public static String LOCAL_HOST= "";
	
	public static int i = 1;
	
	public static ExecutorService POOL = null; //线程池
	
	public static Map<String,String> SESSION_STATUS= null;
	
	public static Map<String,Map<String,String>> SESSION_MANAGER = null;
	
	public static String DATA_SOURCE = "";
	
	public static String TO_BE_DEPOLY_DIR = "/home/zhou/SCA_models";
	
	

	/**
	 * Initializes the empty image.
	 */
	static
	{	
		Properties prop = new Properties();   
		try
		{	
			EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			InputStream in = new BufferedInputStream(new FileInputStream(new File("../../config.properties"))); 
			prop.load(in); 
			LOG_PATH = prop.getProperty("LogPath").trim();
			SUBSCIBE_LOG_PATH = prop.getProperty("subscribeLogPath").trim();
			SCALA_FILE_PATH = prop.getProperty("scalaPath").trim();
			JAVA_FILE_PATH = prop.getProperty("javaPath").trim();
			OUTPUT_PATH = prop.getProperty("OutputPath").trim();	
			MASTER = prop.getProperty("Master").trim();
			JAR_LOCATION = prop.getProperty("JarLocation").trim();
			WSN_CORE = prop.getProperty("wsnCore").trim();
			processVec = new Vector<Process>();
			LOCAL_HOST= prop.getProperty("localhost").trim();
			POOL = Executors.newFixedThreadPool(5);
			SESSION_STATUS = new HashMap<String, String>();
			SESSION_MANAGER = new HashMap<String,Map<String,String>>();
			DATA_SOURCE = prop.getProperty("dataSource");
			TO_BE_DEPOLY_DIR = prop.getProperty("toBeDeployDir");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Maximum size (in bytes) for request payloads. Default is 10485760 (10MB).
	 */
	public static final int MAX_REQUEST_SIZE = 10485760;

	/**
	 * Maximum width for ex�prts. Default is 5000px.
	 */
	public static final int MAX_WIDTH = 6000;

	/**
	 * Maximum height for exports. Default is 5000px.
	 */
	public static final int MAX_HEIGHT = 6000;
	

}
