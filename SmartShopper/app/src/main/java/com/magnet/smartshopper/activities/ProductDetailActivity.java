package com.magnet.smartshopper.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.services.MagnetMessageService;
import com.magnet.smartshopper.walmart.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {
    public ImageView ivItemImage;
    public TextView tvItemName;
    public TextView tvItemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        // Fetch views
        ivItemImage = (ImageView) findViewById(R.id.ivItemImage);
        tvItemName = (TextView) findViewById(R.id.tvItemName);
        tvItemPrice = (TextView) findViewById(R.id.tvItemPrice);
        // Use the product to populate the data into our views
        final Product product = (Product) getIntent().getSerializableExtra(ProductListActivity.PRODUCT_DETAIL_KEY);
        loadProduct(product);


        //Select the users from the list
        Button btnShare = (Button)findViewById(R.id.btnShare);
        btnShare.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(ProductDetailActivity.this, UserSelectActivity.class);
                        intent.putExtra(ProductListActivity.PRODUCT_DETAIL_KEY, product);
                        startActivity(intent);
                    }
                }
        );

        //Add to Wish List
        Button btnAddToWhishList = (Button)findViewById(R.id.btnAddToWishList);
        btnAddToWhishList.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        MagnetMessageService.addToWishList(getApplicationContext(),product);

                    }
                }
        );

    }

    // Populate data for the product
    private void loadProduct(Product product) {
        //change activity title
        this.setTitle(product.getName());
        // Populate data
        Picasso.with(this).load(Uri.parse(product.getThumbnailImage())).error(R.drawable.notification_template_icon_bg).into(ivItemImage);
        tvItemName.setText(product.getName());
        tvItemPrice.setText(product.getSalePrice());
    }


}
