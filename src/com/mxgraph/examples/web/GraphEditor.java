package com.mxgraph.examples.web;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.mxgraph.examples.subscribe.singleSubscribe;

/**
 * 启动类
 * @author spark
 *
 */
public class GraphEditor
{
	//启动的服务器的端口号，新版本的jetty在运行参数中指定
	public static int PORT = 8080;

	/**
	 * Point your browser to http://localhost:8080/sparkPlatform/www/index.html
	 */
	public static void main(String[] args) throws Exception
	{
		Server server = new org.mortbay.jetty.Server(PORT);

		// Servlets
		Context context = new Context(server, "/");
		context.addServlet(new ServletHolder(new SaveServlet()), "/save");
		context.addServlet(new ServletHolder(new ExportServlet()), "/export");
		context.addServlet(new ServletHolder(new OpenServlet()), "/open");

		ResourceHandler fileHandler = new ResourceHandler();
		fileHandler.setResourceBase(".");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { fileHandler, context });
		server.setHandler(handlers);

		System.out.println("Go to http://localhost:8080/sparkPlatform/index.html");
		
		server.start();
		server.join();
		
	}
}
