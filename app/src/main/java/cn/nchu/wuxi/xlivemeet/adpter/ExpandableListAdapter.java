package cn.nchu.wuxi.xlivemeet.adpter;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.layout.ExpandableLayout;

import java.util.Collection;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;

public class ExpandableListAdapter extends BaseRecyclerAdapter<TCustomer> {

    private RecyclerView mRecyclerView;

    public ExpandableListAdapter(RecyclerView recyclerView, Collection<TCustomer> data) {
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
        return R.layout.adapter_expandable_list_item;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position 索引
     * @param item     列表项
     */

    private void onClickItem(View view, final ExpandableLayout expandableLayout, final int position) {
        RecyclerViewHolder holder = (RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectPosition);
        if (holder != null) {
            holder.select(R.id.fl_title, false);
            ((ExpandableLayout) holder.findViewById(R.id.expandable_layout)).collapse();
        }

        if (position == mSelectPosition) {
            mSelectPosition = -1;
        } else {
            view.setSelected(true);
            expandableLayout.expand();
            mSelectPosition = position;
        }
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, TCustomer item) {
        ExpandableLayout expandableLayout = holder.findViewById(R.id.expandable_layout);
//        AppCompatImageView ivIndicator = holder.findViewById(R.id.iv_indicator);
        expandableLayout.setInterpolator(new OvershootInterpolator());
        expandableLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if (mRecyclerView != null && state == ExpandableLayout.State.EXPANDING) {
                    mRecyclerView.smoothScrollToPosition(position);
                }
//                if (ivIndicator != null) {
//                    ivIndicator.setRotation(expansion * 90);
//                }
            }
        });

        boolean isSelected = position == mSelectPosition;
        expandableLayout.setExpanded(isSelected, false);

        holder.select(R.id.fl_title, isSelected);
        holder.text(R.id.tv_title, item.getNickName());
        holder.text(R.id.tv_post, item.getPost());
        holder.text(R.id.tv_content, item.getDeptName());
        holder.click(R.id.fl_title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(v, expandableLayout, position);
            }
        });
    }
}