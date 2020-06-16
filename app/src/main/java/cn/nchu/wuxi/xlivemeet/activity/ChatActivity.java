package cn.nchu.wuxi.xlivemeet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.java_websocket.enums.ReadyState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.Message;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatSocketListener;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.ChatViewEvent;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatWebSocketListener;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatWsManager;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.ChatViewEvent2;
import cn.nchu.wuxi.xlivemeet.fragment.MessageFragment;
import cn.nchu.wuxi.xlivemeet.utils.ACache;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import okhttp3.WebSocket;

public class ChatActivity extends BaseActivity implements ChatViewEvent, ChatViewEvent2 {

    @BindView(R.id.tb_chat_view)
    TitleBar tb_chat_view;
    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.input)
    MessageInput messagesInput;
    private String phone;
    private String userName;
    private String avatar;
    private SharedPreferences sp;
    private ChatActivity context;
    private MessagesListAdapter<Message> adapter;
    private String targetPhone;
    private String targetAvatar;
    private String targetUserName;
    private List<Message> list;
    //messageFragment聊天列表
    private SortedList<MyMessage> friendMessageList;
    //当前聊天窗口对应messageFragment聊天列表的位置
    private int position;
    //当前聊天窗口对应messageFragment聊天列表的项
    private MyMessage currentFriendMessage;
    //当前聊天窗口存储聊天记录的list
    private List<Message> currentMessageRecord;
    private ChatWebSocketListener mWebSocketListener;
    private WebSocket socket;
    private ChatSocketListener chatSocketListener;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        initData();
        initView();

    }

    private void initData() {
        sp = getSharedPreferences("user", MODE_PRIVATE);
        context = this;
        phone = sp.getString("phone","0");
        userName = sp.getString("nickName","0");
        avatar = sp.getString("headUrl","0");


        Intent intent = getIntent();
        targetPhone = intent.getStringExtra("phone");
        targetAvatar = intent.getStringExtra("headUrl");
        targetUserName = intent.getStringExtra("userName");
        position = intent.getIntExtra("position", -1);
        friendMessageList = XMeetApp.getInstance().getMessageList();

        if(position != -1){
            //从消息列表跳转而来
            currentFriendMessage = friendMessageList.get(position);
            currentMessageRecord = currentFriendMessage.getMessageRecord();
            list = currentMessageRecord;
        }else {
            //从用户资料页跳转而来
            int i = 0,len = friendMessageList.size();
            for (;i < len;i++){
                if(targetPhone.equals(friendMessageList.get(i).getUser().getPhone())){
                    currentFriendMessage = friendMessageList.get(i);
                    currentMessageRecord = currentFriendMessage.getMessageRecord();
                }
            }
            if (i == friendMessageList.size()){
                currentMessageRecord = new ArrayList<>();
                currentFriendMessage = new MyMessage(new TCustomer(targetPhone, targetUserName, targetAvatar), "", new Date(), 0, currentMessageRecord);
                friendMessageList.add(currentFriendMessage);
            }
        }


        mWebSocketListener = ChatWsManager.getInstance().getListener();
        if (null != mWebSocketListener) mWebSocketListener.setChatViewEvent(this);
        socket = mWebSocketListener.getSocket();
        chatSocketListener = ChatWsManager.getInstance().getWebSocket();
        if (null != chatSocketListener) chatSocketListener.setChatViewEvent(this);


    }

    private void initView() {
        //String cacheDir = getCacheDir(this);
        tb_chat_view.setTitle(targetUserName);
        tb_chat_view.setLeftClickListener(view -> { finishChat(); });

        messagesInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
             //   adapter.addToStart(message, true);
                Message message = new Message(phone,input.toString(),new Date(),new Author(phone,userName,avatar));
                currentMessageRecord.add(message);
                adapter.addToStart(message, true);
                if (socket != null){

                    String s = new Gson().toJson(new RealMessage(phone,targetPhone,input.toString(),RealMessage.NORMAL_STRING_MESSAGE,System.currentTimeMillis()));
                    socket.send(s);
                    LogUtil.d(ChatWebSocketListener.class,"wx-okhttp:send->"+s);

                }

                if(chatSocketListener != null){

                    String s = new Gson().toJson(new RealMessage(phone,targetPhone,input.toString(),RealMessage.NORMAL_STRING_MESSAGE,System.currentTimeMillis()));
                    while (!chatSocketListener.getReadyState().equals(ReadyState.OPEN)) {
                    }
                    chatSocketListener.send(s);
                    LogUtil.d(ChatSocketListener.class,"wx-java-websocket:send->"+s);
                }

//                else
//                    ToastUtil.normal("您已与服务器断开连接");
                return true;
            }
        });


        adapter = new MessagesListAdapter<>(phone, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(context).load(url).into(imageView);
            }
        });
        messagesList.setAdapter(adapter);
        //adapter.add(currentMessageRecord,false);
        for (Message e:currentMessageRecord){
            adapter.addToStart(e,true);
        };
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LogUtil.d(ChatActivity.class,"onOpen:websocket = "+webSocket);
        socket = webSocket;
    }

    @Override
    public void onOpen(ChatSocketListener socket) {
        LogUtil.d(ChatSocketListener.class,"wx-java-webSocket-chatView:onOpen:client = "+socket);
        chatSocketListener = socket;
    }

    @Override
    public void onMessage(String text) {
        RealMessage msgObject = new Gson().fromJson(text, RealMessage.class);
        String phone = msgObject.getFromId();
        String msg = msgObject.getMsg();
        Long millis = msgObject.getMills();
        Date date = new Date();
        date.setTime(millis);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addToStart(new Message(phone,msg,date,new Author(phone,targetUserName,targetAvatar)),true);
            }
        });

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onDestroy() {

        List<MyMessage> list = new ArrayList<>();
        SortedList<MyMessage> msgs = XMeetApp.getInstance().getMessageList();
        msgs.add(currentFriendMessage);
        for (int i = 0,len = msgs.size(); i < len; i++) {
            list.add(msgs.get(i));
        }
        String messages = new Gson().toJson(list);

        ACache cache = ACache.get(context);
        LogUtil.d(MessageFragment.class,"ACache save-> phone = "+phone);
        LogUtil.d(MessageFragment.class,messages);
        cache.put("messages"+phone,messages);
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finishChat();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    void finishChat(){
        currentFriendMessage.setUnreadMsg(0);
        if(currentMessageRecord.size() > 0) {
            currentFriendMessage.setCurrentMsg(currentMessageRecord.get(currentMessageRecord.size() - 1).getText());
            currentFriendMessage.setCurrentTime(currentMessageRecord.get(currentMessageRecord.size() - 1).getCreatedAt());
        }
        if (position != -1){
            setResult(1);
            finish();
        }else {
            Intent intent = new Intent(ChatActivity.this,MainActivity.class);
            intent.putExtra("id",2);
            startActivity(intent);
        }
    }
}
