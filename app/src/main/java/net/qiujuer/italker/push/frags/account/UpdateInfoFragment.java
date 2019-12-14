package net.qiujuer.italker.push.frags.account;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.qiujuer.italker.push.R;
import net.qiujuer.italker.push.common.widget.GalleryView;
import net.qiujuer.italker.push.common.widget.PortraitView;
import net.qiujuer.italker.push.frags.media.GalleryFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更新信息的界面
 * A simple {@link Fragment} subclass.
 */
public class UpdateInfoFragment extends net.qiujuer.italker.push.common.app.Fragment {

    @BindView(R.id.pv_portrait)
    PortraitView mPortraitView;


    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.pv_portrait)
    void onPortraitViewClick() {
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {

                    }
                }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

}
