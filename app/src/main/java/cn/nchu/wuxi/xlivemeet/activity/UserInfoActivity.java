package cn.nchu.wuxi.xlivemeet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dds.skywebrtc.SkyEngineKit;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.webrtc.voip.CallSingleActivity;
import cn.nchu.wuxi.xlivemeet.webrtc.voip.VoipEvent;

public class UserInfoActivity extends BaseActivity {
    private String phone;
    private String userName;
    private String enterpriseName;
    private String headUrl;
    private String myPhone;
    private SharedPreferences sp;

    @BindView(R.id.tb_user_info)
    TitleBar tb_user_info;

    @BindView(R.id.super_user_info)
    SuperTextView super_user_info;

    @BindView(R.id.stv_to_chat)
    SuperTextView stv_to_chat;

    @BindView(R.id.stv_to_audio)
    SuperTextView stv_to_audio;


    @BindView(R.id.stv_to_video)
    SuperTextView stv_to_video;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        sp = getSharedPreferences("user", MODE_PRIVATE);
        myPhone = sp.getString("phone","0");
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        userName = intent.getStringExtra("userName");
        enterpriseName = intent.getStringExtra("enterpriseName");
        headUrl = intent.getStringExtra("headUrl");
        initView();

    }

    private void initView() {
        tb_user_info.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(phone.equals(myPhone)){
            stv_to_chat.setVisibility(View.INVISIBLE);
            stv_to_audio.setVisibility(View.INVISIBLE);
            stv_to_video.setVisibility(View.INVISIBLE);
        }
        super_user_info.setCenterTopString(userName);
        super_user_info.setCenterString(enterpriseName);
        super_user_info.setCenterBottomString(phone);
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(90);
        //通过RequestOptions扩展功能
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(170, 170);
        Glide.with(this)
                .load(headUrl)
                .fallback(R.drawable.head_pic)
                .placeholder(R.drawable.head_pic)
                .error(R.drawable.head_red)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super_user_info.setLeftIcon(resource);
                    }
                });
    }

    @OnClick({R.id.stv_to_chat,R.id.stv_to_video,R.id.stv_to_audio})
    void openActivity(View view){
        Author user = new Author(phone,userName,headUrl);
        XMeetApp.getInstance().setCallUser(user);
        switch (view.getId()){
            case R.id.stv_to_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("phone",phone);
                intent.putExtra("headUrl",headUrl);
                startActivityForResult(intent,1);
                break;
            case R.id.stv_to_audio:
                SkyEngineKit.init(new VoipEvent());
                CallSingleActivity.openActivity(this, phone, true, true);
                break;
            case R.id.stv_to_video:
                SkyEngineKit.init(new VoipEvent());
                CallSingleActivity.openActivity(this, phone, true, false);
                break;
        }
    }
}
