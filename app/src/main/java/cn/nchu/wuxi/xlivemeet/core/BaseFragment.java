package cn.nchu.wuxi.xlivemeet.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.core.PageOption;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.nchu.wuxi.xlivemeet.R;

public abstract class BaseFragment extends Fragment {

    Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = loadRootView(inflater,container,savedInstanceState);
        mUnbinder = ButterKnife.bind(this,v);
        init();
        return v;

    }

    protected  View loadRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        int res = getLayoutId();
        return inflater.inflate(res,container,false);
    }

    protected abstract void init();

    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 打开一个新的页面【建议只在主tab页使用】
     *
     * @param clazz
     * @param <T>
     * @return
     */
   /* public <T extends XPageFragment> Fragment openNewPage(Class<T> clazz) {
        return new PageOption(clazz)
                .setNewActivity(true)
                .open(this);
    }*/


}
