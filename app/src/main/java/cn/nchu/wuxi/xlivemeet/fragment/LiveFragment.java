package cn.nchu.wuxi.xlivemeet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.activity.MainActivity;
import cn.nchu.wuxi.xlivemeet.activity.PlayerActivity;
import cn.nchu.wuxi.xlivemeet.adpter.LiveRoomAdapter;

import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LiveFragment extends BaseFragment implements OnRefreshListener{

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;
    SharedPreferences sp;
    @BindView(R.id.ll_stateful)
    StatefulLayout mStatefulLayout;

    private ClassicsHeader mClassicsHeader;
    private Drawable mDrawableProgress;
    private FragmentActivity context;
    private String rtmpUrl;
    private int enterpriseId;


    @Override
    protected void init() {
        initView();
        initListener();

    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView() {
        context = this.getActivity();
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        enterpriseId = sp.getInt("enterpriseId", 0);
//        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
//        int delta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
//        mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis() - delta));
//       // mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
//        mDrawableProgress = ((ImageView) mClassicsHeader.findViewById(ClassicsHeader.ID_IMAGE_PROGRESS)).getDrawable();
//        if (mDrawableProgress instanceof LayerDrawable) {
//            mDrawableProgress = ((LayerDrawable) mDrawableProgress).getDrawable(0);
//        }

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
////设置布局管理器
//        recyclerView.setLayoutManager(layoutManager);
////设置为垂直布局，这也是默认的
//        layoutManager.setOrientation(OrientationHelper. VERTICAL);
////设置Adapter

       // LiveRoomAdapter adapterDome = new LiveRoomAdapter(list);

        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //RecyclerView.LayoutManager manager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager manager1 = new GridLayoutManager(context,2);
        //manager1.setOrientation(GridLayoutManager.VERTICAL);
        //StaggeredGridLayoutManager manager2 = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
      //  mRecyclerView.setAdapter(adapterDome);
        initData();


    }

    void initData(){
        LogUtil.e(LiveFragment.class,"发出请求");
        mStatefulLayout.showLoading();
        HttpUtil.getLiveRoom(enterpriseId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                LogUtil.e(LiveFragment.class,"请求出错");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String s = response.body().string();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()){
                                Gson gson = new Gson();
//                                JsonReturn<TLiveRome> res = gson.fromJson(s, JsonReturn<TLiveRome>.class);
                                JsonReturn<TLiveRome> res = gson.fromJson(s, new TypeToken<JsonReturn<TLiveRome>>(){}.getType());
                           //     System.out.println(res);
                              //  LogUtil.e(LiveFragment.class,res.toString());
                                if (res.getData().size() == 0) mStatefulLayout.showEmpty();
                                else mStatefulLayout.showContent();
                                LiveRoomAdapter liveRoomAdapter = new LiveRoomAdapter(context,res.getData());
                                liveRoomAdapter.setRecyclerItemClickListener(new LiveRoomAdapter.OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(TLiveRome tLiveRome) {
//                                        ToastUtil.normal(""+tLiveRome.getRomeId());
                                        Intent intent =new Intent(context, PlayerActivity.class);

//用Bundle携带数据
                                        Bundle bundle=new Bundle();
//传递name参数为tinyphp
                                        bundle.putString("ownerPhone",tLiveRome.getOwnerPhone());
                                        bundle.putInt("roomId", tLiveRome.getRomeId());
                                        bundle.putString("roomOwnerName", tLiveRome.getOwnerName());
                                        bundle.putString("roomInfo", tLiveRome.getContent());
                                        intent.putExtras(bundle);

                                        startActivity(intent);
                                    }
                                });
                                mRecyclerView.setAdapter(liveRoomAdapter);
                            }else {
                                LogUtil.e(LiveFragment.class,"请求不成功");
                                LogUtil.e(LiveFragment.class,response.message());
                            }
                                }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.live_fragment;
    }



    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
                refreshLayout.finishRefresh();
             //   refreshLayout.resetNoMoreData();//setNoMoreData(false);
            }
        }, 2000);
    }

}
