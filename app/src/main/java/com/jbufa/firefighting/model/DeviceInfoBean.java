package com.jbufa.firefighting.model;

public class DeviceInfoBean {

    private String StaticPointFault;
    private String AnalogQuantity;
    private String RSSI;
    private String PollutionFault;
    private String FireAlarm;
    private String status_time;
    private String UndervoltageFault;
    private String SelfChecking;
    private String DetectorLossFault;
    private String Temperature;
    private String productKey;
    private String productName;

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

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    public String getStaticPointFault() {
        return StaticPointFault;
    }

    public void setStaticPointFault(String staticPointFault) {
        StaticPointFault = staticPointFault;
    }

    public String getAnalogQuantity() {
        return AnalogQuantity;
    }

    public void setAnalogQuantity(String analogQuantity) {
        AnalogQuantity = analogQuantity;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getPollutionFault() {
        return PollutionFault;
    }

    public void setPollutionFault(String pollutionFault) {
        PollutionFault = pollutionFault;
    }

    public String getFireAlarm() {
        return FireAlarm;
    }

    public void setFireAlarm(String fireAlarm) {
        FireAlarm = fireAlarm;
    }

    public String getStatus_time() {
        return status_time;
    }

    public void setStatus_time(String status_time) {
        this.status_time = status_time;
    }

    public String getUndervoltageFault() {
        return UndervoltageFault;
    }

    public void setUndervoltageFault(String undervoltageFault) {
        UndervoltageFault = undervoltageFault;
    }

    public String getSelfChecking() {
        return SelfChecking;
    }

    public void setSelfChecking(String selfChecking) {
        SelfChecking = selfChecking;
    }

    public String getDetectorLossFault() {
        return DetectorLossFault;
    }

    public void setDetectorLossFault(String detectorLossFault) {
        DetectorLossFault = detectorLossFault;
    }
}
