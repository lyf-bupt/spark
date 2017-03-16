package com.mxgraph.examples.subscribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.jws.WebService;

import com.mxgraph.examples.web.Constants;

@WebService(endpointInterface = "org.apache.servicemix.wsn.push.INotificationProcess", serviceName = "INotificationProcess")
public class NotificationProcessImpl implements INotificationProcess {
	private static int counter = 0;
	private Socket socket = null;

	public NotificationProcessImpl(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}

	public NotificationProcessImpl() {
		// TODO Auto-generated constructor stub
	}

	public void notificationProcess(String notification) {// 收到消息后的行为，主要改这个
		counter++;

		// System.out.println("���ݣ�"+notification);

		if (counter % 1 == 0)
			if (notification != null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				try {
					PrintWriter os;
					os = new PrintWriter(socket.getOutputStream());
					BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					os.println(notification);
					os.flush();
//					System.out.println("已发出到socket");
					FileWriter fw = new FileWriter(Constants.SUBSCRIBE_LOG_FILE, true);
					   BufferedWriter bw = new BufferedWriter(fw);
					   bw.write(df.format(new Date())+"  "+notification+"\n");
					   bw.flush();
					   bw.close();
					   fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			} else {
				System.out.println(System.currentTimeMillis() + "   counter:" + counter);
			}

	}
}