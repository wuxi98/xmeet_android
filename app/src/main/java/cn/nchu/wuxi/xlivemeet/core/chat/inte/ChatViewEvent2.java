package cn.nchu.wuxi.xlivemeet.core.chat.inte;

import cn.nchu.wuxi.xlivemeet.core.chat.ChatSocketListener;
import okhttp3.WebSocket;

public interface ChatViewEvent2 {
    void onOpen(ChatSocketListener socket);
    void onMessage(String text);
    void onClose();
    void onFailure();

}
