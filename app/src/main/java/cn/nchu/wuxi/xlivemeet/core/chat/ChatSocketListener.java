package cn.nchu.wuxi.xlivemeet.core.chat;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.ChatViewEvent;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.ChatViewEvent2;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.MsgListViewEvent;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.MsgListViewEvent2;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;

public class ChatSocketListener extends WebSocketClient {
    private boolean connectFlag = false;
    private String phone;
    ChatViewEvent2 chatViewEvent;
    MsgListViewEvent2 msgListViewEvent;
    public ChatSocketListener(URI serverUri, String phone) {
        super(serverUri);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setChatViewEvent(ChatViewEvent2 chatViewEvent) {
        this.chatViewEvent = chatViewEvent;
    }

    public void setMsgListViewEvent(MsgListViewEvent2 msgListViewEvent) {
        this.msgListViewEvent = msgListViewEvent;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LogUtil.d(ChatSocketListener.class,"wx-java-websocket:onOpen");
        send(new Gson().toJson(new RealMessage(phone,"","",RealMessage.ON_OPEN,0L)));
        if (chatViewEvent != null) chatViewEvent.onOpen(this);
        if (msgListViewEvent != null) msgListViewEvent.onOpen(this);
    }

    @Override
    public void onMessage(String message) {
        LogUtil.d(ChatSocketListener.class,"wx-onMessage:"+message);
        if (chatViewEvent != null) chatViewEvent.onMessage(message);
        if (msgListViewEvent != null) msgListViewEvent.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtil.e(ChatSocketListener.class, "code:"+ code+" onClose:" + reason + "remote:" + remote);
        if (connectFlag) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reconnect();
        } else {
            //todo
        }
    }

    @Override
    public void onError(Exception ex) {
        LogUtil.e(ChatSocketListener.class, " onClose:" + ex.getMessage());
    }
}
