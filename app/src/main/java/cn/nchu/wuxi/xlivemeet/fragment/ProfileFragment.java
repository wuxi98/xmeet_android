package cn.nchu.wuxi.xlivemeet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
//import com.luck.picture.lib.listener.OnResultCallbackListener;
//import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xui.widget.textview.badge.Badge;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.activity.AboutActivity;
import cn.nchu.wuxi.xlivemeet.activity.EnterpriseManageActivity;
import cn.nchu.wuxi.xlivemeet.activity.InfoActivity;
import cn.nchu.wuxi.xlivemeet.activity.MainActivity;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterprise;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.nchu.wuxi.xlivemeet.utils.Utils;
//import cn.nchu.wuxi.xlivemeet.utils.picture.GlideEngine;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends BaseFragment{

    @BindView(R.id.stv_staff)
    SuperTextView enterprise;

    @BindView(R.id.super_user_info)
    SuperTextView super_user_info;

    @BindView(R.id.info_message)
    SuperTextView info_message;

    @BindView(R.id.about_stv)
    SuperTextView about_stv;

    private SharedPreferences sp;
    private String userName;
    private String phone;
    private String enterpriseName;
    private FragmentActivity context;
    private String head_url;
    private Badge mBadge;
    private ProfileFragment _this;

    private List<LocalMedia> mSelectList = new ArrayList<>();
    private List<LocalMedia> images;
    private Handler mainHandler;
    private MainActivity mainActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.profile_fragment;
    }

    @Override
    protected void init() {
        _this = this;
        context = getActivity();
        mainActivity = (MainActivity) this.context;
        mainHandler = mainActivity.handler;
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userName = sp.getString("nickName","无名用户");
        phone = sp.getString("phone",null);
        enterpriseName = sp.getString("enterpriseName","暂无企业");
        head_url = sp.getString("head_url","");
        initView();
        initListener();

    }

    void loadHead(){
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(90);
        //通过RequestOptions扩展功能
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(170, 170);
        Glide.with(context)
                .load(head_url)
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

    private void initView() {
        super_user_info.setLeftTopString(userName);
        super_user_info.setLeftBottomString(enterpriseName);

        loadHead();
        //设置空字符串用于占位
        info_message.setRightString("      ");
//        mBadge = new BadgeView(getContext()).bindTarget(info_message.getRightTextView())
//                .setBadgeGravity(Gravity.END | Gravity.CENTER)
//                .setBadgePadding(3, true)
//                .setBadgeTextSize(9, true)
//                .setBadgeNumber(3);

    }
    private void initListener() {
        enterprise.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                Intent intent = new Intent(getActivity(), EnterpriseManageActivity.class);
                startActivity(intent);
                // openNewPage(EnterpriseManageFragment.class);
            }
        });
        super_user_info.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                new MaterialDialog.Builder(context)
                         .title(R.string.tip_options)
                        .items(R.array.menu_update_user_infos)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                if(position == 0){
                                    Utils.getPictureSelector(_this)
                                            .enableCrop(true)
                                            .setCircleDimmedColor(R.color.picture_color_grey_3e)
                                            .setCircleStrokeWidth(3)
                                            .selectionMode(PictureConfig.SINGLE)
                                            .maxSelectNum(1)
                                            .isCamera(true)
                                            .minSelectNum(1)
                                            .setLanguage(0)
                                            .circleDimmedLayer(false)
                                            .withAspectRatio(1,1)
                                            .showCropFrame(true)
                                            .showCropGrid(false)
                                            .isDragFrame(true)
                                            .freeStyleCropEnabled(true)
                                            .previewImage(true)
                                            .compress(true)
                                            .selectionMedia(mSelectList)
                                            .forResult(PictureConfig.CHOOSE_REQUEST);
                                }else if(position == 1){
                                    DialogLoader.getInstance().showInputDialog(
                                            context,
                                            R.drawable.icon_tip,
                                            getString(R.string.tip_info),
                                            "输入要修改的姓名",
                                            new InputInfo(InputType.TYPE_CLASS_TEXT,
                                                    "输入姓名"),
                                            null,
                                            getString(R.string.ensure),
                                            (dialogInterface, i) -> {
                                                if (dialogInterface instanceof MaterialDialog) {
                                                    String s = ((MaterialDialog) dialogInterface).getInputEditText()
                                                            .getText().toString();
                                                    //  ToastUtil.normal("你输入了:" + s);
                                                    HttpUtil.updateUserName(s, phone, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            LogUtil.i(EnterpriseManageActivity.class,"修改onFailure");
                                                            ToastUtil.normal("修改失败！");
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            context.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    ToastUtil.normal("修改成功!");
                                                                    SharedPreferences.Editor editor = sp.edit();
                                                                    editor.putString("nickName",s);
                                                                    editor.apply();
                                                                    userName = s;
                                                                    super_user_info.setLeftTopString(userName);
                                                                }
                                                            });

//                                                            final String s = response.body().string();
//                                                            context.runOnUiThread(new Runnable() {
//                                                                @Override
//                                                                public void run() {
//                                                                    JsonReturn<TCustomer> res = new Gson().fromJson(s, new TypeToken<JsonReturn<TCustomer>>(){}.getType());
//                                                                    if(res.isSuccess()){
//                                                                        ToastUtil.normal("修改成功!");
//                                                                        SharedPreferences.Editor editor = sp.edit();
//                                                                        editor.putString("nickName",res.getData().get(0).getNickName());
//                                                                        editor.apply();
//                                                                    }else {
//                                                                        ToastUtil.normal("修改失败!");
//                                                                    }
//                                                                }
//                                                            });

                                                        }
                                                    });
                                                }
                                            },
                                            getString(R.string.lab_cancel),
                                            null);
                                }
                            }
                        })
                        .show();
            }
        });
        info_message.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                startActivity(intent);
            }
        });
        about_stv.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LogUtil.e(ProfileFragment.class,"在onActivityResult-->");
        if (resultCode == Activity.RESULT_OK) {
            LogUtil.e(ProfileFragment.class,"在resultCode == Activity.RESULT_OK-->");
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:

                    LocalMedia image = PictureSelector.obtainMultipleResult(data).get(0);
                   // String path = image.getPath();
                    String path = image.getCutPath();
                    LogUtil.e(ProfileFragment.class,"upload path-->"+path);
                    uploadHead(path);
                    break;
                default:
                    break;
            }
        }
    }

    private void uploadHead(String path) {
        String[] split = path.split("/");
        String fileName = split[split.length-1];
        HttpUtil.uploadHead(path, fileName, phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(ProfileFragment.class,"receive data -->"+s);
                        JsonReturn<String> res = new Gson().fromJson(s, new TypeToken<JsonReturn<String>>(){}.getType());
                        if(res.isSuccess()){
                            String url = res.getData().get(0);
                            LogUtil.d(ProfileFragment.class,"url为"+url);
                            head_url = url;
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("head_url",head_url);
                            edit.apply();

                            loadHead();
                            //loadHeadFromServer();
                            mainHandler.post(mainActivity::reLoadHead);
                        }else {
                            ToastUtil.normal("上传头像失败！");
                        }


                    }
                });
            }
        });
    }

    void loadHeadFromServer(){
        HttpUtil.getHeadUrl(phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(ProfileFragment.class,"loadHeadFromServer:onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


            }
        });
    }





}
