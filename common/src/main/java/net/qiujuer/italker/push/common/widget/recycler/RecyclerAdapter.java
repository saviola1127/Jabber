package net.qiujuer.italker.push.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.qiujuer.italker.push.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, IAdapterCallback<Data> {

    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    /***
     * 构造函数
     */
    public RecyclerAdapter() {
        this(null);
    }


    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }


    public RecyclerAdapter(List<Data> list, AdapterListener<Data> listener) {
        mDataList = list;
        mListener = listener;
    }

    /****
     * 复写默认的布局类型返回
     * @param position 位置
     * @return XML文件的ID
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }


    /***
     * 获取布局的类型XML ID
     * @param position
     * @param data 当前布局的数据
     * @return
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);

    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<Data> holder = createViewHolder(root, viewType);

        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        //设置view的tag为ViewHolder 进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);

        //进行界面注解绑定
        holder.mUnbinder = ButterKnife.bind(holder, root);
        //绑定callback
        holder.mCallback = this;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> holder, int position) {
        //获取需要绑定的数据
        Data data = mDataList.get(position);

        //触发holder的banding方法
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /***
     * 添加数据
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyDataSetChanged();
    }

    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0){
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0){
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    /***
     * 清除数据
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }


    /***
     * 替换一段新的集合，其中包含清空操作
     * @param dataList
     */
    public void replace(Collection<Data> dataList) {

        if (dataList == null && dataList.size() == 0) {
            return;
        }

        mDataList.clear();
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            //得到viewHoler当前对应的适配器中的位置信息
            int pos = viewHolder.getAdapterPosition();
            mListener.onItemClicked(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            //得到viewHoler当前对应的适配器中的位置信息
            int pos = viewHolder.getAdapterPosition();
            mListener.onItemClicked(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }


    /**
     * 设置监听适配器
     * @param adapterListener
     */
    public void setListener(AdapterListener<Data> adapterListener) {
        mListener = adapterListener;
    }


    /**
     * 监听器，自定义
     * @param <Data>
     */
    public interface AdapterListener<Data> {
        void onItemClicked(RecyclerAdapter.ViewHolder holder, Data data);
        void onItemLongClicked(RecyclerAdapter.ViewHolder holder, Data data);
    }

    /***
     * 得到一个新的ViewHolder
     * @param root 根布局
     * @param viewType 界面的类型，约定为布局XML的ID
     * @return
     */
    protected abstract ViewHolder<Data> createViewHolder(View root, int viewType);


    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {

        protected Data mData;
        private Unbinder mUnbinder;
        private IAdapterCallback<Data> mCallback;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 当触发数据绑定时候的回调，必须复写此方法
         */
        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        protected abstract void onBind(Data data);

        /**
         * holder自己对自己对应的数据做更新操作
         * @param data
         */
        public void updateData(Data data) {
            if (mCallback != null) {
                mCallback.update(data, this);
            }
        }
    }
}
