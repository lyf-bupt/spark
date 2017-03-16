package com.mxgraph.examples.util;

/**
 * Master节点的数据类，在计算负载时有用
 * @author spark
 *
 */
public class MasterBean {
    private String ip;
    /** 内存使用率 */
    private double memRate;
    /** cpu使用率 */
    private double cpuRate;
    /** 集群应用队列里面的应用数 */
    private int appsCounter;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getMemRate() {
        return memRate;
    }

    public void setMemRate(double memRate) {
        this.memRate = memRate;
    }

    public double getCpuRate() {
        return cpuRate;
    }

    public void setCpuRate(double cpuRate) {
        this.cpuRate = cpuRate;
    }

    public int getAppsCounter() {
        return appsCounter;
    }

    public void setAppsCounter(int appsCounter) {
        this.appsCounter = appsCounter;
    }
}
