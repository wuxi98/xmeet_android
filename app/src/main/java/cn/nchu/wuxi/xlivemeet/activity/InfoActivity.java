package cn.nchu.wuxi.xlivemeet.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.ApplyResponseListAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.ExpandableListAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TApplyResponse;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterpriseJoinApply;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InfoActivity extends BaseActivity {

    @BindView(R.id.info_recycler_view)
    RecyclerView info_recycler_view;
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;

    private SharedPreferences sp;
    private String phone;
    private String userName;
    private int enterpriseId;
    private InfoActivity context;
    private int manageEnterpriseId;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_notice_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        context = this;
        sp = getSharedPreferences("user", MODE_PRIVATE);
        enterpriseId = sp.getInt("enterpriseId",0);
        phone = sp.getString("phone","0");
        userName = sp.getString("nickName","未知昵称");
        manageEnterpriseId = sp.getInt("manageEnterpriseId",0);
        initView();
    }

    private void initView() {
        mTitleBar.setLeftClickListener(view -> finish());
        WidgetUtils.initRecyclerView(info_recycler_view);
        if(manageEnterpriseId == 0) return;
        HttpUtil.getNoticeInfo(phone, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<TEnterpriseJoinApply> res = new Gson().fromJson(s, new TypeToken<JsonReturn<TEnterpriseJoinApply>>(){}.getType());
                        if(res.isSuccess()){
                            List<TEnterpriseJoinApply> data = res.getData();
                            info_recycler_view.setAdapter(new ApplyResponseListAdapter(info_recycler_view, data));
                        }
                        else {
                            LogUtil.i(EnterpriseManageActivity.class,"请求成员信息失败");
                        }
                    }
                });

            }
        });
    }
}
