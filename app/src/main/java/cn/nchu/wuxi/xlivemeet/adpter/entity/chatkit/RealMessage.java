package cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit;

import java.util.Date;

public class RealMessage {

    public static final int NORMAL_STRING_MESSAGE = 1001;
    public static final int ON_OPEN = 1000;
    public static final int HEART_TYPE = 1002;
    private String fromId;
    private String toId;
    private String msg;
    private int type;
    private Long mills;

    public RealMessage(String fromId, String toId, String msg, int type, Long date) {
        this.fromId = fromId;
        this.toId = toId;
        this.msg = msg;
        this.type = type;
        this.mills = mills;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getMills() {
        return mills;
    }

    public void setMills(Long mills) {
        this.mills = mills;
    }

    @Override
    public String toString() {
        return "RealMessage{" +
                "fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", msg='" + msg + '\'' +
                ", type=" + type +
                ", mills=" + mills +
                '}';
    }
}
