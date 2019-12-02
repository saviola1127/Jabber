package net.qiujuer.italker.push.common.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化之前调用的初始化窗口
        initWindows();

        if (initArgs(getIntent().getExtras())) {

            int layoutId = getContentLayoutId();
            setContentView(layoutId);

            initWidget();
            initData();
        } else {
            finish();
        }
    }

    /**
     * 初始化窗口设置
     */
    protected void initWindows() {

    }

    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    protected abstract int getContentLayoutId();

    protected void initWidget() {
        ButterKnife.bind(this);
    }

    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时，finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        @SuppressLint("RestrictedApi")
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof net.qiujuer.italker.push.common.app.Fragment) {
                    if (((net.qiujuer.italker.push.common.app.Fragment) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}
