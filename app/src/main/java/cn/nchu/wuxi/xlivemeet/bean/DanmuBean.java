package cn.nchu.wuxi.xlivemeet.bean;

import net.ossrs.yasea.SrsAllocator;

public class DanmuBean {
    String userName;
    String roomId;
    String msg;
    boolean isOpen;

    public DanmuBean(String userName, String roomId, String msg,boolean isOpen) {
        this.userName = userName;
        this.roomId = roomId;
        this.msg = msg;
        this.isOpen = isOpen;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
