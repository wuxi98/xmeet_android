package cn.nchu.wuxi.xlivemeet.webrtc.voip;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.dds.skywebrtc.CallSession;
import com.dds.skywebrtc.EnumType;
import com.dds.skywebrtc.SkyEngineKit;

import org.webrtc.SurfaceViewRenderer;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;


/**
 * Created by dds on 2018/7/26.
 * android_shuai@163.com
 * 视频通话控制界面
 */
public class FragmentVideo extends Fragment implements CallSession.CallSessionCallback, View.OnClickListener {

    private FrameLayout fullscreenRenderer;
    private FrameLayout pipRenderer;
    private LinearLayout inviteeInfoContainer;
    private ImageView portraitImageView;
    private TextView nameTextView;
    private TextView descTextView;
    private ImageView minimizeImageView;
    private ImageView outgoingAudioOnlyImageView;
    private ImageView outgoingHangupImageView;
    private LinearLayout audioLayout;
    private ImageView incomingAudioOnlyImageView;
    private LinearLayout hangupLinearLayout;
    private ImageView incomingHangupImageView;
    private LinearLayout acceptLinearLayout;
    private ImageView acceptImageView;
    private Chronometer durationTextView;
    private ImageView connectedAudioOnlyImageView;
    private ImageView connectedHangupImageView;
    private ImageView switchCameraImageView;

    private View incomingActionContainer;
    private View outgoingActionContainer;
    private View connectedActionContainer;


    private CallSingleActivity activity;
    private SkyEngineKit gEngineKit;
    private boolean isOutgoing;
    private boolean isFromFloatingView;
    private SurfaceViewRenderer localSurfaceView;
    private SurfaceViewRenderer remoteSurfaceView;

