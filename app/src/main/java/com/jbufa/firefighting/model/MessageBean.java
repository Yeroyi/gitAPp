package com.jbufa.firefighting.model;

public class MessageBean {
    private String reportingTime;
    private String location;
    private String dataPointKey;
    private String  dataPointValue;
    private String deviceMac;
    private String deviceName;
    private String productKey;
    private String productName;
    private int id;

    public String getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(String reportingTime) {
        this.reportingTime = reportingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDataPointKey() {
        return dataPointKey;
    }

    public void setDataPointKey(String dataPointKey) {
        this.dataPointKey = dataPointKey;
    }

    public String getDataPointValue() {
        return dataPointValue;
    }

    public void setDataPointValue(String dataPointValue) {
        this.dataPointValue = dataPointValue;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "reportingTime='" + reportingTime + '\'' +
                ", location='" + location + '\'' +
                ", dataPointKey='" + dataPointKey + '\'' +
                ", dataPointValue='" + dataPointValue + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", productKey='" + productKey + '\'' +
                ", productName='" + productName + '\'' +
                ", id=" + id +
                '}';
    }
}
