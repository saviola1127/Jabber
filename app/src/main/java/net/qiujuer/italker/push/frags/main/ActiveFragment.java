package net.qiujuer.italker.push.frags.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.qiujuer.italker.push.R;
import net.qiujuer.italker.push.common.app.Fragment;
import net.qiujuer.italker.push.common.widget.GalleyView;

import butterknife.BindView;


public class ActiveFragment extends Fragment {

    @BindView(R.id.galleryView)
    GalleyView mGallery;

    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();

        mGallery.setup(getLoaderManager(), new GalleyView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
