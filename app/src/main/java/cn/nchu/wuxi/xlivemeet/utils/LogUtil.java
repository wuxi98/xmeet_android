package cn.nchu.wuxi.xlivemeet.utils;

import android.util.Log;

/**
 * @auther WuXi
 * @create 2020/4/8
 */
public class LogUtil {
    static int curent = 4;
    private static final int DEBUG_LEVEL = 4;
    private static final int INFO_LEVEL = 3;
    private static final int WARRING_LEVEL = 2;
    private static final int ERROR_LEVEL = 1;

    public static void d(Class TAG,String msg){
        if(curent >= DEBUG_LEVEL)
            Log.d(TAG.getSimpleName(),msg);
    }
    public static void i(Class TAG,String msg){
        if(curent >= INFO_LEVEL)
            Log.d(TAG.getSimpleName(),msg);
    }
    public static void w(Class TAG,String msg){
        if(curent >= WARRING_LEVEL)
            Log.d(TAG.getSimpleName(),msg);
    }
    public static void e(Class TAG,String msg){
        if(curent >= ERROR_LEVEL)
            Log.d(TAG.getSimpleName(),msg);
    }
}
