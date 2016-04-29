package com.magnet.magnetchat.ui.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
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
    public TypedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseMMXTypedView view = factory.createView(parent.getContext(), viewType);
        return new TypedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TypedViewHolder holder, int position) {
        holder.item.setObject(data.get(position));
    }

    public void put(Collection<T> data) {
        this.data.addAll(data);
    }

    public void put(T data) {
        this.data.add(data);
    }

    public void delete(T data) {
        this.data.remove(data);
    }

    public void set(Collection<T> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TypedViewHolder extends RecyclerView.ViewHolder {

        private final BaseMMXTypedView item;

        public TypedViewHolder(BaseMMXTypedView itemView) {
            super(itemView);
            this.item = itemView;
        }
    }

    public interface ItemComparator<T> {
        int compare(T o1, T o2);

        boolean areContentsTheSame(T o1, T o2);

        boolean areItemsTheSame(T item1, T item2);
    }
}
