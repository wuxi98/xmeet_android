package cn.nchu.wuxi.xlivemeet.adpter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import cn.nchu.wuxi.xlivemeet.adpter.entity.MyMessage;

public class SortedListCallback extends SortedListAdapterCallback<MyMessage> {
    public SortedListCallback(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    /**
     * 把它当成equals 方法就好
     */
    @Override
    public int compare(MyMessage o1, MyMessage o2) {
        return (int)o2.getCurrentTime().getTime() - (int)o1.getCurrentTime().getTime();
    }

    /**
     * 和DiffUtil方法一致，用来判断 两个对象是否是相同的Item。
     */
    @Override
    public boolean areItemsTheSame(MyMessage item1, MyMessage item2) {
        return item1.getUser().getPhone() .equals(item2.getUser().getPhone());
    }
    /**
     * 和DiffUtil方法一致，返回false，代表Item内容改变。会回调mCallback.onChanged()方法;
     */
    @Override
    public boolean areContentsTheSame(MyMessage oldItem, MyMessage newItem) {
        //默认相同 有一个不同就是不同
        if (oldItem.getUser().getPhone().equals(newItem.getUser().getPhone())) {
            return false;
        }
        if (oldItem.getCurrentMsg().equals(newItem.getCurrentMsg())) {
            return false;
        }
        if (oldItem.getCurrentTime() != newItem.getCurrentTime()) {
            return false;
        }
        return true;
    }
}