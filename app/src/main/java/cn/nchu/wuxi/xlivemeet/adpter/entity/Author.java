package cn.nchu.wuxi.xlivemeet.adpter.entity;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

public class Author implements IUser,Serializable {


    private String id;
    private String name;
    private String avatar;

    public Author() {

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public Author(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
