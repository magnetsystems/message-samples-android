package com.magnet.smartshopper.activities;


import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.magnet.mmx.client.common.Log;
import com.magnet.smartshopper.R;
import com.magnet.smartshopper.adapters.ProductAdapter;
import com.magnet.smartshopper.services.MagnetMessageService;
import com.magnet.smartshopper.walmart.SearchItemService;
import com.magnet.smartshopper.walmart.SearchItemServiceClient;
import com.magnet.smartshopper.walmart.model.Items;
import com.magnet.smartshopper.walmart.model.Product;
import com.magnet.smartshopper.walmart.model.SearchResponseObject;
import com.magnet.smartshopper.wunderground.WeatherService;
import com.magnet.smartshopper.wunderground.model.Weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import retrofit.RestAdapter;


public class ProductListActivity extends AppCompatActivity {

    private static final String  API_KEY = "API_KEY";
    private static String RESPONSE_FORMAT = "RESPONSE_FORMAT";
    private static final String BASE_URL = "BASE_URL";


    private final String TAG = "ProductListActivity";

    public static final String PRODUCT_DETAIL_KEY = "product";
    private ListView lvProducts;
    private ProductAdapter productAdapter;
    private ProgressBar progress;

    private RestAdapter restAdapter;
    private Properties credentials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWalmartApiCredentials();
        setContentView(R.layout.activity_product_list);

        Log.setLoggable(null, Log.VERBOSE);

        Toast.makeText(ProductListActivity.this,
                "Trying to get the weather", Toast.LENGTH_LONG).show();

        MagnetMessageService.registerAndLoginUser(this);

        lvProducts = (ListView) findViewById(R.id.lvProducts);
        ArrayList<Product> products = new ArrayList<Product>();
        // initialize the adapter
        productAdapter = new ProductAdapter(this, products);
        // attach the adapter to the ListView
        lvProducts.setAdapter(productAdapter);
        progress = (ProgressBar) findViewById(R.id.progress);
        setupProductSelectedListener();


        //Get Current Weather for fixed location SF
        new AsyncTask<Void,String,Weather>() {
            @Override
            protected Weather doInBackground(Void ...avoid) {
                try {
                    Weather weather = WeatherService.getCurrentTemperature();
                    return weather;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Weather weather) {
                updateWeather(weather);
            }
        }.execute();

    }

    public void initWalmartApiCredentials() {

        credentials = new Properties();
        try {
            credentials.load(this.getResources().openRawResource(R.raw.walmartlabscredentials));
        } catch (IOException e) {
            e.printStackTrace();
        }
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(credentials.getProperty(BASE_URL))
                .build();

    }

    private void updateWeather(Weather weather) {
        ((TextView) findViewById(R.id.tvCurrentTempature)).setText("It's currently " + weather.getTemp() + "F");

        if(Double.parseDouble(weather.getTemp()) <= 60.0){
            fetchProducts("sweater");
        }else{
            fetchProducts("sun glasses");
        }
    }

    public void setupProductSelectedListener() {
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the detail view passing book as an extra
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra(PRODUCT_DETAIL_KEY, productAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    private void fetchProducts(String query) {
        // Show progress bar before making network request
        progress.setVisibility(ProgressBar.VISIBLE);

        new AsyncTask<String, String, List<Product>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<Product> products) {
                applyResults(products);
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected List<Product> doInBackground(String... params) {

                if(params!=null && params.length>0) {
                    String category = params[0];
                    try {
                        List<Product> lists = fetchItemsFromWalmartLabs(category);
                        return lists;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return null;
            }
        }.execute(query);

    }


    private List<Product> fetchItemsFromWalmartLabs(String category) {

        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        SearchItemServiceClient apiService =  restAdapter.create(SearchItemServiceClient.class);

        SearchResponseObject response = apiService.search(category, (String) credentials.get(RESPONSE_FORMAT), (String) credentials.get(API_KEY));

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

    private void applyResults(List<Product> lists) {
        progress.setVisibility(ProgressBar.GONE);
        if (lists != null) {
            // Remove all products from the adapter
            productAdapter.clear();
            // Load model objects into the adapter
            for (Product product : lists) {
                productAdapter.add(product); // add book through the adapter
            }
            productAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.wishList) {
            Toast.makeText(ProductListActivity.this,
                    "Showing the wish list", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ProductListActivity.this, WishListActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
