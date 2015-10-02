package com.magnet.smartshopper.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.walmart.model.Product;
import java.util.List;

public class WishListRecyclerViewAdapter extends RecyclerView.Adapter<WishListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = WishListRecyclerViewAdapter.class.getSimpleName();
    private List<Product> products;
    public Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvProduct = (TextView) itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (products.size() > 0) {
                Product product = products.get(getAdapterPosition());
            }
        }
    }



    public WishListRecyclerViewAdapter(Activity activity,List<Product> products) {
        this.mActivity = activity;
        this.products = products;
    }

    @Override
    public WishListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
            inflate(R.layout.item_user, parent, false);
        return new WishListRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WishListRecyclerViewAdapter.ViewHolder holder, int position) {
        if (position >= products.size()) {
            return;
        }

        Product product = products.get(position);
        holder.tvProduct.setText(product.getName() + " - " + product.getSalePrice());
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }

    }

    public void clear() {
        products.clear();
    }

    public void addAll(List<Product> products) {
        for (Product product:products) {
            this.products.add(product);
        }
    }
}
