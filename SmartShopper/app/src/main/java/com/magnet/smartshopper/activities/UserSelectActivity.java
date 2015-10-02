package com.magnet.smartshopper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXUser;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.adapters.UsersRecyclerViewAdapter;
import com.magnet.smartshopper.model.User;
import com.magnet.smartshopper.walmart.model.Product;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

public class UserSelectActivity extends AppCompatActivity {

    final String TAG = "UserSelectActivity";

    RecyclerView rvUsers;
    List<User> userlist;

    UsersRecyclerViewAdapter adapter;
    Product product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        product = (Product) getIntent().getSerializableExtra(ProductListActivity.PRODUCT_DETAIL_KEY);
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        userlist = new ArrayList<>();
        adapter = new UsersRecyclerViewAdapter(this, userlist,product);
        rvUsers.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUsers.setLayoutManager(layoutManager);

        updateViewState();
    }

    private void updateViewState() {
        // Find all users
        MMXUser.findByName("%", 50, new MMXUser.OnFinishedListener<ListResult<MMXUser>>() {
            public void onSuccess(ListResult<MMXUser> users) {
                refreshListView(users.totalCount > 0 ? users.items : null);
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                Log.e(TAG, "MMXUser.findByName() error: " + failureCode, throwable);
                refreshListView(null);
            }
        });
    }

    protected void refreshListView(final List<MMXUser> users) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                if (users != null && users.size() > 0) {
                    adapter.addAll(users);
                    rvUsers.getAdapter().notifyDataSetChanged();
                } else {
                    Toast.makeText(UserSelectActivity.this, "No users found.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
