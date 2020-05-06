package cn.nchu.wuxi.xlivemeet.core.chat;

import org.java_websocket.client.WebSocketClient;

import cn.nchu.wuxi.xlivemeet.utils.OKHttpSingleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocketListener;

public class ChatWsManager {
    public static final String CHAT_HOST = "39.106.64.220";
    public static final String CHAT_PORT = "8855";

    private static ChatWsManager mInstance;
    ChatWebSocketListener listener;
    WebSocketClient client;

    private ChatWsManager() {
    }

    public static ChatWsManager getInstance() {
        if (mInstance == null) {
            synchronized (ChatWsManager.class) {
                if (mInstance == null) {
                    mInstance = new ChatWsManager();
                }
            }
        }
        return mInstance;
    }
    public static void init(){
        if (mInstance == null){
            mInstance = new ChatWsManager();
        }
        if (mInstance.listener == null){
            mInstance.listener = new ChatWebSocketListener();
        }
    }

    public ChatWebSocketListener getListener() {
        return listener;
    }

    //连接聊天服务器
    public void connectChat(){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request= new Request.Builder()
                .url("ws://"+CHAT_HOST+":"+CHAT_PORT+"/ws")
                .build();
        client.newWebSocket(request, listener);
      //  client.dispatcher().executorService().shutdown();
    }



}
