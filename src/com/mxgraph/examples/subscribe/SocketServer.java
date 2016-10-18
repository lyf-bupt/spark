package com.mxgraph.examples.subscribe;

import java.io.*;
import java.net.*;
import java.applet.Applet;

public class SocketServer {
	public static void main(String args[]) {
		String[] words = {"北京","上海","青岛","济南","天津","厦门"};
		try{
			ServerSocket server=null;
			try{
				server=new ServerSocket(9999);
				//创建一个ServerSocket在端口9999监听客户请求
				System.out.println("Listenning port 9999");
			}catch(Exception e) {
				System.out.println("can not listen to:"+e);
				//出错，打印出错信息
			}
			Socket socket=null;
			try{
				socket=server.accept();
				if(socket!=null){
					System.out.println("client:"+socket.getLocalAddress()+":"+socket.getLocalPort());
					String line;
					BufferedReader is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					//由Socket对象得到输入流，并构造相应的BufferedReader对象
					PrintWriter os=new PrintWriter(socket.getOutputStream());
					//由Socket对象得到输出流，并构造PrintWriter对象
					while(true){
						for(int i=0;i<50000;i++){
							for(String word:words){
								os.println(word);
							}
							os.flush();
						}
						Thread.sleep(999);
						if(false) break;
					}
					//继续循环
					os.close(); //关闭Socket输出流
					is.close(); //关闭Socket输入流
					socket.close(); //关闭Socket
					server.close(); //关闭ServerSocket
				}
				//使用accept()阻塞等待客户请求，有客户
				//请求到来则产生一个Socket对象，并继续执行
			}catch(Exception e) {
				System.out.println("Error."+e);
				//出错，打印出错信息
			}
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
}
