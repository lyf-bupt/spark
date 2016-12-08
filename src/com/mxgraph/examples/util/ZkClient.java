package com.mxgraph.examples.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhou on 16-10-31.
 */
public class ZkClient {
    ZooKeeper zk;
    public ZkClient(String ip,int sessionTimeOut){
        this.zk = null;
        try {
            this.zk = new ZooKeeper(ip, sessionTimeOut, new DemoWatcher());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public String create(String path,String value) throws Exception{
        String result = "";
        result = zk.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        return result;
    }
    public void delete(String path) throws Exception{
        zk.delete(path,-1);
    }
    public Stat setData(String path,String value) throws Exception{
        Stat result = null;
        result = zk.setData(path,value.getBytes(),-1);
        return result;
    }
    public String getData(String path) throws Exception{
        String result = "";
        result = new String(zk.getData(path,null,null));
        return result;
    }
    public Stat exists(String path) throws Exception{
        Stat result = null;
        result = zk.exists(path,false);
        return result;
    }
    public List<String> getChidren(String path) throws Exception{
        List<String> children = null;
        children = zk.getChildren(path,false);
        return children;
    }
    public void close() throws Exception{
        this.zk.close();
    }

}

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