    //对方信息
    private Author user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        initView(view);
        init();
        return view;
    }


    private void initView(View view) {
        fullscreenRenderer = view.findViewById(R.id.fullscreen_video_view);
        pipRenderer = view.findViewById(R.id.pip_video_view);
        inviteeInfoContainer = view.findViewById(R.id.inviteeInfoContainer);
        portraitImageView = view.findViewById(R.id.portraitImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        descTextView = view.findViewById(R.id.descTextView);
        minimizeImageView = view.findViewById(R.id.minimizeImageView);
        outgoingAudioOnlyImageView = view.findViewById(R.id.outgoingAudioOnlyImageView);
        outgoingHangupImageView = view.findViewById(R.id.outgoingHangupImageView);
        audioLayout = view.findViewById(R.id.audioLayout);
        incomingAudioOnlyImageView = view.findViewById(R.id.incomingAudioOnlyImageView);
        hangupLinearLayout = view.findViewById(R.id.hangupLinearLayout);
        incomingHangupImageView = view.findViewById(R.id.incomingHangupImageView);
        acceptLinearLayout = view.findViewById(R.id.acceptLinearLayout);
        acceptImageView = view.findViewById(R.id.acceptImageView);
        durationTextView = view.findViewById(R.id.durationTextView);
        connectedAudioOnlyImageView = view.findViewById(R.id.connectedAudioOnlyImageView);
        connectedHangupImageView = view.findViewById(R.id.connectedHangupImageView);
        switchCameraImageView = view.findViewById(R.id.switchCameraImageView);

        incomingActionContainer = view.findViewById(R.id.incomingActionContainer);
        outgoingActionContainer = view.findViewById(R.id.outgoingActionContainer);
        connectedActionContainer = view.findViewById(R.id.connectedActionContainer);

        outgoingHangupImageView.setOnClickListener(this);
        incomingHangupImageView.setOnClickListener(this);
        connectedHangupImageView.setOnClickListener(this);
        acceptImageView.setOnClickListener(this);
        switchCameraImageView.setOnClickListener(this);

        outgoingAudioOnlyImageView.setOnClickListener(this);
        incomingAudioOnlyImageView.setOnClickListener(this);
        connectedAudioOnlyImageView.setOnClickListener(this);

        minimizeImageView.setOnClickListener(this);

        if((user = XMeetApp.getInstance().getCallUser()) != null){
            nameTextView.setText(user.getName());
            Glide.with(getActivity())
                    .load(user.getAvatar())
                    .error(R.drawable.ac_default_header)
                    .placeholder(R.drawable.ac_default_header)
                    .fallback(R.drawable.head_pic)
                    .into(portraitImageView);
        }

    }

    private void init() {
        gEngineKit = activity.getEngineKit();
        CallSession session = gEngineKit.getCurrentSession();
        LogUtil.d(FragmentVideo.class,"session.getState为---》"+session.getState().toString());
        if (session == null || EnumType.CallState.Idle == session.getState()) {
            activity.finish();
        } else if (EnumType.CallState.Connected == session.getState()) {
            incomingActionContainer.setVisibility(View.GONE);
            outgoingActionContainer.setVisibility(View.GONE);
            connectedActionContainer.setVisibility(View.VISIBLE);
            inviteeInfoContainer.setVisibility(View.GONE);
            minimizeImageView.setVisibility(View.VISIBLE);
            startRefreshTime();
        } else {
            if (isOutgoing) {
                incomingActionContainer.setVisibility(View.GONE);
                outgoingActionContainer.setVisibility(View.VISIBLE);
                connectedActionContainer.setVisibility(View.GONE);
                descTextView.setText(R.string.av_waiting);
            } else {
                incomingActionContainer.setVisibility(View.VISIBLE);
                outgoingActionContainer.setVisibility(View.GONE);
                connectedActionContainer.setVisibility(View.GONE);
                descTextView.setText(R.string.av_video_invite);
            }
        }

        if (isFromFloatingView) {
            didCreateLocalVideoTrack();
            didReceiveRemoteVideoTrack();
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (CallSingleActivity) getActivity();
        if (activity != null) {
            isOutgoing = activity.isOutgoing();
            isFromFloatingView = activity.isFromFloatingView();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void didCallEndWithReason(EnumType.CallEndReason var1) {

    }

    @Override
    public void didChangeState(EnumType.CallState state) {
        runOnUiThread(() -> {
            if (state == EnumType.CallState.Connected) {
                incomingActionContainer.setVisibility(View.GONE);
                outgoingActionContainer.setVisibility(View.GONE);
                connectedActionContainer.setVisibility(View.VISIBLE);
                inviteeInfoContainer.setVisibility(View.GONE);
                descTextView.setVisibility(View.GONE);
                minimizeImageView.setVisibility(View.VISIBLE);
                // 开启计时器
                startRefreshTime();
            } else {
                // do nothing now
            }
        });
    }

    @Override
    public void didChangeMode(boolean isAudio) {
        runOnUiThread(() -> activity.switchAudio());

    }

    @Override
    public void didCreateLocalVideoTrack() {
        LogUtil.d(FragmentVideo.class,"into --->didCreateLocalVideoTrack");
        SurfaceViewRenderer surfaceView = gEngineKit.getCurrentSession().createRendererView();
        if (surfaceView != null) {
            surfaceView.setZOrderMediaOverlay(true);
            localSurfaceView = surfaceView;
            if (isOutgoing && remoteSurfaceView == null) {
                fullscreenRenderer.addView(surfaceView);
            } else {
                pipRenderer.addView(surfaceView);
            }
            gEngineKit.getCurrentSession().setupLocalVideo(surfaceView);
        }
    }

    @Override
    public void didReceiveRemoteVideoTrack() {
        LogUtil.d(FragmentVideo.class,"into --->didReceiveRemoteVideoTrack");
        pipRenderer.setVisibility(View.VISIBLE);
        if (isOutgoing && localSurfaceView != null) {
            ((ViewGroup) localSurfaceView.getParent()).removeView(localSurfaceView);
            pipRenderer.addView(localSurfaceView);
            gEngineKit.getCurrentSession().setupLocalVideo(localSurfaceView);
        }

        SurfaceViewRenderer surfaceView = gEngineKit.getCurrentSession().createRendererView();
        if (surfaceView != null) {
            remoteSurfaceView = surfaceView;
            fullscreenRenderer.removeAllViews();
            fullscreenRenderer.addView(surfaceView);
            gEngineKit.getCurrentSession().setupRemoteVideo(surfaceView);
        }
    }

    @Override
    public void didError(String error) {

    }

    private void runOnUiThread(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (localSurfaceView != null) {
            localSurfaceView.release();
        }
        if (remoteSurfaceView != null) {
            remoteSurfaceView.release();
        }
        fullscreenRenderer.removeAllViews();
        pipRenderer.removeAllViews();

        if (durationTextView != null) {
            durationTextView.stop();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 接听
        CallSession session = gEngineKit.getCurrentSession();
        if (id == R.id.acceptImageView) {
            Log.e("wuxi","点击接听");
            if (session != null && session.getState() == EnumType.CallState.Incoming) {
                Log.e("wuxi","加入房间");
                session.joinHome();
            } else {
                Log.e("wuxi","状态不对");
                activity.finish();
            }
        }
        // 挂断电话
        if (id == R.id.incomingHangupImageView || id == R.id.outgoingHangupImageView ||
                id == R.id.connectedHangupImageView) {
            if (session != null) {
                SkyEngineKit.Instance().endCall();
                activity.finish();
            } else {
                activity.finish();
            }
        }

        // 切换摄像头
        if (id == R.id.switchCameraImageView) {
            if (session != null) {
                session.switchCamera();
            }
        }

        // 切换到语音拨打
        if (id == R.id.outgoingAudioOnlyImageView || id == R.id.incomingAudioOnlyImageView ||
                id == R.id.connectedAudioOnlyImageView) {
            if (session != null) {
                session.switchToAudio();
            }

        }

        // 小窗
        if (id == R.id.minimizeImageView) {
            activity.showFloatingView();
        }
    }

    private void startRefreshTime() {
        CallSession session = SkyEngineKit.Instance().getCurrentSession();
        if (session == null) {
            return;
        }
        if (durationTextView != null) {
            durationTextView.setVisibility(View.VISIBLE);
            durationTextView.setBase(SystemClock.elapsedRealtime() - (System.currentTimeMillis() - session.getStartTime()));
            durationTextView.start();
        }
    }

}
