package cn.nchu.wuxi.xlivemeet.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.SortedList;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dds.skywebrtc.CallSession;
import com.dds.skywebrtc.EnumType;
import com.dds.skywebrtc.SkyEngineKit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.badge.Badge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.fussen.cache.Cache;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.XMeetApp;
import cn.nchu.wuxi.xlivemeet.adpter.entity.Author;
import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;
import cn.nchu.wuxi.xlivemeet.adpter.entity.TCustomer;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.Message;
import cn.nchu.wuxi.xlivemeet.adpter.entity.chatkit.RealMessage;
import cn.nchu.wuxi.xlivemeet.bean.JsonReturn;
import cn.nchu.wuxi.xlivemeet.core.BaseFragment;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatSocketListener;
import cn.nchu.wuxi.xlivemeet.core.chat.ChatWsManager;
import cn.nchu.wuxi.xlivemeet.core.chat.InitSocketThread;
import cn.nchu.wuxi.xlivemeet.fragment.LiveFragment;
import cn.nchu.wuxi.xlivemeet.fragment.MeetFragment;
import cn.nchu.wuxi.xlivemeet.fragment.MessageFragment;
import cn.nchu.wuxi.xlivemeet.fragment.ProfileFragment;
import cn.nchu.wuxi.xlivemeet.utils.HttpUtil;
import cn.nchu.wuxi.xlivemeet.utils.LogUtil;
import cn.nchu.wuxi.xlivemeet.utils.ToastUtil;
import cn.nchu.wuxi.xlivemeet.webrtc.MeetContainer;
import cn.nchu.wuxi.xlivemeet.webrtc.socket.SocketManager;
import cn.nchu.wuxi.xlivemeet.webrtc.voip.VoipEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener,BottomNavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    //侧边栏
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    //@BindView(R.id.tv_avatar)
    TextView tv_avatar;
  //  @BindView(R.id.iv_avatar)
    RadiusImageView iv_avatar;

    //存储导航栏名称
    private String[] mTitles;
    private LiveFragment liveFragment;
    private MeetFragment meetFragment;
    private MessageFragment messageFragment;
    private ProfileFragment profileFragment;
    private FragmentManager sfm;
    private SharedPreferences sp;
    private String userName;
    private String head_url;
    private TextView tv_sign;
    private ChatWsManager wsManger;
    private String phone;
    public Handler handler = new Handler(Looper.getMainLooper());

    public int unReadNum;
    public Badge badge;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        String login = sp.getString("login",null);
//        if(login == null || "0".equals(login)){
//            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//            startActivityForResult(intent,1);
//        }
        userName = sp.getString("nickName","用户356416");
        phone = sp.getString("phone","0");
        head_url = sp.getString("head_url","");
        LogUtil.d(MainActivity.class,"user->{phone="+phone+",userName="+userName);

        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);

        //登陆聊天服务器
        ChatWsManager.init();
        wsManger = ChatWsManager.getInstance();
        //使用okhttp创建websocket连接netty
//        wsManger.connectChat();
//        wsManger.getListener().setPhone(phone);
       // new InitSocketThread(phone).start();

        //使用java-webSocket创建websocket连接netty
        wsManger.connect(phone);
        //登陆视频会议服务器
        SocketManager.getInstance().connect(
                MeetContainer.WEBSOCKET_URL,
                phone,
                MeetContainer.DEVICE);

        //初始化一对一视频服务器以及session状态
        SkyEngineKit.init(new VoipEvent());
        CallSession currentSession;
        if((currentSession = SkyEngineKit.Instance().getCurrentSession())!=null)
            currentSession.setCallState(EnumType.CallState.Idle);
        // initChatData();
        initViews();
        initHeader();
        initFragment();
        initListeners();

    }



    private void initHeader() {
        navView.setItemIconTintList(null);
        View headerView = navView.getHeaderView(0);
        LinearLayout navHeader = headerView.findViewById(R.id.nav_header);
        iv_avatar  = headerView.findViewById(R.id.iv_avatar);
        tv_avatar = headerView.findViewById(R.id.tv_avatar);
        tv_sign = headerView.findViewById(R.id.tv_sign);

        tv_avatar.setText(userName);
        Glide.with(this)
                .load(head_url)
                .fallback(R.drawable.head_pic)
                .placeholder(R.drawable.head_pic)
                .error(R.drawable.head_red)
                .into(iv_avatar);
        tv_sign.setText("这个家伙很懒，什么也没有留下～～");

        //navHeader.setOnClickListener(this);
    }

    private void initFragment() {
        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new LiveFragment(),
                new MeetFragment(),
                new MessageFragment(),
                new ProfileFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);


