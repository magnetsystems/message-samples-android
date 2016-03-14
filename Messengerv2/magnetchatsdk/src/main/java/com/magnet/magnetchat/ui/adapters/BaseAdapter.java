package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.magnet.magnetchat.callbacks.OnRecyclerViewItemClickListener;
import java.util.List;

public abstract class BaseAdapter<V extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<V> {

    protected LayoutInflater mInflater;
    protected List<T> mData;
    protected Context mContext;
    protected OnRecyclerViewItemClickListener mOnClickListener;

    public BaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mData = data;
    }

    /**
     * @param position
     * @return an item by position in list
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(T item) {
        if(!mData.contains(item)) {
            mData.add(0, item);

            notifyItemInserted(0);
        }
    }

    public void append(List<T> items) {
        if(null != items && !items.isEmpty()) {
            int sizeBefore = mData.size();
            for(T item : items) {
                append(item);
            }
            notifyItemRangeInserted(sizeBefore, mData.size());
        }
    }

    public void append(T item) {
        if (!mData.contains(item)) {
            mData.add(item);
        }
    }

    public void insert(List<T> items) {
        if(null != items && !items.isEmpty()) {
            int sizeBefore = mData.size();
            for(int i = items.size() -1; i >=0; i--) {
                T item = items.get(i);
                if (!mData.contains(item)) {
                    mData.add(0, item);
                }
            }
            notifyItemRangeInserted(sizeBefore, mData.size());
        }
    }

    public void updateItem(T item) {
        int position = mData.indexOf(item);
        if(position > -1) {
            notifyItemChanged(position);
        }
    }

    public void setOnClickListener(OnRecyclerViewItemClickListener onConversationLongClick) {
        this.mOnClickListener = onConversationLongClick;
    }

    public void swapData(List<T> data){
        if(mData != data) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    protected Context getContext() {
        return mContext;
    }


}
