package com.magnet.magnetchat.ui.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.View;
import android.view.ViewGroup;

import com.magnet.magnetchat.model.Typed;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;

import java.util.Collection;

/**
 * Created by aorehov on 28.04.16.
 */
public class RecyclerViewTypedAdapter<T extends Typed> extends RecyclerView.Adapter<RecyclerViewTypedAdapter.TypedViewHolder> {

    private MMXListItemFactory factory;
    private SortedList<T> data;

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemCustomEventListener customEventListener;

    public RecyclerViewTypedAdapter(MMXListItemFactory factory, Class<T> clazz, final ItemComparator<T> comporator) {
        this.factory = factory;
        this.data = new SortedList<>(clazz, new SortedListAdapterCallback<T>(this) {
            @Override
            public int compare(T o1, T o2) {
                return comporator.compare(o1, o2);
            }

            @Override
            public boolean areContentsTheSame(T oldItem, T newItem) {
                return comporator.areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(T item1, T item2) {
                return comporator.areItemsTheSame(item1, item2);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public TypedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseMMXTypedView view = factory.createView(parent.getContext(), viewType);

        TypedViewHolder holder = new TypedViewHolder(view);
        holder.setCustomEventListener(customEventListener);
        holder.setItemClickListener(clickListener);
        holder.setLongClickListener(longClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(TypedViewHolder holder, int position) {
        holder.item.setObject(data.get(position));
    }

    public void put(Collection<T> data) {
        this.data.addAll(data);
    }

    public int put(T data) {
        return this.data.add(data);
    }

    public void delete(T data) {
        this.data.remove(data);
    }

    public void set(Collection<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {
        if (this.clickListener == clickListener) return;
        this.clickListener = clickListener;
        notifyDataSetChanged();
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        if (this.longClickListener == longClickListener) return;
        this.longClickListener = longClickListener;
        notifyDataSetChanged();
    }

    public void setCustomEventListener(OnItemCustomEventListener customEventListener) {
        if (this.customEventListener == customEventListener) return;
        this.customEventListener = customEventListener;
        notifyDataSetChanged();
    }

    public static class TypedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, BaseMMXTypedView.OnCustomEventListener {

        private final BaseMMXTypedView item;
        private OnItemClickListener itemClickListener;
        private OnItemLongClickListener longClickListener;
        private OnItemCustomEventListener customEventListener;

        public TypedViewHolder(BaseMMXTypedView itemView) {
            super(itemView);
            this.item = itemView;
        }

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            item.setOnClickListener(this.itemClickListener != null ? this : null);
        }

        public void setLongClickListener(OnItemLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
            item.setOnLongClickListener(this.longClickListener != null ? this : null);
        }

        public void setCustomEventListener(OnItemCustomEventListener customEventListener) {
            this.customEventListener = customEventListener;
            item.setCustomEventListener(this.customEventListener != null ? this : null);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onItemLongClick(getAdapterPosition());
            return true;
        }

        @Override
        public void onEvent(int event) {
            customEventListener.onEventHappened(getAdapterPosition(), event);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnItemCustomEventListener {
        void onEventHappened(int position, int event);
    }

    public interface ItemComparator<T> {
        int compare(T o1, T o2);

        boolean areContentsTheSame(T o1, T o2);

        boolean areItemsTheSame(T item1, T item2);
    }
}
