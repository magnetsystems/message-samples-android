package com.magnet.smartshopper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.magnet.mmx.client.api.ListResult;
import com.magnet.mmx.client.api.MMXChannel;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.adapters.WishListRecyclerViewAdapter;
import com.magnet.smartshopper.services.MagnetMessageService;
import com.magnet.smartshopper.walmart.model.Product;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

public class WishListActivity extends AppCompatActivity {

    private final String TAG = "WishListActivity";

    RecyclerView rvProducts;
    public List<Product> productList;

    WishListRecyclerViewAdapter adapter;
    Product product;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_select);
        productList = new ArrayList<Product>(2);
        product = (Product) getIntent().getSerializableExtra(ProductListActivity.PRODUCT_DETAIL_KEY);
        rvProducts = (RecyclerView) findViewById(R.id.rvUsers);
        adapter = new WishListRecyclerViewAdapter(this,productList);
        rvProducts.setAdapter(new SlideInBottomAnimationAdapter(adapter));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProducts.setLayoutManager(layoutManager);

        updateViewState();
    }

    private void updateViewState() {
        //Find all items in the wish list
        populateWishList();
    }


    public  void populateWishList() {
        final List<Product> products = new ArrayList<Product>(3);
        final MMXChannel privateChannel = new MMXChannel.Builder()
                .name(MagnetMessageService.MY_WISH_LIST)
                .setPublic(false)
                .build();

        Date now = new Date();
        //Last 6 months list
        Date startDate = new Date(now.getTime() - (180 *24 * 60 * 60 * 1000l));
        int limit = 100;
        boolean ascending = false;

        privateChannel.getItems(startDate, now, limit, ascending, new MMXChannel.OnFinishedListener<ListResult<MMXMessage>>() {

            @Override
            public void onSuccess(ListResult<MMXMessage> mmxMessageListResult) {
                for (MMXMessage message : mmxMessageListResult.items) {
                    Product product = new Product();
                    product.setId(message.getContent().get(MagnetMessageService.ITEM_ID));
                    product.setName(message.getContent().get(MagnetMessageService.ITEM_NAME));
                    product.setSalePrice(message.getContent().get(MagnetMessageService.ITEM_PRICE));
                    product.setThumbnailImage(message.getContent().get(MagnetMessageService.ITEM_IMAGE));
                    products.add(product);
                }
                refreshListView(products);

            }

            @Override
            public void onFailure(MMXChannel.FailureCode failureCode, Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        "Error while reading the wish list : " + throwable.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }



    public void refreshListView(final List<Product> products) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                if (products != null && products.size() > 0) {
                    adapter.addAll(products);
                    rvProducts.getAdapter().notifyDataSetChanged();
                } else {
                    Toast.makeText(WishListActivity.this, "Wish list is empty.", Toast.LENGTH_LONG).show();
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
