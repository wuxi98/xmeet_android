package cn.nchu.wuxi.xlivemeet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.toast.XToast;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterprise;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {


    private static final String TAG = "LoginActivity";
    private SharedPreferences sp;

    @BindView(R.id.tv_other_login)
    XUIAlphaTextView tv_other_login;
    @BindView(R.id.fl_verify_frame)
    FrameLayout fl_verify_frame;
    @BindView(R.id.password_frame)
    FrameLayout password_frame;
    @BindView(R.id.btn_get_verify_code)
    RoundButton btn_get_verify_code;

    @BindView(R.id.et_verify_code)
    MaterialEditText et_verify_code;
    @BindView(R.id.et_username)
    MaterialEditText et_username;
    @BindView(R.id.et_password)
    MaterialEditText et_password;
//    @BindView(R.id.reg_password2)
//     EditText et_password2;
//    @BindView(R.id.reg_mail)
//     EditText et_mail;
    @BindView(R.id.button_login)
     Button btn_login;
    @BindView(R.id.button_register)
     Button btn_register;
    private CountDownButtonHelper mCountDownHelper;
    //0 密码登录 1 手机验证码登录
    private int loginType = 0;

    private static final int SEND_SUCCESS = 0, SEND_FAIL = 1, VERIFY_SUCCESS = 2, VERIFY_FAIL = 3;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SUCCESS:
                    Log.i("mob", "发送成功");
                  //  ToastUtil.normal("已发送，请等待。。。");
                    break;
                case SEND_FAIL:
                    Log.i("mob", "发送失败");
                  //  ToastUtil.normal("异常，过会请重试。。。");
                    break;
                case VERIFY_SUCCESS:
                    Log.i("mob", "验证通过");
                    try {
                        loginWithoutPassword();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //  ToastUtil.normal("验证成功");
                   // view.main();
                    break;
                case VERIFY_FAIL:
                    Log.i("mob", "验证失败");
                    ToastUtil.normal("验证失败");
                    break;
            }
        }
    };
    private String username;
    private String password;
    private String verifyCode;

    @Override
    public int getContentViewResId() {
        return R.layout.login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        sp = getSharedPreferences("user",MODE_PRIVATE);
        String phone = sp.getString("phone","0");
        String psword = sp.getString("password","0");
        String isLogin = sp.getString("login","0");
        if("1".equals(isLogin)){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
        if(!"0".equals(psword) && !"0".equals(phone)){
            autoLogin(phone,psword);
        }

        mCountDownHelper = new CountDownButtonHelper(btn_get_verify_code, 60);

        btn_login.setOnClickListener(new MyButton());
        btn_register.setOnClickListener(new MyButton());
    }

    private void autoLogin(String phone, String psword) {
        try {
            loginWithOkHttp(phone,psword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnClick({R.id.tv_other_login,R.id.btn_get_verify_code})
    void click(View v){
        switch (v.getId()){
            case R.id.tv_other_login:
                if("密码登录".equals(tv_other_login.getText().toString())){
                    fl_verify_frame.setVisibility(View.GONE);
                    password_frame.setVisibility(View.VISIBLE);
                    loginType = 0;
                    tv_other_login.setText("手机验证码登录");
            }else {
                    password_frame.setVisibility(View.GONE);
                    fl_verify_frame.setVisibility(View.VISIBLE);
                    tv_other_login.setText("密码登录");
                    loginType = 1;
                }
                break;
            case R.id.btn_get_verify_code:
                if (et_username.validate()) {
                    getVerifyCode(et_username.getEditValue());
                }
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode(String phoneNumber) {
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                if (i1 == SMSSDK.RESULT_COMPLETE) {
                    handler.sendEmptyMessage(SEND_SUCCESS);
                } else {
                    handler.sendEmptyMessage(SEND_FAIL);
                }
            }
        });
        SMSSDK.getVerificationCode("86", phoneNumber);
        mCountDownHelper.start();
    }

    public  class MyButton implements View.OnClickListener{
        @Override
        public void onClick(View view){

            username = et_username.getText().toString().trim();
            password = et_password.getText().toString().trim();
            verifyCode = et_verify_code.getText().toString().trim();
            switch (view.getId()) {
                //当点击登录按钮时
                case R.id.button_login:
                    if (loginType == 0){
                        //使用密码登录
                        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                            Toast.makeText(LoginActivity.this,"密码或账号不能为空",Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                LogUtil.d(LoginActivity.class,"当前位置 -- > case button_login");
                                LogUtil.d(LoginActivity.class,"userName="+username+",password="+password);
                                loginWithOkHttp(username, password);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                       // ToastUtil.normal(username + verifyCode);
                        //使用验证码登录
                        SMSSDK.registerEventHandler(new EventHandler() {
                            public void afterEvent(int event, int result, Object data) {
                                if (result == SMSSDK.RESULT_COMPLETE) {
                                    // TODO 处理验证成功的结果
                                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                        //提交验证码成功
                                        handler.sendEmptyMessage(VERIFY_SUCCESS);
                                        Log.i("EventHandler", "提交验证码成功");
                                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                        //获取验证码成功
                                        Log.i("EventHandler", "获取验证码成功");
                                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                        //返回支持发送验证码的国家列表
                                        Log.i("EventHandler", "返回支持发送验证码的国家列表");
                                    }
                                } else {
                                    // TODO 处理错误的结果
                                    handler.sendEmptyMessage(VERIFY_FAIL);
                                }
                            }
                        });
// 触发操作
                        SMSSDK.submitVerificationCode("86", username, verifyCode);
                    }

                    break;
                //当点击注册按钮事件时
                case R.id.button_register:
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }

    //实现登录
    public void loginWithOkHttp(String phone,String password) throws IOException {

        HttpUtil.loginWithOkHttpSync(phone, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    LogUtil.e(LoginActivity.class,"onFailure -->SocketTimeoutException");
                    LogUtil.e(LoginActivity.class,"onFailure -->"+e.getMessage());
                } else if (e instanceof ConnectTimeoutException) {
                    LogUtil.e(LoginActivity.class,"onFailure -->ConnectTimeoutException");
                    LogUtil.e(LoginActivity.class,"onFailure -->"+e.getMessage());
                }

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //String res = null;
                LogUtil.d(LoginActivity.class,"当前位置 -- > loginWithOkHttpSync -->onResponse");
                final String res = response.body().string();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                JsonReturn<TCustomer> data = gson.fromJson(res, new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                                LogUtil.d(LoginActivity.class,"对象信息 -- >"+data.toString());
                                LogUtil.d(LoginActivity.class, "成功 -- >" + data);
                                if(data.isSuccess()){
                                    processLoginSuccess(data.getData().get(0));
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.normal("账号或密码错误！");
                                        }
                                    });
                                }
                            } else {
                                String res = response.message();
                                LogUtil.d(LoginActivity.class, "message -- >" + res);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    void loginWithoutPassword() throws IOException {
        HttpUtil.loginWithoutPassword(username, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            JsonReturn<TCustomer> data = gson.fromJson(res, new TypeToken<JsonReturn<TCustomer>>() {
                            }.getType());
                            if (data.isSuccess()) {
                                processLoginSuccess(data.getData().get(0));
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.normal(data.getMessage());
                                    }
                                });
                            }
                        } else {
                            String res = response.message();
                            LogUtil.d(LoginActivity.class, "message -- >" + res);
                        }

                    }
                });
            }
        });
    }
    @Override
    protected void onDestroy() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroy();
    }


    private void processLoginSuccess(TCustomer tCustomer) {
        LogUtil.d(LoginActivity.class,"user->"+tCustomer.toString());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phone",tCustomer.getPhone());
        if(tCustomer.getNickName() != null)
            editor.putString("nickName",tCustomer.getNickName());
        else  editor.putString("nickName","用户356416");
        if(tCustomer.getEnterpriseId() != null) {
            HttpUtil.getEnterpriseById(tCustomer.getEnterpriseId(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JsonReturn<TEnterprise> data = new Gson().fromJson(response.body().string(), new TypeToken<JsonReturn<TEnterprise>>(){}.getType());

                    if (data.getData().size() > 0){
                        LogUtil.d(LoginActivity.class,"企业信息为-->"+data.getData().get(0).toString());
                        String enterpriseName = data.getData().get(0).getEnterpriseName();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("enterpriseName",enterpriseName);
                        editor.apply();
                    }

                }
            });
            editor.putInt("enterpriseId", tCustomer.getEnterpriseId());
        }
        else editor.putInt("enterpriseId",0);
        if(tCustomer.getLiveRomeId() != null)
            editor.putInt("live_rome_id",tCustomer.getLiveRomeId());
        else editor.putInt("live_rome_id",0);
        if(tCustomer.getHeadUrl() != null)
            editor.putString("head_url",tCustomer.getHeadUrl());
        else editor.putString("head_url","");
        if(tCustomer.getManageEnterpriseid() != null)
            editor.putInt("manageEnterpriseId",tCustomer.getManageEnterpriseid());
        else editor.putInt("v",0);
        editor.putString("login","1");
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
//        setResult(1);
//        finish();
        //
    }
    }