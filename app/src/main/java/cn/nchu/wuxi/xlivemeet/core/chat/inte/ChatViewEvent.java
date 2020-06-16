package cn.nchu.wuxi.xlivemeet.core.chat.inte;

import okhttp3.WebSocket;

public interface ChatViewEvent {
    void onOpen(WebSocket socket);
    void onMessage(String text);
    void onClose();
    void onFailure();

}
