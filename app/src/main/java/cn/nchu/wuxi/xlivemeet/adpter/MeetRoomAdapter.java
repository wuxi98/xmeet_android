package cn.nchu.wuxi.xlivemeet.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.RoomInfo;
import cn.nchu.wuxi.xlivemeet.adpter.entity.SimpleRoomInfo;
import cn.nchu.wuxi.xlivemeet.fragment.MeetFragment;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;

public class MeetRoomAdapter extends ArrayAdapter<SimpleRoomInfo> {

    private int resourceId;

    // 适配器的构造函数，把要适配的数据传入这里
    public MeetRoomAdapter(Context context, int textViewResourceId, List<SimpleRoomInfo> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SimpleRoomInfo item = getItem(position);
        //LogUtil.d(MeetFragment.class,"adapter item->"+item.toString());
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder=new ViewHolder();
            viewHolder.roomName=view.findViewById(R.id.tv_room_name);
            viewHolder.users=view.findViewById(R.id.tv_users);

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.roomName.setText(String.format("房间号：%s",item.getRoomId()));
        viewHolder.users.setText(String.format("在线人数：%d",item.getUsers()));
        return view;
    }
    class ViewHolder{
        TextView roomName;
        TextView users;
    }
}
