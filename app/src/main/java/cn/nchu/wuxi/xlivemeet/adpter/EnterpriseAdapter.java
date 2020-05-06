package cn.nchu.wuxi.xlivemeet.adpter;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.layout.ExpandableLayout;

import java.util.Collection;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterprise;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;

public class EnterpriseAdapter extends BaseRecyclerAdapter<TEnterprise> {

    private RecyclerView mRecyclerView;
    private OnRecyclerItemClickListener monItemClickListener;

    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        monItemClickListener=listener;
    }

    public EnterpriseAdapter(RecyclerView recyclerView, Collection<TEnterprise> data) {
        super(data);
        mRecyclerView = recyclerView;
    }

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.enterprise_info_item;
    }


    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, TEnterprise item) {
        holder.text(R.id.tv_enterprise_name,item.getEnterpriseName());
        View view = holder.findViewById(R.id.btn_apple_join);
        holder.click(R.id.btn_apple_join, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monItemClickListener.onItemClick(view,item);
            }
        });

    }
    public interface OnRecyclerItemClickListener {
        //RecyclerView的点击事件，将信息回调给view
        void onItemClick(View view, TEnterprise item);
    }
}