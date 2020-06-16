package cn.nchu.wuxi.xlivemeet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.activity.ChatActivity;
import cn.nchu.wuxi.xlivemeet.activity.MainActivity;
import cn.nchu.wuxi.xlivemeet.activity.SearchViewActivity;
import cn.nchu.wuxi.xlivemeet.adpter.MessageAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.Message;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.adpter.viewholder.SortedListCallback;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatSocketListener;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatWebSocketListener;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatWsManager;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.MsgListViewEvent;
import cn.nchu.wuxi.xlivemeet.core.chat.inte.MsgListViewEvent2;
import cn.nchu.wuxi.xlivemeet.utils.ACache;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.WebSocket;

public class MessageFragment extends BaseFragment implements MsgListViewEvent, MsgListViewEvent2 {

    //@BindView(R.id.search_view)
    MaterialSearchView mSearchView;

    @BindView(R.id.msg_recycler_view)
    RecyclerView msg_recycler_view;

    @BindView(R.id.scroll_view)
    ScrollView scroll_view;

//    @BindView(R.id.dialogsList)
//    DialogsList dialogsListView;

    private FragmentActivity context;
    private SortedList<MyMessage> msgs;
    private List<MyMessage> tempList;
    private MessageAdapter adapter;
    private ChatWebSocketListener mWebSocketListener;
    private int position;
    private WebSocket socket;
    private SharedPreferences sp;
    private String phone;
    private ChatSocketListener chatSocketListener;

    @Override
    protected void init() {
        //ToastUtil.normal("onCreate!!!!!!");
        setHasOptionsMenu(true);
        initData();
        initViews();
        //initListener();
    }

