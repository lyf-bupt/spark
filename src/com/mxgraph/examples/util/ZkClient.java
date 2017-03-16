package com.mxgraph.examples.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * zookeeper的客户端，对原生的客户端的一层封装
 * Created by zhou on 16-10-31.
 */
public class ZkClient {
    ZooKeeper zk;
    /**
     * 链接zookeeper server
     * @param ip zookeeper server的ip
     * @param sessionTimeOut 链接超时时间
     */
    public ZkClient(String ip,int sessionTimeOut){
        this.zk = null;
        try {
            this.zk = new ZooKeeper(ip, sessionTimeOut, new DemoWatcher());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 创建zookeeper节点
     * @param path 节点路径
     * @param value 节点值
     * @return 创建节点结果
     * @throws Exception
     */
    public String create(String path,String value) throws Exception{
        String result = "";
        result = zk.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        return result;
    }
    /**
     * 删除zookeeper节点
     * @param path 节点位置
     * @throws Exception
     */
    public void delete(String path) throws Exception{
        zk.delete(path,-1);
    }
    /**
     * 设定节点值
     * @param path 节点地址
     * @param value 节点值
     * @return 设定节点的结果（成功或者不成功）
     * @throws Exception
     */
    public Stat setData(String path,String value) throws Exception{
        Stat result = null;
        result = zk.setData(path,value.getBytes(),-1);
        return result;
    }
    /**
     * 得到节点值
     * @param path 节点位置
     * @return 节点值
     * @throws Exception
     */
    public String getData(String path) throws Exception{
        String result = "";
        result = new String(zk.getData(path,null,null));
        return result;
    }
    /**
     * 判断节点是否存在
     * @param path 节点位置
     * @return 节点存在/不存在
     * @throws Exception
     */
    public Stat exists(String path) throws Exception{
        Stat result = null;
        result = zk.exists(path,false);
        return result;
    }
    /**
     * 得到节点的子节点
     * @param path 节点路径
     * @return 字节点集合
     * @throws Exception
     */
    public List<String> getChidren(String path) throws Exception{
        List<String> children = null;
        children = zk.getChildren(path,false);
        return children;
    }
    /**
     * 
     * @throws Exception
     */
    public void close() throws Exception{
        this.zk.close();
    }

}
/**
 * 
 * @author spark
 *
 */
class DemoWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {
        System.out.println("----------->");
        System.out.println("path:" + event.getPath());
        System.out.println("type:" + event.getType());
        System.out.println("stat:" + event.getState());
        System.out.println("<-----------");
    }
}
