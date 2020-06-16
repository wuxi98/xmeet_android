//package cn.nchu.wuxi.xlivemeet.webrtc.voip;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//
//import com.dds.skywebrtc.CallSession;
//import com.dds.skywebrtc.EnumType;
//import com.dds.skywebrtc.SkyEngineKit;
//import com.dds.skywebrtc.except.NotInitializedException;
//import com.dds.skywebrtc.permission.Permissions;
//
//import java.util.UUID;
//
//import cn.nchu.wuxi.xlivemeet.R;
//import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
//
//
///**
// * 多人通话界面
// */
//public class CallMultiActivity extends AppCompatActivity implements CallSession.CallSessionCallback{
//    public static final String EXTRA_TARGET = "targetId";
//    public static final String EXTRA_ROOM_ID = "room";
//    public static final String EXTRA_ROOM_NAME = "roomName";
//    public static final String EXTRA_IS_CREATE = "isCreate";
//    public static final String EXTRA_MO = "isOutGoing";
//    public static final String EXTRA_AUDIO_ONLY = "audioOnly";
//    public static final String EXTRA_FROM_FLOATING_VIEW = "fromFloatingView";
//
//    private Handler handler = new Handler();
//    private boolean isOutgoing;
//    private String roomId;
//    private String roomName;
//    private boolean isAudioOnly;
//    private boolean isFromFloatingView;
//
//    private SkyEngineKit gEngineKit;
//
//    private CallSession.CallSessionCallback currentFragment;
//    private boolean isCreate;
//
//    public static void openActivityWithJoin(Context context, String roomId, boolean isOutgoing,
//                                    boolean isAudioOnly) {
//        LogUtil.d(CallMultiActivity.class,"roomId="+roomId);
//        Intent intent = new Intent(context, CallMultiActivity.class);
//        intent.putExtra(CallMultiActivity.EXTRA_MO, isOutgoing);
//        intent.putExtra(CallMultiActivity.EXTRA_ROOM_ID, roomId);
//        intent.putExtra(CallMultiActivity.EXTRA_AUDIO_ONLY, isAudioOnly);
//        intent.putExtra(CallMultiActivity.EXTRA_FROM_FLOATING_VIEW, false);
//        intent.putExtra(CallMultiActivity.EXTRA_IS_CREATE, false);
//        if (context instanceof Activity) {
//            context.startActivity(intent);
//        } else {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }
//    }
//
//
//    public static void openActivity(Context context, String roomName, boolean isOutgoing,
//                                    boolean isAudioOnly) {
//        LogUtil.d(CallMultiActivity.class,"roomName="+roomName);
//        Intent voip = new Intent(context, CallMultiActivity.class);
//        voip.putExtra(CallMultiActivity.EXTRA_MO, isOutgoing);
//        voip.putExtra(CallMultiActivity.EXTRA_ROOM_NAME, roomName);
//        voip.putExtra(CallMultiActivity.EXTRA_AUDIO_ONLY, isAudioOnly);
//        voip.putExtra(CallMultiActivity.EXTRA_FROM_FLOATING_VIEW, false);
//        voip.putExtra(CallMultiActivity.EXTRA_IS_CREATE, true);
//        if (context instanceof Activity) {
//            context.startActivity(voip);
//        } else {
//            voip.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(voip);
//        }
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //全屏+锁屏+常亮显示
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
//        setContentView(R.layout.activity_multi_call);
//
//        try {
//            gEngineKit = SkyEngineKit.Instance();
//        } catch (NotInitializedException e) {
//            finish();
//        }
//
//        final Intent intent = getIntent();
//      //  isFromFloatingView = intent.getBooleanExtra(EXTRA_FROM_FLOATING_VIEW, false);
//        isOutgoing = intent.getBooleanExtra(EXTRA_MO, false);
//        isAudioOnly = intent.getBooleanExtra(EXTRA_AUDIO_ONLY, false);
//        roomName = intent.getStringExtra(EXTRA_ROOM_NAME);
//        roomId = intent.getStringExtra(EXTRA_ROOM_ID);
//        isCreate = intent.getBooleanExtra(EXTRA_IS_CREATE,true);
//
//
//        // 权限检测
//        String[] per;
//        if (isAudioOnly) {
//            per = new String[]{Manifest.permission.RECORD_AUDIO};
//        } else {
//            per = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
//        }
//        Permissions.request(this, per, integer -> {
//            if (integer == 0) {
//                // 权限同意
//                    init(roomId, isOutgoing, isAudioOnly, false,isCreate);
//
//            } else {
//                // 权限拒绝
//                CallMultiActivity.this.finish();
//            }
//        });
//    }
//
//    private void init(String roomId, boolean outgoing, boolean audioOnly, boolean isReplace,boolean isCreate) {
//        Fragment fragment;
//        if (audioOnly) {
//            fragment = new FragmentAudio();
//        } else {
//            fragment = new FragmentVideoMulti();
//        }
//
//        currentFragment = (CallSession.CallSessionCallback) fragment;
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (isReplace) {
//            fragmentManager.beginTransaction()
//                    .replace(android.R.id.content, fragment)
//                    .commit();
//        } else {
//            fragmentManager.beginTransaction()
//                    .add(android.R.id.content, fragment)
//                    .commit();
//        }
//        if (outgoing) {
//            boolean b =false;
//            // 创建会话
//            if (isCreate){
//                String room = UUID.randomUUID().toString() + System.currentTimeMillis();
//                b = gEngineKit.createHome(getApplicationContext(), room, roomName, audioOnly);
//            }else {
//                b = gEngineKit.joinRoom(getApplicationContext(), roomId, audioOnly);
//            }
//
//            if (!b) {
//                finish();
//                return;
//            }
//            CallSession session = gEngineKit.getCurrentSession();
//            if (session == null) {
//                finish();
//            } else {
//                session.setSessionCallback(this);
//            }
//        } else {
//            CallSession session = gEngineKit.getCurrentSession();
//            if (session == null) {
//                finish();
//            } else {
//                session.setSessionCallback(this);
//            }
//        }
//
//    }
//
//    public SkyEngineKit getEngineKit() {
//        return gEngineKit;
//    }
//
//    public boolean isOutgoing() {
//        return isOutgoing;
//    }
//
//
//    public boolean isFromFloatingView() {
//        return isFromFloatingView;
//    }
//
//    // 显示小窗
//    public void showFloatingView() {
//        if (!checkOverlayPermission()) {
//            return;
//        }
//        Intent intent = new Intent(this, FloatingVoipService.class);
//        intent.putExtra(EXTRA_ROOM_NAME, roomName);
//        intent.putExtra(EXTRA_AUDIO_ONLY, isAudioOnly);
//        intent.putExtra(EXTRA_MO, isOutgoing);
//        startService(intent);
//        finish();
//    }
//
//    private boolean checkOverlayPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            SettingsCompat.setDrawOverlays(this, true);
//            if (!SettingsCompat.canDrawOverlays(this)) {
//                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_LONG).show();
//                SettingsCompat.manageDrawOverlays(this);
//                return false;
//            }
//        }
//        return true;
//    }
//    // 切换到语音通话
//    public void switchAudio() {
//     //   init(targetId, isOutgoing, true, true);
//    }
//
//
//
//    @TargetApi(19)
//    private static int getSystemUiVisibility() {
//        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        }
//        return flags;
//    }
//
//    @Override
//    public void didCallEndWithReason(EnumType.CallEndReason var1) {
//        finish();
//    }
//
//    @Override
//    public void didChangeState(EnumType.CallState callState) {
//        if (callState == EnumType.CallState.Connected) {
//            isOutgoing = false;
//        }
//        handler.post(() -> currentFragment.didChangeState(callState));
//    }
//
//    @Override
//    public void didChangeMode(boolean var1) {
//        handler.post(() -> currentFragment.didChangeMode(var1));
//    }
//
//    @Override
//    public void didCreateLocalVideoTrack() {
//        handler.post(() -> currentFragment.didCreateLocalVideoTrack());
//    }
//
//    @Override
//    public void didReceiveRemoteVideoTrack() {
//        handler.post(() -> currentFragment.didReceiveRemoteVideoTrack());
//    }
//
//    @Override
//    public void didError(String var1) {
//        finish();
//    }
//}
