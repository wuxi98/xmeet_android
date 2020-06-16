package cn.nchu.wuxi.xlivemeet.core.chat;

import android.os.Handler;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.utils.OKHttpSingleton;
import cn.nchu.wuxi.xlivemeet.webrtc.socket.DWebSocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWsManager {
 //   public static final String CHAT_HOST = "3";
//    public static final String CHAT_HOST = "39.106.64.220";
//    public static final String CHAT_HOST = "192.168.43.39";
    public static final String CHAT_HOST = "49.235.180.5";
    public static final String CHAT_PORT = "8855";
    //心跳检测
    private static final long HEART_BEAT_RATE = 15 * 1000;

    private static ChatWsManager mInstance;
    ChatWebSocketListener listener;
    ChatSocketListener webSocket;
   // WebSocketClient client;

    //okhttp连接
    WebSocket mWebSocket;
    private String phone;

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

    public ChatSocketListener getWebSocket() {
        return webSocket;
    }

    public void connect(String ph) {
        phone = ph;
        if (webSocket == null || !webSocket.isOpen()) {
            URI uri;
            try {
                String urls = "ws://"+CHAT_HOST+":"+CHAT_PORT+"/ws";
                uri = new URI(urls);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
            webSocket = new ChatSocketListener(uri,phone);
            // 设置wss

            // 开始connect
            webSocket.connect();
            mHandler.postDelayed(heartBeatRunnableJWeb, HEART_BEAT_RATE);//开启心跳检测
        }


    }


    //连接聊天服务器
    public void connectChat(){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request= new Request.Builder()
                .url("ws://"+CHAT_HOST+":"+CHAT_PORT+"/ws")
                .build();
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
      //  client.dispatcher().executorService().shutdown();
    }
    public void closeConnect(){
        try {
            if (null != listener.socket) {
                listener.socket.cancel();
            }
            if (null != webSocket) {
                webSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener = null;
            webSocket = null;
        }
    }

    private long sendTime = 0L;
    // 发送心跳包
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                boolean isSuccess = listener.socket.send(new Gson().toJson(new RealMessage("","","",RealMessage.HEART_TYPE,0L)));//发送一个空消息给服务器，通过发送消息的成功失败来判断长连接的连接状态
                    if (!isSuccess) {//长连接已断开
                        mHandler.removeCallbacks(heartBeatRunnable);
                        listener.socket.cancel();//取消掉以前的长连接
                        new InitSocketThread(phone).start();//创建一个新的连接
                    } else {//长连接处于连接状态

                }

                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };


    private Runnable heartBeatRunnableJWeb = new Runnable() {
        @Override
        public void run() {
            if (webSocket != null) {
                if (webSocket.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化websocket
                connect(phone);
            }
            //定时对长连接进行心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    //重连
                    webSocket.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



}
