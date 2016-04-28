package com.magnet.magnetchat.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.magnet.magnetchat.model.Typed;
import com.magnet.magnetchat.ui.factories.MMXListItemFactory;
import com.magnet.magnetchat.ui.views.abs.BaseMMXTypedView;

import java.util.List;

/**
 * Created by aorehov on 28.04.16.
 */
public class RecyclerViewTypedAdapter<T extends Typed> extends RecyclerView.Adapter<RecyclerViewTypedAdapter.TypedViewHolder> {

    private MMXListItemFactory factory;
    private List<T> data;

    @Override
    public TypedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseMMXTypedView view = factory.createView(parent.getContext(), viewType);
        return new TypedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TypedViewHolder holder, int position) {
        holder.item.setObject(data.get(position));
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
}
