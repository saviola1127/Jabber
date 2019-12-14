package net.qiujuer.italker.push.frags.media;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.italker.push.R;
import net.qiujuer.italker.push.common.widget.GalleryView;

/**
 * 图片选择fragment
 * A simple {@link Fragment} subclass.
 */

public class GalleryFragment extends BottomSheetDialogFragment
implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransBottomSheetDialog(getContext());
        //return new BottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = (GalleryView) root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        if (count > 0) {
            dismiss();
            if (mListener != null) {
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImage(paths[0]);
                mListener = null;
            }
        }
    }


    public GalleryFragment setListener(OnSelectedListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnSelectedListener {
        void onSelectedImage(String path);
    }


    private static class TransBottomSheetDialog extends BottomSheetDialog {

        public TransBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null) {
                return;
            }

            //得到屏幕高度像素值
            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            // 得到状态栏的高度
            int statusBarHeight = (int) Ui.dipToPx(getContext().getResources(), 25);

            int dialogHeight = screenHeight - statusBarHeight;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }
}