    private void initData() {
        context = getActivity();
        sp = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        phone = sp.getString("phone","0");
        msgs = XMeetApp.getInstance().getMessageList();
        adapter = new MessageAdapter(null, context);
        adapter.setRecyclerItemClickListener(new MessageAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, MyMessage item) {

                XMeetApp.getInstance().setMessageList(msgs);
                position = position;
                msgs.get(position).setUnreadMsg(0);
//                item.setUnreadMsg(0);
              //  LogUtil.d(MessageFragment.class,"wuxi:item.getUnreadMsg()="+item.getUnreadMsg());
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userName",item.getUser().getNickName());
                intent.putExtra("phone",item.getUser().getPhone());
                intent.putExtra("headUrl",item.getUser().getHeadUrl());
                intent.putExtra("position",position);
                startActivityForResult(intent,1);
            }
        });
        msgs = new SortedList<>(MyMessage.class, new SortedListCallback(adapter));
        if(tempList != null)        msgs.replaceAll(tempList);
        initChatData();
        XMeetApp.getInstance().setMessageList(msgs);

        mWebSocketListener = ChatWsManager.getInstance().getListener();
        if (null != mWebSocketListener) mWebSocketListener.setMsgListViewEvent(this);
        chatSocketListener = ChatWsManager.getInstance().getWebSocket();
        if (null != chatSocketListener) chatSocketListener.setMsgListViewEvent(this);
    }

    private void initViews() {

//        tempList = Cache.with(context)
//                .path(context.getCacheDir().getPath())
//                .getCacheList("chatList",MyMessage.class);
        msg_recycler_view.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        msg_recycler_view.setAdapter(adapter);
        adapter.setList(msgs);

    }
    @Override
    protected int getLayoutId() {
        return R.layout.message_fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 1) {
           // textView.setText(result);
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.float_search})
    void toSearchActivity(View v) {
        String phone = "123456";
        String msg = "123456";
        Long millis = 321423435L;
        Date date = new Date();
        date.setTime(millis);
        switch (v.getId()){
            case R.id.float_search:
                Intent intent = new Intent(context, SearchViewActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onOpen(WebSocket webSocket) {
        //socket = webSocket;
      // socket.send(new Gson().toJson(new RealMessage(phone)))

    }

    /**
     * 加载离线消息
     */
    private void initChatData() {
        HttpUtil.loadChatRecord(phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(MainActivity.class,"加载离线消息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                JsonReturn<RealMessage> res = new Gson().fromJson(s,new TypeToken<JsonReturn<RealMessage>>(){}.getType());
                List<RealMessage> messageList = res.getData();

                System.out.println("离线消息列表：");
                //将离线消息加入到本地缓存消息列表种
                for(RealMessage e:messageList){
                    System.out.println(e.toString());
                    Date date = new Date();
                    date.setTime(e.getMills());
                    int i = 0,len = msgs.size();
                    for (; i < len; i++) {
                        MyMessage item = msgs.get(i);
                        if (item.getUser().getPhone().equals(e.getFromId())){
                            item.getMessageRecord().add(new Message(e.getFromId(),e.getMsg(),date,new Author(e.getFromId(),item.getUser().getNickName(),item.getUser().getHeadUrl())));
                            item.setCurrentTime(date);
                            item.setUnreadMsg(item.getUnreadMsg()+1);
                            item.setCurrentMsg(e.getMsg());
                        }
                    }
                    if (i == len){
                        HttpUtil.searchUser(e.getFromId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                LogUtil.i(MainActivity.class,"加载离线消息失败[加载用户失败]");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String s1 = response.body().string();
                                JsonReturn<TCustomer> o = new Gson().fromJson(s1, new TypeToken<JsonReturn<TCustomer>>() {
                                }.getType());
                                List<Message> list = new ArrayList<>();
                                new MyMessage(o.getData().get(0),e.getMsg(),date,1,list);
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                    HttpUtil.deleteChatRecord(phone);
                }

            }
        });

    }

    @Override
    public void onOpen(ChatSocketListener socket) {
        chatSocketListener = socket;
    }

    /**
     * 收到消息
     * @param text
     */
    @Override
    public void onMessage(String text) {
        LogUtil.d(MessageFragment.class,"wuxi receive->"+text);
        RealMessage msgObject = new Gson().fromJson(text, RealMessage.class);
        String phone = msgObject.getFromId();
        String msg = msgObject.getMsg();
        Long millis = msgObject.getMills();
        int i = 0,len = msgs.size();
        boolean flag = false;
        for (;i < len;i++){
            MyMessage item = msgs.get(i);
            if(item.getUser().getPhone().equals(phone)){
                LogUtil.d(MessageFragment.class,"wuxi:接收到已存在的消息记录");
                item.setUnreadMsg(item.getUnreadMsg() + 1);
                item.setCurrentMsg(msg);
                Date date = new Date();
                date.setTime(millis);
                item.setCurrentTime(date);
                Message message = new Message(phone, msg, date, new Author(phone, item.getUser().getNickName(), item.getUser().getHeadUrl()));
                item.getMessageRecord().add(message);
                final int index = i;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgs.updateItemAt(index,item);
                        adapter.notifyDataSetChanged();
                    }
                });

                flag = true;
            }
        }
        if(!flag) {
            HttpUtil.searchUser(phone, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonReturn<TCustomer> res = new Gson().fromJson(s, new TypeToken<JsonReturn<TCustomer>>() {
                            }.getType());
                            if (res.isSuccess()) {
                                TCustomer customer = res.getData().get(0);
                                Date date = new Date();
                                date.setTime(millis);
                                List<Message> messageRecord = new ArrayList<>();
                                messageRecord.add(new Message(customer.getPhone(), msg, date, new Author(customer.getPhone(), customer.getNickName(), customer.getHeadUrl())));
                                msgs.add(new MyMessage(customer, msg, date, 1, messageRecord));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onFailure() {

    }


    void handleMessage(String msg){

    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {

        tempList = new ArrayList<>();

        ACache cache = ACache.get(getActivity());

        String phone = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE).getString("phone","");
        LogUtil.d(MessageFragment.class,"ACache get-> phone = "+phone);
        String s= cache.getAsString("messages"+phone);
        s = (s == null?"":s);
        LogUtil.d(MessageFragment.class,s);
        tempList = new Gson().fromJson(s, new TypeToken<List<MyMessage>>() {
        }.getType());
        super.onAttach(context);
    }


    @Override
    public void onDestroyView() {
//
//        List<MyMessage> list = new ArrayList<>();
//        for (int i = 0,len = msgs.size(); i < len; i++) {
//            list.add(msgs.get(i));
//        }
//        String messages = new Gson().toJson(list);
//
//        ACache cache = ACache.get(context);
//        LogUtil.d(MessageFragment.class,"ACache save-> phone = "+phone);
//        LogUtil.d(MessageFragment.class,messages);
//        cache.put("messages"+phone,messages);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        List<MyMessage> list = new ArrayList<>();
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
}
