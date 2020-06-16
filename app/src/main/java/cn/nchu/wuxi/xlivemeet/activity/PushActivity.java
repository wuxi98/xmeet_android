package cn.nchu.wuxi.xlivemeet.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.anbetter.danmuku.DanMuView;
import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.utils.DimensionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;
import cn.nchu.wuxi.xlivemeet.bean.DanmuBean;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.core.component.BarrageView;
import cn.nchu.wuxi.xlivemeet.fragment.ProfileFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.nchu.wuxi.xlivemeet.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static cn.nchu.wuxi.xlivemeet.R.color.xui_config_color_gray_6;

public class PushActivity extends BaseActivity  implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener, View.OnClickListener{

    private SrsPublisher mPublisher;
    private boolean isFullScreen = false;
    private SharedPreferences sp;
    private String rtmpUrl = "rtmp://49.235.180.5:1935/wuxi/";

    //播放器组件
    @BindView(R.id.btn_full_video)
    Button btn_full_screen;
    @BindView(R.id.btn_play_video)
    Button btn_play_video ;
    @BindView(R.id.btn_video_back )
    Button btn_video_back ;
//    @BindView(R.id.pb_progressbar)
//    ProgressBar pb_progressbar ;
    @BindView(R.id.fl_group)
    FrameLayout fl_group;

    //Publisher组件
    @BindView(R.id.push_publish)
    Button push_publish;
    @BindView(R.id.push_switch_cam)
    Button push_switch_cam;

    @BindView(R.id.push_switch_encoder)
    Button push_switch_encoder;

    @BindView(R.id.surfaceView)
    SrsCameraView cameraView;
    @BindView(R.id.danmu_area)
    LinearLayout lin_danmu_area;
    @BindView(R.id.et_danmu)
    EditText et_danmu;
    @BindView(R.id.send_danmu)
    Button btn_send_danmu;
//    @BindView(R.id.room_content)
//    TextView tv_room_content;
//    @BindView(R.id.room_ownerName)
//    TextView tv_room_ownerName;
    @BindView(R.id.stv_live_info)
    SuperTextView stv_live_info;
    @BindView(R.id.bv)
    BarrageView danmuView;

