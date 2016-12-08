package com.mxgraph.examples.util;

public class MasterBean {
    private String ip;
    private double memRate;
    private double cpuRate;
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
