package com.mxgraph.examples.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import javax.naming.NamingException;

import com.bupt.wangfu.ldap.Ldap;
import com.bupt.wangfu.ldap.TopicEntry;

/** 
 * 得到发布订阅中所有的主题，已废弃
 * 
 */
public class GetAllTopic {
	public LinkedList<String> AllTopicByGivenFile(){
    	LinkedList<String> result = new LinkedList<String>();
    	
    	SubscribeConfig config = new SubscribeConfig("subscribeConfig.properties");
    	String ManAddr=config.getValue("ManAddr");
    	String LdapAddr=config.getValue("LdapAddr");
    	String ManPort=config.getValue("ManPort");
    	String LdapUser=config.getValue("LdapUser");
    	String LdapPassword=config.getValue("LdapPassword");
    	Ldap ldap = new Ldap(ManAddr,Integer.valueOf(ManPort));
		try {
			ldap.connectLdap(LdapAddr, LdapUser, LdapPassword);	
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String TreeNode=config.getValue("TreeNode");
		TopicEntry all = new TopicEntry("", "1", TreeNode, null,null);
		
		Queue<TopicEntry> queue = new LinkedList<TopicEntry>();
		queue.offer(all);
		while(!queue.isEmpty()){
			TopicEntry temp = queue.poll();
			List<TopicEntry> list = ldap.getSubLevel(temp);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					queue.offer(list.get(i));
				}
			}else{
				result.add(temp.getTopicPath());
			}
		}
		LinkedList<String> resultList = new LinkedList<String>();
		for(String s:result){
			s = s.replace(",ou=all_test,dc=wsn,dc=com", "");
			String[] temp = s.split(",");
			StringBuilder sb = new StringBuilder();
			sb.append("all");
			for(int i=temp.length-1;i>=0;i--){
				sb.append(":");
				temp[i] = temp[i].replace("ou=", "");
				sb.append(temp[i]);
			}
			resultList.add(sb.toString().trim());
		}
    	return resultList;
    }
    
    public HashSet<String> AllContents() {
		LinkedList<String> ll = AllTopicByGivenFile();
		HashSet<String> hs = new HashSet<String>();
		for(int i=0;i<ll.size();i++){
			String s = ll.get(i);
			s = s.substring(0, s.lastIndexOf(":"));
			hs.add(s);
		}
		return hs;
	}
    
    public boolean topicExists(String topicName){
    	LinkedList<String> ll = AllTopicByGivenFile();
    	for(int i=0;i<ll.size();i++){
    		String s = ll.get(i);
    		ll.set(i, s.substring(s.lastIndexOf(":")+1));
    	}
    	HashSet<String> allTopic = new HashSet<String>(ll);
//    	System.out.println("所有的Topic如下：");
//    	System.out.println(allTopic);
    	if(allTopic.contains(topicName))
    		return true;
    	return false;
    }


    public static void main(String[] args){
    	GetAllTopic topic = new GetAllTopic();
    	LinkedList<String> list = topic.AllTopicByGivenFile();
    	for(int i=0;i<list.size();i++){
    		System.out.println("主题"+i+"："+list.get(i));
    	}
    }
}
