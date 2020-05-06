package cn.nchu.wuxi.xlivemeet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.utils.JsonFormUtils;
import cn.nchu.wuxi.xlivemeet.utils.OKHttpSingleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void jsonFormTest(){
        String s = "[\n" +
                "            {\n" +
                "                \"romeId\": 1,\n" +
                "                \"romeName\": \"wxdzdj\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 3,\n" +
                "                \"romeName\": \"吴曦的直播间2\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 4,\n" +
                "                \"romeName\": \"吴曦的直播间3\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 5,\n" +
                "                \"romeName\": \"吴曦的直播间4\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 6,\n" +
                "                \"romeName\": \"吴曦的直播间5\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 7,\n" +
                "                \"romeName\": \"吴曦的直播间6\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 8,\n" +
                "                \"romeName\": \"吴曦的直播间7\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 9,\n" +
                "                \"romeName\": \"吴曦的直播间8\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 10,\n" +
                "                \"romeName\": \"吴曦的直播间9\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 11,\n" +
                "                \"romeName\": \"吴曦的直播间10\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 12,\n" +
                "                \"romeName\": \"吴曦的直播间11\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 13,\n" +
                "                \"romeName\": \"吴曦的直播间12\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            }\n" +
                "        ]";
        JsonFormUtils utils = new JsonFormUtils();
        List<TLiveRome> liveRomes = utils.parseString2List(s, TLiveRome.class);
        System.out.println(liveRomes);
    }

    @Test
    public void jsontest2(){
        String s = "{\n" +
                "    \"success\": true,\n" +
                "    \"code\": 200,\n" +
                "    \"data\": {\n" +
                "        \"code\": 1,\n" +
                "        \"msg\": \"\",\n" +
                "        \"data\": [\n" +
                "            {\n" +
                "                \"romeId\": 1,\n" +
                "                \"romeName\": \"wxdzdj\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 3,\n" +
                "                \"romeName\": \"吴曦的直播间2\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 4,\n" +
                "                \"romeName\": \"吴曦的直播间3\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 5,\n" +
                "                \"romeName\": \"吴曦的直播间4\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 6,\n" +
                "                \"romeName\": \"吴曦的直播间5\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 7,\n" +
                "                \"romeName\": \"吴曦的直播间6\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 8,\n" +
                "                \"romeName\": \"吴曦的直播间7\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 9,\n" +
                "                \"romeName\": \"吴曦的直播间8\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 10,\n" +
                "                \"romeName\": \"吴曦的直播间9\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 11,\n" +
                "                \"romeName\": \"吴曦的直播间10\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 12,\n" +
                "                \"romeName\": \"吴曦的直播间11\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            },\n" +
                "            {\n" +
                "                \"romeId\": 13,\n" +
                "                \"romeName\": \"吴曦的直播间12\",\n" +
                "                \"ownerPhone\": \"18270088305\",\n" +
                "                \"enterpriseId\": 1,\n" +
                "                \"content\": \"写代码\",\n" +
                "                \"label\": \"\",\n" +
                "                \"picUrl\": null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"dataSize\": 12\n" +
                "    },\n" +
                "    \"message\": \"\",\n" +
                "    \"length\": null,\n" +
                "    \"currentTime\": 1586811025389\n" +
                "}";
        Gson gson = new Gson();
        JsonReturn<TLiveRome> res = gson.fromJson(s, new TypeToken<JsonReturn<JsonReturn>>(){}.getType());
        System.out.println(res);
    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void okhttptest() throws InterruptedException {
        OkHttpClient client = OKHttpSingleton.getInstance();

//        Request request = new Request.Builder()
//                .url()
//                .build();
        //发送请求获取响应
        Request request = new Request.Builder()
                .url("http://localhost:8080/account/login/18270088305/123456")
                .build();

        client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                            if(response.isSuccessful()){
                                System.out.println(response.body().string());
                            }

                            else{
                                System.err.println(response.message());
                            }


                }
            });
        Thread.sleep(10000);


    }
    @Test
    public void oktest2(){
        OkHttpClient client = OKHttpSingleton.getInstance();

        Request request = new Request.Builder()
                .url("http://localhost:8080/account/login/18270088305/123456")
                .build();
        //发送请求获取响应
//        RequestBody body = new FormBody.Builder()
//                .build();
//        Request request = new Request.Builder()
//                .url()
//                .post(body)
//                .build();

        try {
            Response response=client.newCall(request).execute();
            //判断请求是否成功
            if(response.isSuccessful()){
                //打印服务端返回结果
//                Log.i("sdadsfa",response.body().string());
                System.out.println(response.body().string());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}