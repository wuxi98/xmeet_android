package cn.nchu.wuxi.xlivemeet.core.chat;

import okhttp3.WebSocket;

public interface MsgListViewEvent {
    void onOpen(WebSocket socket);
    void onMessage(String text);
    void onClose();
    void onFailure();
}
