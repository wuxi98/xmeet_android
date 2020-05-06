//package cn.nchu.wuxi.xlivemeet.utils;
//
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.william_zhang.base.mvp.baseImpl.BasePresenterImpl;
//import com.william_zhang.base.retrofit.ExceptionHelper;
////import com.william_zhang.williamapp.manager.BombManager;
//import com.william_zhang.williamapp.mvp.activity.NodeLoginActivity;
//import com.william_zhang.williamapp.mvp.contact.NodeLoginContact;
//
//import java.util.concurrent.TimeUnit;
//
//
////import cn.bmob.newsmssdk.BmobSMS;
////import cn.bmob.newsmssdk.exception.BmobException;
////import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
////import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;
//import io.reactivex.Observable;
////import io.reactivex.Scheduler;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Action;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//
///**
// * Created by william_zhang on 2018/2/8.
// */
//
//public class NodeLoginPresenter extends BasePresenterImpl<NodeLoginContact.view> implements NodeLoginContact.presenter {
//    private static final int SEND_SUCCESS = 0, SEND_FAIL = 1, VERIFY_SUCCESS = 2, VERIFY_FAIL = 3;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case SEND_SUCCESS:
//                    Log.i("mob", "发送成功");
//                    view.toast("已发送，请等待。。。");
//                    break;
//                case SEND_FAIL:
//                    Log.i("mob", "发送失败");
//                    view.toast("异常，过会请重试。。。");
//                    break;
//                case VERIFY_SUCCESS:
//                    Log.i("mob", "验证通过");
//                    view.toast("验证成功");
//                    view.main();
//                    break;
//                case VERIFY_FAIL:
//                    Log.i("mob", "验证失败");
//                    view.toast("验证失败，请重试。。。");
//                    break;
//            }
//        }
//    };
//
//    public NodeLoginPresenter(NodeLoginContact.view view) {
//        super(view);
//    }
//
//    @Override
//    public void init() {
//        view.init();
//    }
//
//    @Override
//    public void time(final int time) {
//        Observable<Integer> observable = Observable.interval(0, 1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<Long, Integer>() {
//                    @Override
//                    public Integer apply(Long aLong) throws Exception {
//                        return time - aLong.intValue();
//                    }
//                })
//                .take(time + 1)
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        Log.e("开始:   ", "计时");
//                        addDisposable(disposable);
//                    }
//                });
//        observable.subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        view.setTimeText(integer.toString());
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        ExceptionHelper.handleException(throwable);
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        view.setTimeText("接收");
//                    }
//                });
//    }
//
//    private String phone = "";
//
//    @Override
//    public void smsRequest(NodeLoginActivity nodeLoginActivity, String s) {
//        phone = s;
//        SMSSDK.registerEventHandler(new EventHandler() {
//            @Override
//            public void afterEvent(int i, int i1, Object o) {
//                if (i1 == SMSSDK.RESULT_COMPLETE) {
//                    handler.sendEmptyMessage(SEND_SUCCESS);
//                } else {
//                    handler.sendEmptyMessage(SEND_FAIL);
//                }
//            }
//        });
//        SMSSDK.getVerificationCode("86", s);
////        phone = s;
////        BombManager.SMSRequest(nodeLoginActivity, s, new RequestSMSCodeListener() {
////            @Override
////            public void done(Integer integer, BmobException e) {
////                if (e == null) {
////                    view.toast("已发送，请等待。。。");
////                } else {
////                    view.toast("异常，过会请重试。。。");
////                }
////            }
////        });
//    }
//
//    @Override
//    public void verifySmsCode(NodeLoginActivity nodeLoginActivity, String s) {
//        if (s == null || s.equals("")) {
//            view.toast("验证码为空");
//            return;
//        }
//        // 注册一个事件回调，用于处理提交验证码操作的结果
//        SMSSDK.registerEventHandler(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // TODO 处理验证成功的结果
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                        //提交验证码成功
//                        handler.sendEmptyMessage(VERIFY_SUCCESS);
//                        Log.i("EventHandler", "提交验证码成功");
//                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        //获取验证码成功
//                        Log.i("EventHandler", "获取验证码成功");
//                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
//                        //返回支持发送验证码的国家列表
//                        Log.i("EventHandler", "返回支持发送验证码的国家列表");
//                    }
//                } else {
//                    // TODO 处理错误的结果
//                    handler.sendEmptyMessage(VERIFY_FAIL);
//                }
//            }
//        });
//// 触发操作
//        SMSSDK.submitVerificationCode("86", phone, s);
////        BombManager.verifySmsCode(nodeLoginActivity, phone, s, new VerifySMSCodeListener() {
////            @Override
////            public void done(BmobException ex) {
////                if (ex == null) {//成功
////                    Log.i("bmob", "验证通过");
////                    view.toast("验证成功");
////                } else {
////                    Log.i("bmob", "验证失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
////                    view.toast("验证失败，请重试。。。");
////                }
////            }
////        });
//    }
//}
