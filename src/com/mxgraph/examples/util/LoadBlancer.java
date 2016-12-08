package com.mxgraph.examples.util;

import java.util.Deque;

public class LoadBlancer {
	public void process(String bindingSCANode,String bindingTargetNode,boolean isStreaming) throws Exception{
        String targetNode = "10.109.253.71";
        String[] masters= "10.109.253.71;10.109.253.72".split(";");
        BlancerUtil util = new BlancerUtil();
        double minValue= Double.MAX_VALUE;
        String monitorResult=util.fetch("localhost:8080/sparkPlatform/monitor");
        for(String node:masters){
            if(!util.isBusy(node)){
                double value = util.range(node);
                if(value<minValue){
                    minValue = value;
                    targetNode = node;
                }
            }
        }
        if(bindingSCANode != null){
            if(!util.isBusy(bindingSCANode)) {
                double value = util.range(bindingSCANode);
                if(value<minValue){
                    minValue = value;
                    targetNode = bindingSCANode;
                }
            }
        }
        if(bindingTargetNode!=null){
            if(!util.isBusy(bindingTargetNode)){
                targetNode = bindingTargetNode;
            }
        }
        if(isStreaming){
        	util.reDeploySCA(bindingSCANode,targetNode);
        }
        util.deploySparkJar(targetNode);

    }

}
