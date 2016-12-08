package com.mxgraph.examples.util;

import java.io.IOException;
import java.util.HashMap;

import com.mxgraph.examples.web.Constants;

public class BlancerUtil {
	HashMap<String,MasterBean> clusterInfo = null;
	
	protected String fetch(String url){
		String result ="";
		result = HttpMethod.sendGet(url, "");
		parseJson(result);
        return result;
    }
	protected boolean isBusy(String url){
        return false;
    }
	protected double range(String node){
		MasterBean master = this.clusterInfo.get(node);
        double ans = 0.6*master.getAppsCounter()+0.2*master.getMemRate()+0.2*master.getCpuRate();
        return ans;
    }
	protected void reDeploySCA(String SCANode,String targetNode){
		if(SCANode.equals(targetNode)) return;
		

    }
	protected void deploySparkJar(String targetNode){
		String[] cmd ={"/bin/sh", "-c", "spark-submit --jars $(echo /home/zhou/genSpark/test/lib/*.jar | tr ' ' ',')  --class \""+"filename"+"\" --master "+targetNode+" "+Constants.JAR_LOCATION};
		String cmd1 = "ifconfig";
		Runtime run=Runtime.getRuntime();
		try {
			Process pro=run.exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	private HashMap<String,MasterBean> parseJson(String jsonStr){
        String[] res = jsonStr.split("},");
        MasterBean master = null;
        HashMap<String,MasterBean> clusterInfo= new HashMap<String,MasterBean>();
        for(int k=0;k<res.length;k++) {
            master = new MasterBean();
            String[] ans = res[k].split(",");
            String key="";
            for (int i = 0; i < ans.length; i++) {
                if ((ans[i].indexOf("\"ip\":") > -1)) {
                    key=ans[i].substring(ans[i].indexOf("\"ip\":") + 6,ans[i].lastIndexOf("\""));
                    if(key.equals("127.0.0.1")) key = "10.109.253.71";
                    master.setIp(key);

                }
                if ((ans[i].indexOf("\"memRate\":") > -1)) {
                    master.setMemRate(Double.parseDouble(ans[i].substring(ans[i].indexOf("\"memRate\":") + 11,
                            ans[i].lastIndexOf("\""))));
                }
                if ((ans[i].indexOf("\"cpuRate\":") > -1)) {
                    master.setCpuRate(Double.parseDouble(ans[i].substring(ans[i].indexOf("\"cpuRate\":") + 11,
                            ans[i].lastIndexOf("\""))));
                }
            }
            clusterInfo.put(key,master);
        }
        this.clusterInfo = clusterInfo;
        return clusterInfo;
    }
	
	public static void main(String[] args){
		BlancerUtil util = new BlancerUtil();
		util.fetch("http://localhost:8088/sparkPlatform/monitor");
	}

}
