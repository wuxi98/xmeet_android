package cn.nchu.wuxi.xlivemeet.adpter.entity;

public class SimpleRoomInfo {
    private String roomId;
    private Integer users;

    public SimpleRoomInfo(String roomId, Integer users) {
        this.roomId = roomId;
        this.users = users;
    }

    public SimpleRoomInfo() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "SimpleRoomInfo{" +
                "roomId='" + roomId + '\'' +
                ", users=" + users +
                '}';
    }
}
