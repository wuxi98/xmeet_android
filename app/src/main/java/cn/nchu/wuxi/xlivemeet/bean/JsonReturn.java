package cn.nchu.wuxi.xlivemeet.bean;

import java.util.List;

/**
 * @auther WuXi
 * @create 2020/4/8
 */
public class JsonReturn<T> {
    /**
     * success : true
     * code : 200
     * data : []
     * message :
     * length : 12
     * currentTime : 1586817170744
     */

    private boolean success;
    private int code;
    private String message;
    private int length;
    private long currentTime;
    private List<T> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonReturn{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", length=" + length +
                ", currentTime=" + currentTime +
                ", data=" + data +
                '}';
    }
    /**
     * success : true
     * code : 200
     * data : {"code":0,"msg":"账号或密码错误","data":[],"dataSize":0}
     * message : 登录失败
     * length : null
     * currentTime : 1586301909256
     */


}
