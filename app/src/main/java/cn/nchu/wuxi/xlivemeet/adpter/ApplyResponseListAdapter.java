package cn.nchu.wuxi.xlivemeet.adpter;

import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.layout.ExpandableLayout;

import java.io.IOException;
import java.util.Collection;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TApplyResponse;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterpriseJoinApply;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ApplyResponseListAdapter extends BaseRecyclerAdapter<TEnterpriseJoinApply> {

    private RecyclerView mRecyclerView;

    public ApplyResponseListAdapter(RecyclerView recyclerView, Collection<TEnterpriseJoinApply> data) {
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
        return R.layout.apply_response_list_item;
    }

    /**
     * 绑定数据
     *
     * @param view
     * @param position 索引
     * @param item     列表项
     */

    private void onAccept(View view, final int position,TEnterpriseJoinApply item) {
        RecyclerViewHolder holder = (RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectPosition);
        if (holder != null) {
            holder.text(R.id.btn_res,"已同意");
            Button accept = (Button) holder.findViewById(R.id.btn_accept);
            accept.setVisibility(View.GONE);
            Button reject = (Button) holder.findViewById(R.id.btn_reject);
            reject.setVisibility(View.GONE);
            Button res = (Button) holder.findViewById(R.id.btn_res);
            res.setVisibility(View.VISIBLE);
            HttpUtil.processApply(item.getEnterpriseId(), item.getPhone(), 1, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }



                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }
        if (position == mSelectPosition) {
            mSelectPosition = -1;
        } else {
            view.setSelected(true);
           // expandableLayout.expand();
            mSelectPosition = position;
        }
    }

    private void onReject(View view, final int position,TEnterpriseJoinApply item) {
        RecyclerViewHolder holder = (RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectPosition);
        if (holder != null) {
            holder.text(R.id.btn_res, "已同意");
            Button accept = (Button) holder.findViewById(R.id.btn_accept);
            accept.setVisibility(View.GONE);
            Button reject = (Button) holder.findViewById(R.id.btn_reject);
            reject.setVisibility(View.GONE);
            Button res = (Button) holder.findViewById(R.id.btn_res);
            res.setVisibility(View.VISIBLE);
            HttpUtil.processApply(item.getEnterpriseId(), item.getPhone(), 1, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });

        }
            if (position == mSelectPosition) {
                mSelectPosition = -1;
            } else {
                view.setSelected(true);
                // expandableLayout.expand();
                mSelectPosition = position;
            }

    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, TEnterpriseJoinApply item) {

        boolean isSelected = position == mSelectPosition;
     //   expandableLayout.setExpanded(isSelected, false);

        holder.text(R.id.apply_user,String.format("%s(%s)",item.getApplyName(),item.getPhone()));
        if(item.getState() == 0){
            holder.click(R.id.btn_accept, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAccept(v, position, item);
                }
            });
            holder.click(R.id.btn_reject, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReject(v, position, item);
                }
            });
        }else if(item.getState() == 1){
            holder.text(R.id.btn_res, "已同意");
            Button accept = (Button) holder.findViewById(R.id.btn_accept);
            accept.setVisibility(View.GONE);
            Button reject = (Button) holder.findViewById(R.id.btn_reject);
            reject.setVisibility(View.GONE);
            Button res = (Button) holder.findViewById(R.id.btn_res);
            res.setVisibility(View.VISIBLE);
        }else if(item.getState() == 2){
            holder.text(R.id.btn_res, "已拒绝");
            Button accept = (Button) holder.findViewById(R.id.btn_accept);
            accept.setVisibility(View.GONE);
            Button reject = (Button) holder.findViewById(R.id.btn_reject);
            reject.setVisibility(View.GONE);
            Button res = (Button) holder.findViewById(R.id.btn_res);
            res.setVisibility(View.VISIBLE);
        }

    }
}