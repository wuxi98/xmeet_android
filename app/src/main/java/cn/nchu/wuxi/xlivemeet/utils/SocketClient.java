//package cn.nchu.wuxi.xlivemeet.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.edu.nchu.rescue.common.enums.MsgActionEnum;
//import com.edu.nchu.rescue.common.netty.DataContent;
//import com.edu.nchu.rescue.entity.Message;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_6455;
//import org.java_websocket.enums.ReadyState;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class SocketClient {
//    //private volatile static SocketClient instance = null;
//    private WebSocketClient webSocketClient;
//    private Integer uid;
//
//    public interface Callback {
//        void OnMessage(String message);
//    }
//
////    public static SocketClient getInstance(Integer uid, Callback callback){
////        if(instance == null){
////            synchronized (SocketClient.class){
////                if(instance == null){
////                    instance = new SocketClient(uid, callback);
////                }
////            }
////        }
////        return instance;
////    }
//
//    public SocketClient(Integer uid, Callback callback){
//        this.uid = uid;
//        try{
//            webSocketClient = new WebSocketClient(new URI("ws://localhost:8088/ws"),new Draft_6455()) {
//                @Override
//                public void onOpen(ServerHandshake handshakedata) {
//                    //System.out.println("【WebSocket】连接成功");
//                }
//
//                @Override
//                public void onMessage(String message) {
//                    callback.OnMessage(message);
//                }
//
//                @Override
//                public void onClose(int code, String reason, boolean remote) {
//                    //System.out.println("【WebSocket】已断开连接");
//                }
//
//                @Override
//                public void onError(Exception ex) {
//                    System.out.println("【WebSocket】 连接错误={"+ex.getMessage()+"}");
//                }
//            };
//            webSocketClient.connect();
//            while (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) { }
//            login();
//            //sendHeart();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public synchronized void send(Integer recvId, String message) {
//        send(recvId, message, null);
//    }
//
//    public synchronized void send(Integer recvId, String message, String extend) {
//        DataContent content = new DataContent();
//        content.setAction(MsgActionEnum.CHAT.type);
//        if(extend != null){
//            content.setExtend(extend);
//        }
//        Message msg = new Message();
//        msg.setSendId(this.uid);
//        msg.setRecvId(recvId);
//        msg.setMessage(message);
//        msg.setSendTime(new Date());
//        content.setChatData(msg);
//        webSocketClient.send(JSONObject.toJSONString(content));
//    }
//
//    private void login() {
//        DataContent content = new DataContent();
//        content.setAction(MsgActionEnum.CONNECT.type);
//        Message msg = new Message();
//        msg.setSendId(this.uid);
//        msg.setSendTime(new Date());
//        content.setChatData(msg);
//        webSocketClient.send(JSONObject.toJSONString(content));
//    }
//
//    private void sendHeart(){
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                DataContent content = new DataContent();
//                content.setAction(MsgActionEnum.KEEPALIVE.type);
//                webSocketClient.send(JSONObject.toJSONString(content));
//            }
//        }, 30000, 30000);
//    }
//}
