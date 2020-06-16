package cn.nchu.wuxi.xlivemeet.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.dds.skywebrtc.SkyEngineKit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.activity.EnterpriseManageActivity;
import cn.nchu.wuxi.xlivemeet.adpter.MeetRoomAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.RoomInfo;
import cn.nchu.wuxi.xlivemeet.adpter.entity.SimpleRoomInfo;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;

//import cn.nchu.wuxi.xlivemeet.webrtc.room.WebrtcUtil;
import cn.nchu.wuxi.xlivemeet.webrtc.room.WebrtcUtil;
import cn.nchu.wuxi.xlivemeet.webrtc.socket.IUserState;
import cn.nchu.wuxi.xlivemeet.webrtc.socket.SocketManager;

import cn.nchu.wuxi.xlivemeet.webrtc.voip.CallSingleActivity;
import cn.nchu.wuxi.xlivemeet.webrtc.voip.VoipEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetFragment extends BaseFragment implements View.OnClickListener, IUserState, AdapterView.OnItemClickListener,OnRefreshListener {

   @BindView(R.id.btn_invite_audio)
   Button btn_invite_audio;
   @BindView(R.id.btn_invite_video)
    Button btn_invite_video;
   @BindView(R.id.btn_create_room)
   Button btn_create_room;
//    @BindView(R.id.btn__login_meet)
//    Button btn_login_meet;
    @BindView(R.id.lin_are1)
    LinearLayout lin_area1;
    @BindView(R.id.lin_room_list)
    ListView lin_room_list;
//    @BindView(R.id.btn_create_room)
//    Button btn_create_room;
    @BindView(R.id.tv_refresh)
    TextView tv_refresh;
    @BindView(R.id.ll_stateful)
    StatefulLayout mStatefulLayout;

    private FragmentActivity context;
    private SharedPreferences sp;
    private String phone;
    private List<SimpleRoomInfo> rooms;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler toastHandler = new Handler(Looper.getMainLooper());;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet;
    }
    @Override
    protected void init() {
      //  btn_invite_video = (Button)this.getActivity().findViewById(R.id.btn_invite_video);
        initView();
        initListener();
    }
    private void initView() {
        context = getActivity();
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        phone = sp.getString("phone",null);
    }

    private void initListener() {
//        btn_login_meet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_login_meet.setVisibility(View.GONE);
//                lin_area1.setVisibility(View.VISIBLE);
//                login_server();
//                getRoomList();
//            }
//        });
        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatefulLayout.showLoading();
                getRoomList();
            }
        });
        btn_invite_video.setOnClickListener(this);
        btn_invite_audio.setOnClickListener(this);
        btn_create_room.setOnClickListener(this);
        lin_room_list.setOnItemClickListener(this);
        //mRefreshLayout.setOnRefreshListener(this);
    }



    void login_server() {
        if(phone == null){
            LogUtil.e(MeetFragment.class,"phone为空，无法登陆wtc服务器");
            return;
        }
        LogUtil.i(MeetFragment.class,"phone=="+phone);

    }

    void getRoomList(){
        HttpUtil.getMeetList(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       // LogUtil.d(MeetFragment.class,"string->"+s);
                        rooms = new Gson().fromJson(s, new TypeToken<List<SimpleRoomInfo>>(){}.getType());
                       // LogUtil.d(MeetFragment.class,"list->"+rooms.get(0).toString());
                            lin_room_list.setAdapter(new MeetRoomAdapter(context,R.layout.meet_room_info_item,rooms));
                            if (rooms.size() == 0) mStatefulLayout.showEmpty();
                            else mStatefulLayout.showContent();


                    }
                });
            }
        });
    }

    // 拨打语音
    public void call(Context _context,String targetPhone) {
        HttpUtil.searchUser(targetPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<TCustomer> res = new Gson().fromJson(s,new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                        if (res.isSuccess()){
                            if(res.getData().size() == 0){
                                ToastUtil.normal("不存在该用户");
                            }
                            else {
                                TCustomer item = res.getData().get(0);
                                Author user = new Author(item.getPhone(),item.getNickName(),item.getHeadUrl());
                                XMeetApp.getInstance().setCallUser(user);
                                SkyEngineKit.init(new VoipEvent());
                                CallSingleActivity.openActivity(_context, targetPhone, true, true);
                            }
                        }else {
                            LogUtil.i(MeetFragment.class,"查找用户失败");
                            ToastUtil.normal("服务器出错");
                        }
                    }
                });
            }
        });


    }

    // 拨打视频
    public void callVideo(Context _context,String targetPhone) {
        HttpUtil.searchUser(targetPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<TCustomer> res = new Gson().fromJson(s,new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                        if (res.isSuccess()){
                            if(res.getData().size() == 0){

                                ToastUtil.normal("不存在该用户");
                            }
                            else {
                                TCustomer item = res.getData().get(0);
                                Author user = new Author(item.getPhone(),item.getNickName(),item.getHeadUrl());
                                XMeetApp.getInstance().setCallUser(user);
                                SkyEngineKit.init(new VoipEvent());
                                CallSingleActivity.openActivity(_context, targetPhone, true, false);
                            }
                        }else {
                            LogUtil.i(MeetFragment.class,"查找用户失败");
                            ToastUtil.normal("服务器出错");
                        }
                    }
                });
            }
        });

    }

    // 创建房间
    public void createRoom(String roomName) {
        WebrtcUtil.call(context,"", roomName);
//        SkyEngineKit.init(new VoipEvent());
//        CallMultiActivity.openActivity(getActivity(), roomName, true, false);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_invite_video:
                showDialog("请输入对方手机号",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (dialogInterface instanceof MaterialDialog) {
                            String s = ((MaterialDialog) dialogInterface).getInputEditText().getText().toString();
                           // ToastUtil.normal("你输入了:" + s);
                            callVideo(context,s);
                        }
                    }
                });
                break;
            case R.id.btn_invite_audio:
                showDialog("请输入对方手机号",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (dialogInterface instanceof MaterialDialog) {
                            String s = ((MaterialDialog) dialogInterface).getInputEditText().getText().toString();

                            call(context,s);
                        }
                    }
                });
                break;
            case R.id.btn_create_room:
                showDialog("请输入房间号",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (dialogInterface instanceof MaterialDialog) {
                            String s = ((MaterialDialog) dialogInterface).getInputEditText()
                                    .getText().toString();
                            ToastUtil.normal("你输入了:" + s);
                            createRoom(s);
                        }
                    }
                });
                break;

        }
    }

    void showDialog(String tip,DialogInterface.OnClickListener listener){
        DialogLoader.getInstance().showInputDialog(
                getContext(),
                R.drawable.icon_tip,
                getString(R.string.tip_info),
                tip,
                new InputInfo(InputType.TYPE_CLASS_TEXT,
                        tip),
                null,
                getString(R.string.ensure),
                listener,
                getString(R.string.lab_cancel),
                null);
    }

    @Override
    public void userLogin() {
        handler.post(this::loginState);
    }

    @Override
    public void userLogout() {
        handler.post(this::logoutState);
    }
    public void loginState() {
        ToastUtil.normal("已连接");
    }

    public void logoutState() {
        ToastUtil.normal("已断开");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SimpleRoomInfo room =rooms.get(i);
        WebrtcUtil.call(context,"", room.getRoomId());
       // SkyEngineKit.init(new VoipEvent());
//        CallMultiActivity.openActivityWithJoin(context,room.getRoomId(),true,false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                getRoomList();
                refreshLayout.finishRefresh();
                //   refreshLayout.resetNoMoreData();//setNoMoreData(false);
            }
        }, 1500);
    }
}
