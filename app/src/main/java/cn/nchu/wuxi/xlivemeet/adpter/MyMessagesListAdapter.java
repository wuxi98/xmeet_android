package cn.nchu.wuxi.xlivemeet.adpter;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.List;

public class MyMessagesListAdapter<Message> extends MessagesListAdapter {
    public MyMessagesListAdapter(List<Message> list,String senderId, ImageLoader imageLoader) {
        super(senderId, imageLoader);
        items = list;
    }
}
