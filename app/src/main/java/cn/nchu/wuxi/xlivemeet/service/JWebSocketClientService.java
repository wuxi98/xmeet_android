package cn.nchu.wuxi.xlivemeet.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.net.URI;

import cn.nchu.wuxi.xlivemeet.core.chat.ChatSocketListener;

public class JWebSocketClientService extends Service {
    private URI uri;
    public ChatSocketListener client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    //用于Activity和service通讯
    class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
    }
}
