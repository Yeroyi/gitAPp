package com.jbufa.firefighting.model;

import java.io.Serializable;

public class Records implements Serializable {
    private String analogQuantity;
    private String detectorLossFault;
    private String deviceMac;
    private String deviceName;
    private String deviceSno;
    private boolean fireAlarm;
    private String pollutionFault;
    private int rssi;
    private String selfChecking;
    private String staticPointFault;
    private String undervoltageFault;

    public String getAnalogQuantity() {
        return analogQuantity;
    }

    public void setAnalogQuantity(String analogQuantity) {
        this.analogQuantity = analogQuantity;
    }

    public String getDetectorLossFault() {
        return detectorLossFault;
    }

    public void setDetectorLossFault(String detectorLossFault) {
        this.detectorLossFault = detectorLossFault;
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

    public String getDeviceSno() {
        return deviceSno;
    }

    public void setDeviceSno(String deviceSno) {
        this.deviceSno = deviceSno;
    }

    public boolean isFireAlarm() {
        return fireAlarm;
    }

    public void setFireAlarm(boolean fireAlarm) {
        this.fireAlarm = fireAlarm;
    }

    public String getPollutionFault() {
        return pollutionFault;
    }

    public void setPollutionFault(String pollutionFault) {
        this.pollutionFault = pollutionFault;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getSelfChecking() {
        return selfChecking;
    }

    public void setSelfChecking(String selfChecking) {
        this.selfChecking = selfChecking;
    }

    public String getStaticPointFault() {
        return staticPointFault;
    }

    public void setStaticPointFault(String staticPointFault) {
        this.staticPointFault = staticPointFault;
    }

    public String getUndervoltageFault() {
        return undervoltageFault;
    }

    public void setUndervoltageFault(String undervoltageFault) {
        this.undervoltageFault = undervoltageFault;
    }
}
