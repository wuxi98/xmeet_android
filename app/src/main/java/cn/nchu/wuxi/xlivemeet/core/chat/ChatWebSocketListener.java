package cn.nchu.wuxi.xlivemeet.core.chat;

import com.google.gson.Gson;

import javax.annotation.Nullable;

import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.ChatViewEvent;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.MsgListViewEvent;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatWebSocketListener extends WebSocketListener {

    ChatViewEvent chatViewEvent;
    MsgListViewEvent msgListViewEvent;
    WebSocket socket;

    public WebSocket getSocket() {
        return socket;
    }

    public void setSocket(WebSocket socket) {
        this.socket = socket;
    }

    private String phone;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setChatViewEvent(ChatViewEvent chatViewEvent) {
        this.chatViewEvent = chatViewEvent;
    }

    public void setMsgListViewEvent(MsgListViewEvent msgListViewEvent) {
        this.msgListViewEvent = msgListViewEvent;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        //ToastUtil.normal("onOpen");
        LogUtil.d(ChatWebSocketListener.class,"onOpen");
        socket = webSocket;
        webSocket.send(new Gson().toJson(new RealMessage(phone,"","",RealMessage.ON_OPEN,0L)));
        if (chatViewEvent != null) chatViewEvent.onOpen(webSocket);
        if (msgListViewEvent != null) msgListViewEvent.onOpen(webSocket);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        if (chatViewEvent != null) chatViewEvent.onFailure();
        if (msgListViewEvent != null) msgListViewEvent.onFailure();
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);

    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
       // ToastUtil.normal("onClosed");
        LogUtil.e(ChatWebSocketListener.class,String.format("webSocket onClose! code=%d reason=%s",code,reason));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ChatWsManager.getInstance().connectChat();
        if (chatViewEvent != null) chatViewEvent.onClose();
        if (msgListViewEvent != null) msgListViewEvent.onClose();
    }


    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        LogUtil.d(ChatWebSocketListener.class,text);
        if (chatViewEvent != null) chatViewEvent.onMessage(text);
        if (msgListViewEvent != null) msgListViewEvent.onMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        LogUtil.d(ChatWebSocketListener.class,bytes.utf8());
    }
}
