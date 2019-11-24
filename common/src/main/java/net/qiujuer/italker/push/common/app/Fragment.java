package net.qiujuer.italker.push.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class Fragment extends android.support.v4.app.Fragment {

    private View mRoot;
    private Unbinder mRootUnbinder;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRoot == null) {
            int layoutId = getContentLayoutId();

            //初始化当前的根布局，但是不在此时刚刚创建的时候就把它添加到container里头去
            View root = inflater.inflate(layoutId, container, false);

            initWidget(root);
            mRoot = root;
        } else {

            if (mRoot.getParent() != null) {
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }

        }

        return mRoot;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract int getContentLayoutId();

    /*
     * 初始化窗口设置
     */
    protected void initWindows() {

    }

    protected void initData() {

    }

    protected void initWidget(View root) {
        mRootUnbinder = ButterKnife.bind(this, root);
    }

    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /*
     * 返回按键，触发时调用，
     * return true 代表已处理逻辑，activity不用finish
     * return false 代表没有处理逻辑，activity需要自己处理
     */
    public boolean onBackPressed() {
        return false;
    }
}
