package cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.io.Serializable;
import java.util.Date;

import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;

public class Message implements IMessage, Serializable {
    private String id;
    private String text;
    private Date createdAt;
    private Author author;

    public Message(String s, String s1) {
    }

    /*...*/

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public Message(String id, String text, Date createdAt, Author author) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.author = author;
    }
}