/**
 * 发布/订阅的主类、这个类可以暂时不用管
 */
package com.mxgraph.examples.subscribe;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.ws.Endpoint;
import com.mxgraph.examples.subscribe.NotificationProcessImpl;
import wsn.wsnclient.command.SendWSNCommand;
import com.mxgraph.examples.web.Constants;

public class singleSubscribe {
	private SendWSNCommand sendWSNCommand = null;
	
	public void bootSubscribeSys(){
		String subWebSAddr = "http://10.108.165.187:9016/wsn-subscribe";// 本地webservice地址，需要改成本机ip
		String wsnAddr = Constants.WSN_CORE;// 发布/订阅主机地址
		System.out.println("Starting Server");
		Socket socket = null;
		NotificationProcessImpl implementor = new NotificationProcessImpl();
		try {
		socket = new Socket("127.0.0.1", 9999);
		} catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		socket=null;
		} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		socket=null;
		}
		if (socket != null) {
		implementor = new NotificationProcessImpl(socket);// ��Ϣ�����߼�
		}
		Endpoint endpint = Endpoint.publish(subWebSAddr, implementor);// �������շ���
		sendWSNCommand = new SendWSNCommand(subWebSAddr, wsnAddr);
		// endpint.stop();
		System.out.println("Server start!");
	}
	
	public String subscribTopic(String topic){
		if(Constants.SUBSCRIBE_LOG_FILE ==null){
			//创建日志文件
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式\n"
					
			String logPath = Constants.SUBSCIBE_LOG_PATH+"Log:"+df.format(new Date()).toString()+".txt";
			File logFile = new File(logPath);
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}
			try {
				logFile.createNewFile();
			} catch (IOException e) {
			   e.printStackTrace();
			}
			Constants.SUBSCRIBE_LOG_FILE = logFile;
		}
			try {
			return this.sendWSNCommand.subscribe(topic);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "No";
			}
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		singleSubscribe sub = new singleSubscribe();
		sub.bootSubscribeSys();
		sub.subscribTopic("all:yeah");
	}
}