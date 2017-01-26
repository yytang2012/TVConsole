package edu.wing.yytang.performance;

import android.telephony.TelephonyManager;

/**
 * Created by yytang on 3/29/16.
 */
public class PointPerformanceData {
    private double cpuUsage = -1; // % (0.0 to 1.0) or -1 (unknown)
    private int memoryUsage; // kB
    private double batteryLevel = -1; // % (0.0 to 1.0) or -1 (unknown)
    private double wifiStrength = -1; // % (0.0 to 1.0) or -1 (unknown)
    private int cellNetwork = TelephonyManager.NETWORK_TYPE_UNKNOWN;
    private String cellValues = ""; // varies
    private int ping; // ms
    private long lastPingDate; // last time the ping value was set

    // constructor
    public PointPerformanceData() {}

    // getters
    public double getCpuUsage() {
        return cpuUsage;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public double getWifiStrength() {
        return wifiStrength;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public int getCellNetwork() {
        return cellNetwork;
    }

    public String getCellValues() {
        return cellValues;
    }

    public int getPing() {
        return ping;
    }

    // setters
    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public void setWifiStrength(double wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    public synchronized void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public synchronized void setCellData(int cellNetwork, String cellValues) {
        this.cellNetwork = cellNetwork;
        this.cellValues = cellValues;
    }

    public synchronized void setPing(long startDate, long endDate) {
        if (endDate > startDate && endDate > lastPingDate) {
            this.ping = (int)(endDate - startDate);
            this.lastPingDate = endDate;
        }
    }

    public String toString() {
        return String.format("cpuUsage '%s', memoryUsage '%dkB', wifiStrength '%s', batteryLevel '%s', cellNetwork '%s', cellValues '%s', ping '%sms'",
                cpuUsage, memoryUsage, wifiStrength, batteryLevel, cellNetwork, cellValues, ping);
    }
}
