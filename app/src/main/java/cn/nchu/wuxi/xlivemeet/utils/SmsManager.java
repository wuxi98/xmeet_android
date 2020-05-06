//package cn.nchu.wuxi.xlivemeet.utils;
//
//import android.os.Message;
//import android.util.Log;
//
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;
//
//public class SmsManager {
//
//    void init(){
//        EventHandler eh=new EventHandler(){
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                mHandler.sendMessage(msg);
//
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // TODO 处理验证成功的结果
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                        //提交验证码成功
//                        Log.i("EventHandler", "提交验证码成功");
//                    } else if
//                    (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) { //获取验证码成功
//                        Log.i("EventHandler", "获取验证码成功");
//                    } else if
//                    (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
//                        //返回支持发送验证码的国家列表
//                        Log.i("EventHandler", "返回支持发送验证码的国家列表");
//                    }
//                } else {
//                    // TODO 处理错误的结果
//
//                }
//
//            }
//        };
//
////注册一个事件回调监听，用于处理SMSSDK接口请求的结果
//        SMSSDK.registerEventHandler(eh);
//    }
//}
