package com.mxgraph.examples.util;

import java.io.IOException;  
import java.net.InetAddress;  
import java.net.Socket;  
import java.net.UnknownHostException;

import com.mxgraph.examples.web.Constants;  
  
/**
 * baiza均衡模块辅助类，用来计算出空闲的端口
 * @author spark
 *
 */
public class NetUtil {  
      
    /*** 
     *  查看本地端口是否被占用
     * @param port 
     * 				端口号
     * @return true ：端口被占用
     * 		   false：端口没有被占用
     */  
    public static boolean isLoclePortUsing(int port){  
        boolean flag = true;  
        try {  
            flag = isPortUsing("127.0.0.1", port) && isPortUsing(Constants.LOCAL_HOST, port);
        } catch (Exception e) {  
        }  
        return flag;  
    }  
    /*** 
     *  查看本地端口是否被占用
     * @param host 主机ip或者主机名
     * @param port 
     * 				端口号
     * @return true ：端口被占用
     * 		   false：端口没有被占用
     */  
    public static boolean isPortUsing(String host,int port) throws UnknownHostException{  
        boolean flag = false;  
        InetAddress theAddress = InetAddress.getByName(host);  
        try {  
            Socket socket = new Socket(theAddress,port);
            flag = true;  
        } catch (IOException e) {  
              
        }  
        return flag;  
    }
    
    /*** 
     *  得到可用的端口
     * @param start 从该端口开始找寻空闲端口
     * @return 空闲的端口号，如果向后找了1000个端口都没有空闲端口，返回-1  
     */  
    public static int getAvailablePort(int start){
    	for(int i=start;i<start+1000;i++){
    		if(!isLoclePortUsing(i)){
    			return i;
    		}
    	}
    	return -1;
    }
    
    public static void main(String[] args){
    	System.out.println("avalubleport:"+getAvailablePort(8088));
    	
    }
    
}  
