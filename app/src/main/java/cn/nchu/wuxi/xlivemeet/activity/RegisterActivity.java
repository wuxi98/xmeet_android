package cn.nchu.wuxi.xlivemeet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nchu.wuxi.xlivemeet.R;
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

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.reg_et_number)
    MaterialEditText reg_username;
    @BindView(R.id.reg_et_password)
    MaterialEditText reg_password;
    @BindView(R.id.reg_et_password2)
    MaterialEditText reg_password2;
//    @BindView(R.id.reg_mail)
//     EditText reg_mail;
    @BindView(R.id.button_ensure_register)
    RoundButton reg_btn_sure;
    @BindView(R.id.return_login)
    RoundButton reg_return_login;
    @BindView(R.id.btn_get_verify_code)
    RoundButton btn_get_verify_code;
    @BindView(R.id.et_verify_code)
    MaterialEditText et_verify_code;

    private CountDownButtonHelper mCountDownHelper;

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
                    doRegister(phone, password);
                    break;
                case VERIFY_FAIL:
                    Log.i("mob", "验证失败");
                    ToastUtil.normal("验证失败");
                    break;
            }
        }
    };
    private String verifyCode;
    private String password2;
    private String password;
    private String phone;



    @Override
    public int getContentViewResId() {
        return R.layout.register_activity;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        reg_btn_sure.setOnClickListener(this);
        reg_return_login.setOnClickListener(this);
        mCountDownHelper = new CountDownButtonHelper(btn_get_verify_code, 60);
    }

    @Override
    public void onClick(View view) {
        phone = reg_username.getText().toString().trim();
        password = reg_password.getText().toString().trim();
        password2 = reg_password2.getText().toString().trim();
        verifyCode = et_verify_code.getText().toString().trim();
        LogUtil.d(RegisterActivity.class,"p1 = "+ password+",p2="+password2);
        switch (view.getId()){
            case R.id.button_ensure_register:
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
                        Toast.makeText(RegisterActivity.this, "各项均不能为空", Toast.LENGTH_SHORT).show();
                        return;
                }
                if (!password.equals(password2)) {
                ToastUtil.normal("两次输入的密码不一样");
                return;
            }
                SMSSDK.registerEventHandler(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
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
                Log.i("mob","verifyCode="+verifyCode);
                Log.i("mob","verifyCode.length="+verifyCode.length());
                SMSSDK.submitVerificationCode("86", phone, verifyCode);
                break;
            case R.id.return_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
        }
    }

    @OnClick({R.id.btn_get_verify_code})
    void click(View v){
        switch (v.getId()){
            case R.id.btn_get_verify_code:
                if(reg_username.validate()){
                    getVerifyCode(reg_username.getEditValue());
                }
        }
    }

    private void getVerifyCode(String phone) {
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int i, int i1, Object o) {
                if (i1 == SMSSDK.RESULT_COMPLETE) {
                    handler.sendEmptyMessage(SEND_SUCCESS);
                } else {
                    Log.i("mob","event="+i);
                    Log.i("mob","result="+i1);
                    Throwable t = (Throwable)o;
                    Log.i("mob","data->"+t.getMessage());
                    handler.sendEmptyMessage(SEND_FAIL);
                }
            }
        });
        SMSSDK.getVerificationCode("86", phone);
        mCountDownHelper.start();
    }

    private void doRegister(String phone, String password){
                HttpUtil.registerWithOkHttp(phone, password, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        if (response.isSuccessful()){
                            Gson gson = new Gson();
                            JsonReturn obj = gson.fromJson(res, JsonReturn.class);
                            LogUtil.d(RegisterActivity.class, "注册返回信息"+obj.toString());
                            if(obj.isSuccess()){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                ToastUtil.showToastInAnyThread(obj.getMessage());
                            }else {
                                LogUtil.d(RegisterActivity.class,"此处应该注册失败");
                                ToastUtil.showToastInAnyThread(obj.getMessage());
                            }
                        }else {
                            LogUtil.d(RegisterActivity.class,"注册请求不成功"+response.message());
                        }
                    }
                });


    }
}
