package net.qiujuer.italker.push.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;


/***
 *
 * 完成对Fragment的调度与重用问题，达到最优的Fragment切换
 * 对内存优化以及对切换显示的效率
 *
 */
public class NavHelper<T> {
    //所有的Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray();
    private final Context context;

    //初始化的必要参数
    private final int containerId;
    private final FragmentManager fragmentManager;
    private OnTabChangeListener<T> listener;

    //当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, OnTabChangeListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    // 输入执行点击菜单操作菜单的id
    // 返回是否能够处理这个点击
    public boolean performClickMenu(int menuId) {


        //集合中寻找点击的菜单对应的Tab
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {

            doSelect(tab);
            return true;
        }

        return false;
    }


    /***
     * 进行真实的tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;

        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                //如果点击的tab和当前tab重合，默认不作操作
                //可以考虑重复点击多次以后，实现某一个刷新逻辑
                notifyTabReselect();
                return;
            }
        }

        currentTab = tab;
        doTabChange(currentTab, oldTab);
    }


    private void doTabChange(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (oldTab != null) {
            if (oldTab.fragment != null) {
                //从UI界面移除fragment，
                //但是还在fragmentManager的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                //如果首次新建，再缓存
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                newTab.fragment = fragment;

                //提交到fragmentManager
                ft.add(containerId, fragment, newTab.clx.getName());
            } else {
                // 从fragmentManager的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }

        ft.commit();
        //通知回调
        notifyTabSelect(newTab, oldTab);
    }


    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }


    private void notifyTabReselect() {
        //TODO 二次点击Tab所做的操作
    }


    /***
     * 添加Tab和对应的菜单项
     * @param menuId
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;
    }


    /***
     * 获取当时显示的tab
     * @return
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }


    public static class Tab<T> {

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        //fragment对应的class信息
        public Class<?> clx;

        //额外的字段，用户可以自己设定需要使用什么内容
        public T extra;

        //内部缓存的Fragment
        //package权限，外部无法访问
        Fragment fragment;
    }

    /***
     * 事件回调接口，处理完成以后的动作上报
     * @param <T>
     */
    public interface OnTabChangeListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
