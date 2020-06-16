package cn.nchu.wuxi.xlivemeet.webrtc.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dds.skywebrtc.SkyEngineKit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.webrtc.MeetContainer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by dds on 2019/8/25.
 * android_shuai@163.com
 */
public class VoipReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(VoipReceiver.class,"接收到广播");
        String action = intent.getAction();
        if (MeetContainer.ACTION_VOIP_RECEIVER.equals(action)) {
            String room = intent.getStringExtra("room");
            boolean audioOnly = intent.getBooleanExtra("audioOnly", true);
            String inviteId = intent.getStringExtra("inviteId");
            String userList = intent.getStringExtra("userList");
            String[] list = userList.split(",");
            SkyEngineKit.init(new VoipEvent());
            boolean b = SkyEngineKit.Instance().startInCall(XMeetApp.getInstance(), room, inviteId, audioOnly);
            if (b) {
                if (list.length == 1) {
                    HttpUtil.searchUser(inviteId, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {               final String s = response.body().string();
                            JsonReturn<TCustomer> res = new Gson().fromJson(s,new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                            if(res.isSuccess()){
                                Author author = new Author(
                                        res.getData().get(0).getPhone(),
                                        res.getData().get(0).getNickName(),
                                        res.getData().get(0).getHeadUrl());
                                LogUtil.d(VoipReceiver.class,"author->"+author.toString());
                                XMeetApp.getInstance().setCallUser(author);
                                CallSingleActivity.openActivity(context, inviteId, false, audioOnly);
                            }


                        }
                    });

                } else {
                    // 群聊
                }

            }


        }

    }
}
