package cn.nchu.wuxi.xlivemeet.adpter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.badge.Badge;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterprise;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MyMessageHolder> {

    private RecyclerView mRecyclerView;
    private Context context;
    SortedList<MyMessage> list;
    private OnRecyclerItemClickListener monItemClickListener;

    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        monItemClickListener=listener;
    }

    public void setList(SortedList<MyMessage> list) {
        this.list = list;
    }

    public MessageAdapter(SortedList<MyMessage> data, Context context) {
        list = data;
      //  this.mRecyclerView = mRecyclerView;
        this.context = context;
    }


    @NonNull
    @Override
    public MyMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_list_item, parent, false);
        MyMessageHolder myViewHolder = new MyMessageHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyMessageHolder holder, int position) {
        MyMessage item = list.get(position);
        RoundedCorners roundedCorners= new RoundedCorners(20);
        //通过RequestOptions扩展功能
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(170, 170);
        Glide.with(context)
                .load(item.getUser().getHeadUrl())
                .fallback(R.drawable.head_pic)
                .placeholder(R.drawable.head_pic)
                .error(R.drawable.head_pic)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.stv.setLeftIcon(resource);
                    }
                });

       // if(item.getUser().getHeadUrl() == null)  holder.stv.setLeftIcon(R.drawable.head_pic);
        holder.stv.setLeftTopString(item.getUser().getNickName());
        holder.stv.setLeftBottomString(item.getCurrentMsg());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        holder.stv.setRightBottomString(format.format(item.getCurrentTime()));
        holder.badge.setBadgeNumber(item.getUnreadMsg());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyMessageHolder extends RecyclerView.ViewHolder {
        SuperTextView stv;
        Badge badge;

        public MyMessageHolder(View itemView) {
            super(itemView);
            stv = itemView.findViewById(R.id.msg_item);
            badge = new BadgeView(context).bindTarget(stv);
            stv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monItemClickListener.onItemClick(getAdapterPosition(),list.get(getAdapterPosition()));
                }
            });
        }

    }
    public interface OnRecyclerItemClickListener {
        //RecyclerView的点击事件，将信息回调给view
        void onItemClick(int position, MyMessage item);
    }
}
