package cn.nchu.wuxi.xlivemeet.adpter.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.Message;

public class MyMessage implements Serializable {

    private TCustomer user;
    private String currentMsg;
    private Date currentTime;
    private int UnreadMsg;
    private List<Message> messageRecord;

    public MyMessage() {
    }

    public MyMessage(TCustomer user, String currentMsg, Date currentTime, int unreadMsg,List<Message> messageRecord) {
        this.user = user;
        this.currentMsg = currentMsg;
        this.currentTime = currentTime;
        UnreadMsg = unreadMsg;
        this.messageRecord = messageRecord;
    }

    public TCustomer getUser() {
        return user;
    }

    public void setUser(TCustomer user) {
        this.user = user;
    }

    public String getCurrentMsg() {
        return currentMsg;
    }

    public void setCurrentMsg(String currentMsg) {
        this.currentMsg = currentMsg;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public int getUnreadMsg() {
        return UnreadMsg;
    }

    public void setUnreadMsg(int unreadMsg) {
        UnreadMsg = unreadMsg;
    }

    public List<Message> getMessageRecord() {
        return messageRecord;
    }

    public void setMessageRecord(List<Message> messageRecord) {
        this.messageRecord = messageRecord;
    }
}