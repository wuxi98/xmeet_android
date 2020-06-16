package cn.nchu.wuxi.xlivemeet;

import android.app.Application;
import android.content.Context;


import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.SortedList;

import com.facebook.stetho.Stetho;
import com.mob.MobSDK;
import com.xuexiang.xui.XUI;
import com.xuexiang.xutil.XUtil;

import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;
import cn.nchu.wuxi.xlivemeet.utils.CrashHandler;
import cn.nchu.wuxi.xlivemeet.utils.OwnUncaughtExceptionHandler;

public class XMeetApp extends Application {
    private static XMeetApp app;
    private SortedList<MyMessage> messageList;
    private Author callUser;

    public Author getCallUser() {
        return callUser;
    }

    public void setCallUser(Author callUser) {
        this.callUser = callUser;
    }

    public SortedList<MyMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(SortedList<MyMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        XUI.init(this); //初始化UI框架
        XUtil.init(this);
        XUI.debug(true);  //开启UI框架调试日志

        MobSDK.init(this);
        MobSDK.submitPolicyGrantResult(true, null);
        //打印崩溃日志
       // CrashHandler.getInstance().init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(new OwnUncaughtExceptionHandler());

        //FaceBook调试器,可在Chrome调试网络请求,查看SharePreferences,数据库等
        Stetho.initializeWithDefaults(this);
    }



    public static XMeetApp getInstance() {
        return app;
    }
}
