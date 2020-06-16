package cn.nchu.wuxi.xlivemeet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.IpSecManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.bean.DanmuBean;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.core.component.BarrageView;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.OKHttpSingleton;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener{

    private IjkMediaPlayer mMediaPlayer;



    private boolean isPlaying = false;
    private   boolean isFullScreen = false;
    private String videoUrl="rtmp:49.235.182.5:1935/wuxi/123";

    @BindView(R.id.btn_full_video)
    Button btn_full_screen;
    @BindView(R.id.btn_play_video)
    Button btn_play_video ;
    @BindView(R.id.btn_video_back )
    Button btn_video_back ;
    @BindView(R.id.pb_progressbar)
    ProgressBar pb_progressbar ;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.fl_group)
    FrameLayout fl_group;
    @BindView(R.id.bv)
    BarrageView barrageView;
//    @BindView(R.id.room_content)
//    TextView tv_room_content;
//    @BindView(R.id.room_ownerName)
   // TextView tv_room_ownerName;
    @BindView(R.id.stv_live_info)
    SuperTextView stv_live_info;
    @BindView(R.id.send_danmu)
    Button btn_send_danmu;
    @BindView(R.id.et_danmu)
    EditText et_danmu;
    @BindView(R.id.danmu_area)
    LinearLayout lin_danmu_area;
    private int roomId;
    private String userName;
    private String roomOwnerName;
    private String room_content;
    private String rtmpUrl = "rtmp://49.235.180.5:1935/wuxi/";
    private WebSocket socket = null;
    private PlayerActivity context;
    private SharedPreferences sp;
    private String ownerPhone;
    private String headUrl;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_player;
    }

    @Override
    public void init(Bundle savedInstanceState) {


        sp = getSharedPreferences("user", MODE_PRIVATE);
        userName = sp.getString("nickName","用户某某某");
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        roomId = bundle.getInt("roomId");
        roomOwnerName = bundle.getString("roomOwnerName","主播某某某");
        ownerPhone = bundle.getString("ownerPhone","0");
        room_content = bundle.getString("roomInfo");
      //  ToastUtil.normal("房间号为"+roomId);
        //getActionBar().hide();
        context = this;
        new Thread( ()->
                Glide.get(this).clearDiskCache()
        ).start();
        Glide.get(this).clearMemory();
        initView();
        initSurfaceView();
        initListener();

        initPlayer();
        initDanmu();

    }

    private void initView() {
        stv_live_info.setLeftTopString(String.format("%s", roomOwnerName));
        stv_live_info.setLeftBottomString(String.format("%s%s", "主播公告：", room_content));
        HttpUtil.getHeadUrl(ownerPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<String> res = new Gson().fromJson(s,new TypeToken<JsonReturn<String>>(){}.getType());
                        if(res.isSuccess()){
                            headUrl = res.getData().get(0);
                            LogUtil.d(PlayerActivity.class,"headurl->"+headUrl);
                            RoundedCorners roundedCorners= new RoundedCorners(90);
                            //通过RequestOptions扩展功能
                            RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(170, 170);
                            Glide.with(context)
                                    .load(headUrl)
                                    .fallback(R.drawable.head_pic)
                                    .placeholder(R.drawable.head_pic)
                                    .error(R.drawable.head_red)
                                    .apply(options)
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @androidx.annotation.Nullable Transition<? super Drawable> transition) {
                                            stv_live_info.setLeftIcon(resource);
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    private void initListener() {
        btn_full_screen.setOnClickListener(this);
        btn_play_video.setOnClickListener(this);
        btn_video_back.setOnClickListener(this);
        btn_send_danmu.setOnClickListener(this);

    }

    private void initSurfaceView(){
//        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(100), 3000);
    }
    private void initPlayer(){
        mMediaPlayer = new IjkMediaPlayer();
    }



    /**
     * dp2px  动态设置视频的宽高
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }


    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                btn_video_back.setVisibility(View.VISIBLE);
                btn_play_video.setVisibility(View.VISIBLE);
                btn_full_screen.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(100, 3000);
                break;

            default:
                break;
        }
        return true;
    };


     Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 100:
                    if(btn_full_screen !=null) btn_full_screen.setVisibility(View.GONE);
                    if(btn_play_video !=null) btn_play_video.setVisibility(View.GONE);
                    if(btn_video_back !=null) btn_video_back.setVisibility(View.GONE);
                    if(pb_progressbar !=null) pb_progressbar.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }

        };
    };

    class PreparedListener implements IMediaPlayer.OnPreparedListener {
        int postSize;
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.e("onPrepared", "----onPrepared");
            pb_progressbar.setVisibility(View.GONE); // 取消dialog
            btn_play_video.setBackgroundResource(R.drawable.ic_play);
//            videoView.setBackgroundColor(Color.argb(0, 0, 255, 0));
            if (mMediaPlayer != null) {
                isPlaying=true;
                mMediaPlayer.start(); // 播放
                mHandler.sendMessageDelayed(mHandler.obtainMessage(100), 3000);
            } else {
                //return;
            }
        }
    }

    protected void playVideo(SurfaceHolder surfaceHolder) {

        try {
            mMediaPlayer.reset(); // 重置
            mMediaPlayer.setDataSource(videoUrl);
            // 视频地址
            mMediaPlayer.setDisplay(surfaceHolder); // holder
//            mMediaPlayer.setOnPreparedListener(new PreparedListener()); //

//            mMediaPlayer.prepareAsync();
        }catch (IOException e) {
            Log.e("918", e.toString());
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {

            mMediaPlayer.setDataSource(rtmpUrl+roomId);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setDisplay(surfaceHolder);
        mMediaPlayer.prepareAsync();
        mMediaPlayer.start();
        //将所播放的视频图像输出到指定的SurfaceView组件

//        if (videoUrl != null || !"".equals(videoUrl)) {
//            new Thread(){
//                public void run() {
//                    playVideo(surfaceHolder);
//                };
//            }.start();
//        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();

            pb_progressbar.setVisibility(View.VISIBLE);
        }


    }





    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_full_video:

                if (isFullScreen) { // 全屏转半屏

                    isFullScreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            dp2px(PlayerActivity.this, 200));
                    fl_group.setLayoutParams(lp);

                } else { // 非全屏切换全屏
                    isFullScreen = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    fl_group.setLayoutParams(lp);
                }

                break;

            case R.id.btn_video_back:

                if (isFullScreen) { // 全屏转半屏

                    isFullScreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            dp2px(PlayerActivity.this, 200));
                    fl_group.setLayoutParams(lp);

                }else{
                    Intent intent = new Intent(PlayerActivity.this,MainActivity.class);
                    startActivity(intent);

                }
                break;
            case R.id.btn_play_video:
                if(isPlaying&&mMediaPlayer!=null){   //视频的播放与暂停
                    mMediaPlayer.pause();
                    isPlaying=false;

                    btn_play_video.setBackground(getResources().getDrawable(R.drawable.ic_play));

                }else{

                    btn_play_video.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                    mMediaPlayer.start();
                    isPlaying=true;

                }

                break;
            case R.id.send_danmu:
                String msg = et_danmu.getText().toString().trim();
                if(socket != null) socket.send(new Gson().toJson(new DanmuBean(userName,""+roomId,msg,false)));
                et_danmu.setText("");
            default:
                break;
        }
    }

    public void initDanmu(){
        //ToastUtil.normal("初始化弹幕");

        HttpUtil.connectDanmu(new WebSocketListener() {
            //线程池
            ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // ToastUtil.normal("open");
                socket = webSocket;
                DanmuBean bean = new DanmuBean(userName,""+roomId,"open",true);
                socket.send(new Gson().toJson(bean));
                LogUtil.e(PlayerActivity.class,"已连接上，发送消息");
                Message msg = Message.obtain();
                msg.what = 2;
                String tip = "您已连接至弹幕服务器";
                msg.obj = tip;
                danmuHandler.sendMessage(msg);

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //获取到服务器发送过来的信息，然后通过handler进行UI线程的操作

                Message message = Message.obtain();
                message.what = 1;
                message.obj = text;
                danmuHandler.sendMessage(message);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Message message = Message.obtain();
                message.what = 2;
                String tip = "您已断开连接";
                message.obj = tip;
                danmuHandler.sendMessage(message);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                t.printStackTrace();
                // LogUtil.e(PlayerActivity.class,"websocket --> onFailure ->response.message() -->"+response.message());
                //   LogUtil.e(PlayerActivity.class,"websocket --> onFailure ->t.getMessage() -->"+t.getMessage());
            }
        });
        /*OkHttpClient client = OKHttpSingleton.getInstance();
        Request request= new Request.Builder()
                .url("ws://192.168.0.105:8899/ws")
                .build();
        client.newWebSocket(request, new WebSocketListener() {
            //线程池
            ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                   // ToastUtil.normal("open");
                    socket = webSocket;
                    DanmuBean bean = new DanmuBean(userName,""+roomId,"open",true);
                    socket.send(new Gson().toJson(bean));
                    LogUtil.e(PlayerActivity.class,"已连接上，发送消息");
                    Message msg = Message.obtain();
                    msg.what = 2;
                    String tip = "您已连接至弹幕服务器";
                    msg.obj = tip;
                    danmuHandler.sendMessage(msg);

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //获取到服务器发送过来的信息，然后通过handler进行UI线程的操作

                Message message = Message.obtain();
                message.what = 1;
                message.obj = text;
                danmuHandler.sendMessage(message);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Message message = Message.obtain();
                message.what = 2;
                String tip = "您已断开连接";
                message.obj = tip;
                danmuHandler.sendMessage(message);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                t.printStackTrace();
               // LogUtil.e(PlayerActivity.class,"websocket --> onFailure ->response.message() -->"+response.message());
             //   LogUtil.e(PlayerActivity.class,"websocket --> onFailure ->t.getMessage() -->"+t.getMessage());
            }
        });*/

    }

    final Handler danmuHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TextView danmu = new TextView(context);
                    danmu.setText((String) msg.obj);
                   if(lin_danmu_area!=null) lin_danmu_area.addView(danmu);
                    if(null != barrageView) {
                        String s = (String) msg.obj;
                        barrageView.addTextitem((s.substring(s.indexOf(":")+1)));
                    }
                    break;
                case 2:
                   // LogUtil.d(PlayerActivity.class,"已连接上，添加新弹幕");
                 //   ToastUtil.normal("已连接");
                    TextView tip = new TextView(context);
                    tip.setTextColor(Color.YELLOW);
                    tip.setText((String) msg.obj);
                    if(lin_danmu_area!=null) lin_danmu_area.addView(tip);
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(isFullScreen){   //全屏切换半屏
                    isFullScreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            dp2px(PlayerActivity.this, 200));
                    fl_group.setLayoutParams(lp);
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