    private String roomOwnerName;
    private String room_content;
    private PushActivity context;
    private WebSocket socket;
    private String userName;
    private int roomId;
    private String headUrl;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_push;
    }

    Handler mHandler=new Handler(Looper.getMainLooper());

    void setInvisibility(){
        if(btn_full_screen != null) btn_full_screen.setVisibility(View.GONE);
        if(btn_play_video != null) btn_play_video.setVisibility(View.GONE);
        if(btn_video_back != null) btn_video_back.setVisibility(View.GONE);
    }
    @Override
    public void init(Bundle savedInstanceState) {
        context = this;
        initPushData();
        initSurfaceView();
        initListener();

        initPlayer();
        initPublisher();
        initDanme();
    }

    private void initView() {
        HttpUtil.getRoomInfo(roomId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(PushActivity.class,"主播请求房间信息onFailure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String s = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonReturn<TLiveRome> res = gson.fromJson(s, new TypeToken<JsonReturn<TLiveRome>>(){}.getType());
                        if(res.getData().size() == 0){
                            try {
                                registerRoom();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            TLiveRome rome = res.getData().get(0);
//                            tv_room_content.setText(String.format("%s%s", tv_room_content.getText(),rome.getContent()));
//                            tv_room_ownerName.setText(String.format("%s%s", tv_room_ownerName.getText(),rome.getOwnerName()));
                            stv_live_info.setLeftTopString(String.format("%s",rome.getOwnerName()));
                            stv_live_info.setLeftBottomString(String.format("%s%s", "主播公告:",rome.getContent()));
                        }

                    }
                });
            }
        });
    }

    private void registerRoom() throws IOException {
        String phone = sp.getString("phone","0");
        if(!"0".equals(phone)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = null;
                    try {
                        response = HttpUtil.registerLiveRoom(phone);

                    String res = response.body().string();
                    JsonReturn<TCustomer> obj = new Gson().fromJson(res,new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                    if(obj.isSuccess()) {
                        TCustomer customer = obj.getData().get(0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("live_room_id", customer.getLiveRomeId());
                        editor.apply();
                        roomId = customer.getLiveRomeId();
                        initView();
                    }else {
                        LogUtil.e(PushActivity.class,obj.getMessage());
//                        ToastUtil.normal(obj.getMessage());
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }
    private void initPushData()  {
        sp = getSharedPreferences("user",MODE_PRIVATE);
        roomId = sp.getInt("live_rome_id",0);
        userName = sp.getString("nickName","用户某某某");
        headUrl = sp.getString("head_url",null);
        if (roomId == 0){
            try {
                registerRoom();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            initView();
        }
    }

    private void initSurfaceView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPublisher = new SrsPublisher(cameraView);
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(360, 640);
        mPublisher.setOutputResolution(360, 640);
        mPublisher.switchToHardEncoder();
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();
        mHandler.postDelayed(this::setInvisibility,3000);

    }

    private void initListener() {
        btn_full_screen.setOnClickListener(this);
        btn_video_back.setOnClickListener(this);
        push_publish.setOnClickListener(this);
        push_switch_cam.setOnClickListener(this);
        push_switch_encoder.setOnClickListener(this);
        btn_send_danmu.setOnClickListener(this);
        stv_live_info.setRightTopTvClickListener(textView ->
                        showDialog("输入主播公告", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            if (dialogInterface instanceof MaterialDialog) {
                                String s = ((MaterialDialog) dialogInterface).getInputEditText().getText().toString();

                                HttpUtil.updateLiveNotice(roomId, s, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        ToastUtil.normal("修改失败");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        mHandler.post(context::initView);


                                    }
                                });
                            }
                        }));

        stv_live_info.setRightBottomTvClickListener(textView ->
                Utils.getPictureSelector22(this)
                        .enableCrop(true)
                        .setCircleDimmedColor(R.color.picture_color_grey_3e)
                        .setCircleStrokeWidth(3)
                        .selectionMode(PictureConfig.SINGLE)
                        .maxSelectNum(1)
                        .isCamera(true)
                        .minSelectNum(1)
                        .setLanguage(0)
                        .circleDimmedLayer(false)
                        .withAspectRatio(4,3)
                        .showCropFrame(true)
                        .showCropGrid(false)
                        .isDragFrame(true)
                        .freeStyleCropEnabled(true)
                        .previewImage(true)
                        .compress(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST));

    }



    private void initPlayer() {
    }
    private void initPublisher() {
        //设置图片圆角角度
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


    @Override
    protected void onStart() {
        super.onStart();
        if(mPublisher.getCamera() == null){
            //if the camera was busy and available again
            mPublisher.startCamera();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        HttpUtil.changeRoomState(roomId, "offline", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

  //      mPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (push_publish.getText().toString().contentEquals("停止推流")) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                btn_video_back.setVisibility(View.VISIBLE);
                btn_full_screen.setVisibility(View.VISIBLE);
                mHandler.postDelayed(this::setInvisibility, 3000);
                break;

            default:
                break;
        }
        return true;
    };

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

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_video:

                if (isFullScreen) { // 全屏转半屏

                    isFullScreen = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 手动横屏
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            dp2px(PushActivity.this, 200));
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
                            dp2px(PushActivity.this, 200));
                    fl_group.setLayoutParams(lp);

                }else{
                    Intent intent = new Intent(PushActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.push_publish:

                if (push_publish.getText().toString().contentEquals("开始推流")) {


                    mPublisher.startPublish(rtmpUrl+roomId);
                    mPublisher.startCamera();

                    if (push_switch_encoder.getText().toString().contentEquals("软解码")) {
                        ToastUtil.normal("使用硬解码");
                    } else {
                        ToastUtil.normal("使用软解码");

                    }
                    push_publish.setText(R.string.stop_publish);
                    push_publish.setBackgroundColor(Color.parseColor("#FF00FF"));
                    push_switch_encoder.setEnabled(false);
                    //push_pause.setEnabled(true);
                    HttpUtil.changeRoomState(roomId, "online", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });

                } else if (push_publish.getText().toString().contentEquals("停止推流")) {


                    mPublisher.stopPublish();
                    mPublisher.stopRecord();
                    push_publish.setText(R.string.start_publish);
                    push_publish.setBackgroundColor(Color.parseColor("#FFFFFF"));
                   // btnRecord.setText("record");
                    push_switch_encoder.setEnabled(true);
                    HttpUtil.changeRoomState(roomId, "offline", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                        }
                    });
                }
                break;
            case R.id.push_switch_cam:
                mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
                break;
            case R.id.push_switch_encoder:
                if (push_switch_encoder.getText().toString().contentEquals("软解码")) {
                    mPublisher.switchToSoftEncoder();
                    push_switch_encoder.setText(R.string.hard_encoder);
                } else if (push_switch_encoder.getText().toString().contentEquals("硬解码")) {
                    mPublisher.switchToHardEncoder();
                    push_switch_encoder.setText(R.string.soft_encoder);
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

    void initDanme(){

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
    }

    final Handler danmuHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TextView danmu = new TextView(context);
                    danmu.setText((String) msg.obj);
                    if(lin_danmu_area!=null) lin_danmu_area.addView(danmu);
                    if(null != danmuView) {

                        String s = (String) msg.obj;
                        danmuView.addTextitem((s.substring(s.indexOf(":")+1)));
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

    @Override
    public void onRtmpConnecting(String msg) {

    }

    @Override
    public void onRtmpConnected(String msg) {

    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {

    }

    @Override
    public void onRtmpDisconnected() {

    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {

    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpSocketException(SocketException e) {

    }

    @Override
    public void onRtmpIOException(IOException e) {

    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {

    }

    @Override
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String msg) {

    }

    @Override
    public void onRecordFinished(String msg) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordIOException(IOException e) {

    }

    void showDialog(String tip, DialogInterface.OnClickListener listener){
        DialogLoader.getInstance().showInputDialog(
                this,
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
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    LocalMedia image = PictureSelector.obtainMultipleResult(data).get(0);
                    String path = image.getCutPath();
                    uploadPage(path);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void uploadPage(String path) {
        String[] split = path.split("/");
        String fileName = split[split.length-1];
        HttpUtil.uploadPage(path, fileName, roomId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.normal("上传封面成功");
                    }
                });

            }
        });
    }



}
