package com.magnet.magnetchat.ui.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.magnet.magnetchat.callbacks.OnRecyclerViewItemClickListener;
import java.util.List;

@Deprecated
public abstract class BaseSortedAdapter<V extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<V> {

    protected LayoutInflater mInflater;
    protected SortedList<T> mData;
    protected Context mContext;
    protected OnRecyclerViewItemClickListener mOnClickListener;

    public interface ItemComparator<T> {
        int compare(T o1, T o2);
        boolean areContentsTheSame(T o1, T o2);
        boolean areItemsTheSame(T item1, T item2);
    }

    public abstract class BaseSortedListCallback<T> extends SortedList.Callback<T> {
        public abstract int compare(T o1, T o2) ;

        public abstract boolean areContentsTheSame(T oldItem, T newItem);

        public abstract boolean areItemsTheSame(T item1, T item2);

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }
    }

    public BaseSortedAdapter(final Context context, final List<T> data, final Class<T> clazz, final ItemComparator comparator) {
        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mData = new SortedList<T>(clazz, new BaseSortedListCallback<T>() {

            @Override public int compare(T item1, T item2) {
                return comparator.compare(item1, item2);
            }

            @Override public boolean areContentsTheSame(T oldItem, T newItem) {
                return comparator.areContentsTheSame(oldItem, newItem);
            }

            @Override public boolean areItemsTheSame(T item1, T item2) {
                return comparator.areItemsTheSame(item1, item2);
            }
        });

        mData.addAll(data);
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
        mData.add(item);
    }

    public void addItem(List<T> items) {
        if(null != items && !items.isEmpty()) {
            mData.addAll(items);
        }
    }

    public void swapData(List<T> data){
        if(mData != data) {
            mData.beginBatchedUpdates();
            mData.clear();
            mData.addAll(data);
            mData.endBatchedUpdates();
        }
        //notifyDataSetChanged();
    }

    public SortedList<T> getData() {
        return mData;
    }

    public void removeItem(int position) {
        mData.removeItemAt(position);
    }

    protected Context getContext() {
        return mContext;
    }

    public void setOnClickListener(OnRecyclerViewItemClickListener onConversationLongClick) {
        this.mOnClickListener = onConversationLongClick;
    }
}