//        liveFragment = new LiveFragment();
//        meetFragment = new MeetFragment();
//        messageFragment = new MessageFragment();
//        profileFragment = new ProfileFragment();
//        sfm = getSupportFragmentManager();
//        switchFragment(liveFragment);

    }
    void switchFragment(BaseFragment fragment){
        FragmentTransaction ft = sfm.beginTransaction();
        ft.replace(R.id.view_pager,fragment);
        ft.commit();
    }

    void initViews(){

        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(mTitles[0]);
        toolbar.inflateMenu(R.menu.menu_main_add);
        toolbar.setOnMenuItemClickListener(this);
    }

    void initListeners(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.isCheckable()) {
                handleNavigationItemSelected(menuItem);
                drawerLayout.closeDrawers();
            } else {
                switch (menuItem.getItemId()) {
                    case R.id.nav_settings:
//                        openNewPage(SettingsFragment.class);
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        break;
                    case R.id.nav_logout:
//                        XToastUtils.toast("点击了:" + menuItem.getTitle());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("phone");
                        editor.remove("nickName");
                        editor.remove("enterpriseId");
                        editor.remove("enterpriseName");
                        editor.remove("live_rome_id");
                        editor.putString("login","0");
                        editor.apply();
                        ChatSocketListener socket;
                        if((socket = wsManger.getWebSocket()) != null)
                        socket.close();
//                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//                        startActivity(intent);
                        finish();
                        break;
                }
            }
            return true;
        });



        bottomNavigation.setOnNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(this);

    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
//        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        int index = -1;
        for(int i = 0,len = mTitles.length;i < len;i++){
            if(mTitles[i].equals(menuItem.getTitle())){
                index = i;
                break;
            }
        }
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    public void reLoadHead(){
        head_url = sp.getString("head_url",head_url);
        Glide.with(this)
                .load(head_url)
                .fallback(R.drawable.head_pic)
                .placeholder(R.drawable.head_pic)
                .error(R.drawable.head_red)
                .into(iv_avatar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = -1;
        for(int i = 0,len = mTitles.length;i < len;i++){
            if(mTitles[i].equals(menuItem.getTitle())){
                index = i;
                break;
            }
        }
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);

//            updateSideNavStatus(menuItem);
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==RESULT_OK){
            if(resultCode == 2){
                userName = sp.getString("nickName","用户356416");
                phone = sp.getString("phone","0");
                head_url = sp.getString("head_url","");
                initHeader();
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.start_live:
//                Utils.showPrivacyDialog(this, null);
                //ToastUtil.normal("点击开始直播");
                Intent intent = new Intent(MainActivity.this, PushActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottomNavigation.getMenu().getItem(position);
        toolbar.setTitle(item.getTitle());
        item.setChecked(true);

//        updateSideNavStatus(item);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_add, menu); return true; }
        // 菜单的监听方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.start_live:
                ToastUtil.normal( "点击了第一个菜单");
               // Toast.makeText(this, "点击了第一个菜单", T).show(); break;
            default: break;
        }
            return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Cache.with(this).path(getCacheDir().getPath())
//                .saveCache("messageList",)
    }

    @Override
    protected void onResume() {


        int id = getIntent().getIntExtra("id", 0);
        if (id == 2){
            viewPager.setCurrentItem(2);
        }
        super.onResume();
    }
}
