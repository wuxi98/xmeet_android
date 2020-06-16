package cn.nchu.wuxi.xlivemeet.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.List;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;

public class LiveRoomAdapter extends RecyclerView.Adapter<LiveRoomAdapter.RoomViewHolder>{

    private Context context;
    private List<TLiveRome> list;
    private View inflater;
    private OnRecyclerItemClickListener monItemClickListener;


    public LiveRoomAdapter(Context context, List<TLiveRome > data) {
        this.list = data;
    }


    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        monItemClickListener=listener;
    }




    @NonNull
    @Override
    public LiveRoomAdapter.RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
//        inflater = LayoutInflater.from(context).inflate(R.layout.item_live_room,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_room, parent, false);
        RoomViewHolder myViewHolder = new RoomViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull LiveRoomAdapter.RoomViewHolder holder, int position) {
        //将数据和控件绑定
        holder.room_name.setText(list.get(position).getRomeName());
        holder.user_name.setText(list.get(position).getOwnerName());
        Glide.with(XUI.getContext())
                .load(list.get(position).getPicUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder{
        TextView room_name;
        TextView user_name;
        RadiusImageView imageView;
        public RoomViewHolder(View itemView) {
            super(itemView);
            room_name = (TextView) itemView.findViewById(R.id.adapter_text_room_name);
            user_name = (TextView) itemView.findViewById(R.id.adapter_text_user);
            imageView = (RadiusImageView) itemView.findViewById(R.id.adapter_image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (monItemClickListener!=null){

                        LogUtil.e(LiveRoomAdapter.class,"position -->"+getAdapterPosition());
                        monItemClickListener.onItemClick(list.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    public interface OnRecyclerItemClickListener {
        //RecyclerView的点击事件，将信息回调给view
        void onItemClick(TLiveRome tLiveRome);
    }
}


