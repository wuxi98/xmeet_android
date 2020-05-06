package cn.nchu.wuxi.xlivemeet.adpter.entity;

import java.util.concurrent.CopyOnWriteArrayList;

public class RoomInfo {
    private String roomId;
    // 房间里的人
    //private CopyOnWriteArrayList<UserBean> userBeans;
    // 房间大小
    private int maxSize;

    //房间名
    private String roomName;

    public RoomInfo() {
    }

    public RoomInfo(String roomId, int maxSize, String roomName) {
        this.roomId = roomId;
        this.maxSize = maxSize;
        this.roomName = roomName;
    }

    public RoomInfo(int maxSize, int mediaType) {
        this.maxSize = maxSize;
    }


    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
