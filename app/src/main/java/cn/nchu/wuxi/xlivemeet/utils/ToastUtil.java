package cn.nchu.wuxi.xlivemeet.utils;

import android.os.Looper;

import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.toast.XToast;

/**
 * @auther WuXi
 * @create 2020/4/8
 */
public class ToastUtil {

    /**
     * 在任意线程当中都可以调用弹出吐司的方法
     * @param result
     */
    public static void showToastInAnyThread(final String result) {
        Looper.prepare();
        XToast.normal(XUI.getContext(),result).show();
        Looper.loop();
    }
    /**
     * 普通调用
     * @param result
     */
    public static void normal(final String result) {
        XToast.normal(XUI.getContext(),result).show();
    }

    public static void debug(final String result) {
        XToast.normal(XUI.getContext(),result).show();
    }
}
