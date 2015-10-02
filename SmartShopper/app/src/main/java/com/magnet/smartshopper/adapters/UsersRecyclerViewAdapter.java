package com.magnet.smartshopper.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.model.User;
import com.magnet.smartshopper.services.MagnetMessageService;
import com.magnet.smartshopper.walmart.model.Product;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = UsersRecyclerViewAdapter.class.getSimpleName();
    private List<User> users;
    public Activity mActivity;
    private Product product;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (users.size() > 0) {
                User user = users.get(getAdapterPosition());
                MagnetMessageService.shareTheProduct(view.getContext(),user,product);
            }
        }
    }



    public UsersRecyclerViewAdapter(Activity activity, List<User> users, Product product) {
        this.users = users;
        this.mActivity = activity;
        this.product = product;
    }

    @Override
    public UsersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
            inflate(R.layout.item_user, parent, false);
        return new UsersRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UsersRecyclerViewAdapter.ViewHolder holder, int position) {
        if (position >= users.size()) {
            return;
        }

        User item = users.get(position);
        holder.tvUsername.setText(item.getUsername());
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        } else {
            return 0;
        }
    }

    public void clear() {
        users.clear();
    }

    public void addAll(List<MMXUser> mmxUsers) {
        for (int i = 0; i < mmxUsers.size(); i++) {
            User user = new User();
            user.setUsername(mmxUsers.get(i).getUsername());
            users.add(user);
        }
    }
}
