package net.qiujuer.italker.push.frags.main;


import net.qiujuer.italker.push.R;
import net.qiujuer.italker.push.common.app.Fragment;
import net.qiujuer.italker.push.common.widget.GalleryView;

import butterknife.BindView;


public class ActiveFragment extends Fragment {

    @BindView(R.id.galleryView)
    GalleryView mGallery;

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

        mGallery.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
