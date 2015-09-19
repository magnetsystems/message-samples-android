package com.magnet.smartshopper.walmart;


import android.content.Context;
import android.content.res.Resources;

import com.magnet.smartshopper.R;
import com.magnet.smartshopper.SmartShopperApp;
import com.magnet.smartshopper.walmart.model.Items;
import com.magnet.smartshopper.walmart.model.Product;
import com.magnet.smartshopper.walmart.model.SearchResponseObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import retrofit.RestAdapter;


public class SearchItemService {

    private static final String  API_KEY = "API_KEY";
    private static String RESPONSE_FORMAT = "RESPONSE_FORMAT";
    private static final String BASE_URL = "BASE_URL";

    private RestAdapter restAdapter;
    private Properties credentials;

    boolean initialized = false;


    private SearchItemService(){
        init();
    }

    public synchronized void init() {

        credentials = new Properties();
        try {
            credentials.load(Resources.getSystem().openRawResource(R.raw.walmartlabscredentials));
        } catch (IOException e) {
            e.printStackTrace();
        }
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();
        initialized = true;
    }

    public  List<Product> getItems(String category) {
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        SearchItemServiceClient apiService =
                restAdapter.create(SearchItemServiceClient.class);

        SearchResponseObject response = apiService.search(category, RESPONSE_FORMAT, API_KEY);

        List<Product> products = new ArrayList<Product>(2);

        for(Items items : response.getItems()){
            Product product = new Product();
            product.setId(items.getItemId());
            product.setName(items.getName());
            product.setSalePrice("$"+ items.getSalePrice());
            product.setThumbnailImage(items.getThumbnailImage());

            products.add(product);

        }
        return products;


    }


}
