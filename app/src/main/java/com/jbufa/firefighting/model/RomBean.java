package com.jbufa.firefighting.model;

public class RomBean {
    private String roomName;
    private int deviceCount;
    private int roomId;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "RomBean{" +
                "roomName='" + roomName + '\'' +
                ", deviceCount=" + deviceCount +
                ", roomId=" + roomId +
                '}';
    }
}
