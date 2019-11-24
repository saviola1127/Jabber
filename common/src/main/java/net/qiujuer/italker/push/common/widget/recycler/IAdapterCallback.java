package net.qiujuer.italker.push.common.widget.recycler;

public interface IAdapterCallback<Data> {

    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);

}
