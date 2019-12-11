package net.qiujuer.italker.push.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.qiujuer.italker.push.common.R;
import net.qiujuer.italker.push.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GalleyView extends RecyclerView {
    private static final int LOADER_ID = 0x00;
    private static final int MAX_IMAGE_SEL = 3; //最大的图片选中数量
    private static final int MIN_IMAGE_SIZE = 10 * 1024;
    private LoaderCallback loaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;

    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));

        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClicked(RecyclerAdapter.ViewHolder holder, Image image) {
                //cell点击操作，如果说我们的点击是允许的，那么更新对应的cell的状态
                //然后更新界面，同理：如果说不能允许点击（已经达到最大的选中数量）那么就不刷新界面
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }

    /****
     * 初始化方法，返回一个loaderID
     * @param loaderManager
     * @return 一个loader ID，可用于销毁LOADER
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
        return LOADER_ID;
    }


    /***
     * cell点击的具体逻辑
     * @param image
     * @return true 代表进行数据更改，需要刷新；反之则不刷新
     */

    private boolean onItemSelectClick(Image image) {
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            //如果之前在，那现在就移除
            mSelectedImages.remove(image);
            image.isSelected = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_SEL) {
                //Toast for alert
                Toast.makeText(getContext(),
                        String.format(getResources().getString(R.string.label_gallery_select_max_size), MAX_IMAGE_SEL),
                        Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelected = true;
                notifyRefresh = true;
            }
        }

        //如果数据有更改，那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh) {
            notifySelectChanged();
        }

        return notifyRefresh;
    }


    /****
     * 得到选中图片的全部地址
     * @return 一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;

        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }


    public void clear() {
        for (Image image : mSelectedImages) {
            //一定要先重置状态
            image.isSelected = false;
        }
        mSelectedImages.clear();

        //通知更新
        mAdapter.notifyDataSetChanged();
    }

    /***
     *
     */
    private void notifySelectChanged() {
        //回调数量变化
        if (mListener != null) {
            mListener.onSelectedCountChanged(mSelectedImages.size());
        }
    }


    /****
     * 通知adapter 数据更改
     * @param images 新的数据
     */
    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }


    /***
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC"); //倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            //当loader加载完成时
            List<Image> images = new ArrayList<>();
            // 判断是否有数据
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    //游标移动到开始
                    data.moveToFirst();

                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);

                    do {
                        //循环读取直到没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long datetime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_SIZE) {
                            continue;
                        }

                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.datetime = datetime;
                        images.add(image);

                    } while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            updateSource(null);
        }
    }


    /***
     * 一个cell的内部数据结构
     */
    private static class Image {
        //数据的ID
        int id;
        //图片的路径
        String path;
        //图片的创建时间
        long datetime;
        //是否选中
        boolean isSelected;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /***
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_gallery;
        }

        @Override
        protected ViewHolder<Image> createViewHolder(View root, int viewType) {
            return new GalleyView.ViewHolder(root);
        }
    }

    /****
     * cell对应的view holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_sel);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //直接从原图加载
                    .centerCrop() //居中剪切
                    .placeholder(R.color.windowsBackground)
                    .into(mPic);
            mShade.setVisibility(image.isSelected ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelected);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /***
     * 对外的一个监听器
     */
    public interface SelectedChangeListener{
        void onSelectedCountChanged(int count);
    }
}
