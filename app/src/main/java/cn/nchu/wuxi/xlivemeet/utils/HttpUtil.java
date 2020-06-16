package cn.nchu.wuxi.xlivemeet.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocketListener;

public class HttpUtil {
    //web service
    public static final String HOST_URL = "http://39.106.64.220:8080";
//    public static final String HOST_URL = "http://192.168.43.39:8080";

    public static final String HOST_URL_TEMP = "http://39.106.64.220:5000";
    public static final String MULTI_SERVER = "http://39.106.64.220:3000";

    //弹幕服务器
    public static final String WEBSOCKET_URL = "ws://49.235.180.5:8899/ws";

    //登录
    public static void loginWithOkHttpSync(String phone, String password, Callback callback) throws IOException {
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody body = new FormBody.Builder()
                .add("phone",phone)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(HOST_URL+"/account/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //验证码登录
    public static void loginWithoutPassword(String phone, Callback callback) throws IOException {
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url(HOST_URL+"/account/loginWithoutPassword/"+phone)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
    //注册
    public static void registerWithOkHttp(String account,String password,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody body = new FormBody.Builder()
                .add("phone",account)
                .add("password",password)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/account/register")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    //获取直播间
    public static void getLiveRoom(int enterpriseId,Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url(HOST_URL+"/liveRoom/"+enterpriseId)
                .get()
                .build();
        client.newCall(request).enqueue(callback);

    }
    //注册直播间
    public static Response registerLiveRoom(String phone) throws IOException {
        OkHttpClient client = OKHttpSingleton.getInstance();

        RequestBody body = new FormBody.Builder()
                .add("phone",phone)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/liveRoom/register")
                .post(body)
                .build();
        return client.newCall(request).execute();
    }
    //连接弹幕服务器
    public static void connectDanmu(WebSocketListener webSocketListener){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request= new Request.Builder()
                .url(WEBSOCKET_URL)
                .build();
        client.newWebSocket(request, webSocketListener);
    }
    //主播请求房间信息
    public static void getRoomInfo(int roomId,Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/liveRoom/getInfoById/"+roomId)
                .get()
                .build();
         client.newCall(request).enqueue(callback);
    }
    //改变直播间状态
    public static void changeRoomState(int roomId,String status,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody body = new FormBody.Builder()
                .add("roomId",""+roomId)
                .add("status",status)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/liveRoom/changeStatus")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    //请求企业用户
    public static void getCustomerListByEnterpriseId(int enterpriseId,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/enterpriseId/"+enterpriseId)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
    //请求会议房间
    public static void getMeetList(okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url(MULTI_SERVER+"/rooms")
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //请求企业信息
    public static void getEnterpriseById(int enterpriseId, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/enterprise/"+enterpriseId)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //上传头像
    public static void uploadHead(String path, String fileName, String phone, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(path)))
                .addFormDataPart("phone", phone)
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(HOST_URL+"/customer/uploadHeadPic")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    //上传直播封面
    public static void uploadPage(String path, String fileName, int roomId, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(path)))
                .addFormDataPart("roomId", ""+roomId)
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(HOST_URL+"/liveRoom/uploadPage")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //修改用户姓名
    public static void updateUserName(String name, String phone, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/updateName/"+phone+"/"+name)
                .post(new FormBody.Builder().build())
                .build();
        client.newCall(request).enqueue(callback);
    }

    //获取头像
    public static void getHeadUrl(String phone, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/headUrl/"+phone)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //创建企业
    public static void createEnterprise(String enterpriseName, String phone, okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody requestBody = new FormBody.Builder()
                .add("enterpriseName", enterpriseName)
                .add("phone", phone)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/createEnterprise")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //获取企业
    public static void getEnterprises(okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/enterprises")
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //申请加入企业
    public static void applyJoinEnterprise(int enterpriseId,String phone,String userName,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("enterpriseId", ""+enterpriseId)
                .addFormDataPart("phone", phone)
                .addFormDataPart("userName", userName)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/joinEnterprise")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //查找用户
    public static void searchUser(String targetPhone,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/targetPhone/" +targetPhone)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //加载离线消息
    public static void loadChatRecord(String phone,okhttp3.Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/chatRecord/loadChatRecord/" +phone)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //删除离线消息
    public static void deleteChatRecord(String phone){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/chatRecord/deleteChatRecord/" +phone)
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    //获取通知消息
    public static void getNoticeInfo(String phone,Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/notices/" +phone)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //处理请求申请
    public static void processApply(int enterpriseId, String phone, int code, Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url(HOST_URL+"/customer/processEnterpriseApply/" +enterpriseId+"/"+phone+"/"+code)
                .post(new FormBody.Builder().build())
                .build();
        client.newCall(request).enqueue(callback);
    }

    //修改直播公告
    public static void updateLiveNotice(int roomId, String msg, Callback callback){
        OkHttpClient client = OKHttpSingleton.getInstance();
        FormBody body = new FormBody.Builder()
                .add("roomId", "" + roomId)
                .add("msg", msg)
                .build();
        Request request = new Request.Builder()
                .url(HOST_URL+"/liveRoom/updateNotice")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }






}