package cn.nchu.wuxi.xlivemeet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xutil.app.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.core.BaseActivity;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.version)
    TextView mVersionTextView;
    @BindView(R.id.about_list)
    XUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright)
    TextView mCopyrightTextView;
    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_about;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        title_bar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mVersionTextView.setText(String.format("版本号：%s", AppUtils.getAppVersionName()));
        XUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_homepage)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_wiki)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_github)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_update)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_sponsor)), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPage(SponsorFragment.class);
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));

    }
}
