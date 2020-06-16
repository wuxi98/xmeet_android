package cn.nchu.wuxi.xlivemeet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xormlite.db.DBService;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.SearchRecordTagAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.entity.SearchRecord;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.fragment.MessageFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.nchu.wuxi.xlivemeet.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchViewActivity extends BaseActivity implements RecyclerViewHolder.OnItemClickListener<SearchRecord>{

    @BindView(R.id.search_titleBar)
    TitleBar titleBar;
    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;
    private SearchViewActivity context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private DBService<SearchRecord> mDBService;
    private SearchRecordTagAdapter mAdapter;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_search_view;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initViews();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(this);
    }

    private void initViews() {
        context = this;
       // setSupportActionBar();
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).addAction(new TitleBar.ImageAction(R.drawable.icon_action_query) {

            @Override
            @SingleClick
            public void performAction(View view) {
                mSearchView.showSearch();
            }
        });
        mSearchView.setVoiceSearch(false);
//        mSearchView.setCursorDrawable(R.drawable.custom_cursor);//自定义光标颜色
        mSearchView.setEllipsize(true);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // SnackbarUtils.Long(mSearchView, "Query: " + query).show();
                searchUser(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        mSearchView.setSubmitOnClick(true);

        mAdapter = new SearchRecordTagAdapter();

        recyclerView.setLayoutManager(Utils.getFlexboxLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    void searchUser(String targetPhone){
        HttpUtil.searchUser(targetPhone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(MessageFragment.class,"search user onFailure!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<TCustomer> res = new Gson().fromJson(s,new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                        if(res.isSuccess()){
                            if(res.getData().size() == 0){
                                ToastUtil.normal("未找到该用户");
                            }else {
                                Intent intent = new Intent(context, UserInfoActivity.class);
                                TCustomer user = res.getData().get(0);
                                intent.putExtra("phone",user.getPhone());
                                intent.putExtra("userName",user.getNickName());
                                intent.putExtra("enterpriseName",user.getEnterprisename());
                                intent.putExtra("headUrl",user.getHeadUrl());
                                startActivity(intent);
                            }
                        }else
                        {
                            ToastUtil.normal("服务器返回数据异常");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View itemView, SearchRecord item, int position) {
        ToastUtil.normal(item.getContent());
    }
}
