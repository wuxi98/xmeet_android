package cn.nchu.wuxi.xlivemeet.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.config.PictureConfig;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xutil.tip.ToastUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.EnterpriseAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.ExpandableListAdapter;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TEnterprise;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TLiveRome;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.nchu.wuxi.xlivemeet.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EnterpriseManageActivity extends BaseActivity {

    @BindView(R.id.staff_recycler_view)
    RecyclerView staffRecyclerView;
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    private SharedPreferences sp;
    private String phone;
    private String userName;
    private int enterpriseId;
    private EnterpriseManageActivity context;

    @Override
    public void init(Bundle savedInstanceState) {
        context = this;
         sp = getSharedPreferences("user", MODE_PRIVATE);
         enterpriseId = sp.getInt("enterpriseId",0);
         phone = sp.getString("phone","0");
         userName = sp.getString("nickName","未知昵称");
        initView();
        initListener();
    }

    private void initListener() {
        mTitleBar.setLeftClickListener(view -> finish()).addAction(new TitleBar.ImageAction(R.drawable.ic_add_white_24dp) {
            @Override
            public void performAction(View view) {
                new MaterialDialog.Builder(context)
                        .title(R.string.tip_options)
                        .items(R.array.menu_manage_enterprise)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                //创建企业
                                if(position == 0){
                                    if (enterpriseId != 0){
                                        ToastUtil.normal("您已经是企业用户了");
                                        return;
                                    }
                                    DialogLoader.getInstance().showInputDialog(
                                            context,
                                            R.drawable.icon_tip,
                                            getString(R.string.tip_info),
                                            "输入企业名称",
                                            new InputInfo(InputType.TYPE_CLASS_TEXT,
                                                    "输入企业名称"),
                                            null,
                                            getString(R.string.ensure),
                                            (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                if (dialogInterface instanceof MaterialDialog) {
                                                    String s = ((MaterialDialog) dialogInterface).getInputEditText()
                                                            .getText().toString();
                                                  //  ToastUtil.normal("你输入了:" + s);
                                                    HttpUtil.createEnterprise(s, phone, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            LogUtil.i(EnterpriseManageActivity.class,"创建企业onFailure");
                                                            ToastUtil.normal("创建企业失败");
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            final String s = response.body().string();
                                                            runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                JsonReturn<TEnterprise> res = new Gson().fromJson(s, new TypeToken<JsonReturn<TEnterprise>>(){}.getType());
                                                                if(res.isSuccess()){
                                                                    ToastUtil.normal("创建成功!");
                                                                    SharedPreferences.Editor editor = sp.edit();
                                                                    editor.putInt("enterpriseId",res.getData().get(0).getEnterpriseId());
                                                                    editor.putString("enterpriseName",res.getData().get(0).getEnterpriseName());
                                                                    editor.apply();
                                                                }else {
                                                                    ToastUtil.normal("创建失败!");
                                                                }
                                                            }
                                                        });

                                                        }
                                                    });
                                                }
                                            },
                                            getString(R.string.lab_cancel),
                                            null);
                                }
                                //加入企业
                                else if(position == 1){
                                    //ToastUtil.normal("加载所有企业列表");
                                    View view = View.inflate(getApplicationContext(), R.layout.enterprise_list,null);
                                    RecyclerView enterpriseRecyclerView = view.findViewById(R.id.enterprise_recycler_view);
                                    WidgetUtils.initRecyclerView(enterpriseRecyclerView);

                                    HttpUtil.getEnterprises(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            final String s = response.body().string();
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    JsonReturn<TEnterprise> res = new Gson().fromJson(s,new TypeToken<JsonReturn<TEnterprise>>(){}.getType());
                                                    if (res.isSuccess()){
                                                        EnterpriseAdapter enterpriseAdapter = new EnterpriseAdapter(enterpriseRecyclerView, res.getData());
                                                        enterpriseAdapter.setRecyclerItemClickListener(new EnterpriseAdapter.OnRecyclerItemClickListener() {
                                                            @Override
                                                            public void onItemClick(View view,TEnterprise item) {
                                                                if(enterpriseId != 0){
                                                                    ToastUtil.normal("您已经是企业用户了");
                                                                    return;
                                                                }
//                                                                ToastUtil.debug("" +
//                                                                        "" +
//                                                                        "" +
//                                                                        "申请"+item.getEnterpriseName());
                                                                view = (Button)view;

                                                                ((Button) view).setText("已申请");
                                                                ((Button) view).setTextColor(getResources().getColor(R.color.gray));
                                                                ((Button) view).setEnabled(false);
                                                                HttpUtil.applyJoinEnterprise(item.getEnterpriseId(),
                                                                        phone, userName, new Callback() {
                                                                            @Override
                                                                            public void onFailure(Call call, IOException e) {

                                                                            }

                                                                            @Override
                                                                            public void onResponse(Call call, Response response) throws IOException {

                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        });

                                                        new MaterialDialog.Builder(context)
                                                               // .customView(R.layout.enterprise_list, true)
                                                                .title("企业列表")
                                                                .adapter(enterpriseAdapter,null)
                                                                .show();
                                                    }
                                                    else {
                                                        ToastUtil.normal("请求数据失败");
                                                        LogUtil.d(EnterpriseManageActivity.class,"请求企业数据失败");
                                                    }
                                                }
                                            });
                                        }
                                    });


                                    //loadEnterpriseList();
                                }
                            }
                        })
                        .show();
            }
        });
    }
    void loadEnterpriseList(){

    }

    private void initView() {
        WidgetUtils.initRecyclerView(staffRecyclerView);
        HttpUtil.getCustomerListByEnterpriseId(enterpriseId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsonReturn<TCustomer> res = new Gson().fromJson(s, new TypeToken<JsonReturn<TCustomer>>(){}.getType());
                        if(res.isSuccess()){
                            List<TCustomer> data = res.getData();
                            staffRecyclerView.setAdapter(new ExpandableListAdapter(staffRecyclerView, data));
                        }
                        else {

                            LogUtil.i(EnterpriseManageActivity.class,"请求成员信息失败");
                        }
                    }
                });

            }
        });
    }


    @Override
    public int getContentViewResId() {
        return R.layout.enterprise_manage_fragment;
    }


}
