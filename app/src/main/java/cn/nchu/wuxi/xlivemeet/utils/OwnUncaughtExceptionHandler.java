package cn.nchu.wuxi.xlivemeet.utils;

import android.util.Log;

public class OwnUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StackTraceElement[] elements = ex.getStackTrace();
        StringBuilder reason =new StringBuilder(ex.toString());
        if (elements !=null && elements.length >0) {
            for (StackTraceElement element : elements) {
                reason.append("\n");
                reason.append(element.toString());
            }
        }
        Log.e("zyq", reason.toString());
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}




