package com.example.ebayapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.example.ebayapp.MainActivity.SERVER_TO_HIT;

//import static com.example.ebayapp.RecyclerViewAdapterResults.WISH_LIST;

public class ProductDetails extends AppCompatActivity implements ProductFragment.OnFragmentInteractionListener,
        PhotosFragment.OnFragmentInteractionListener, ShippingFragment.OnFragmentInteractionListener, SimilarItemsFragment.OnFragmentInteractionListener{

    public static final String TAG = "Product Details";
    public static final String FB_ID = "2052807138130016";
    public JSONObject responseJson;
    CustomSharedPreference customSharedProduct;
    private static SharedPreferences.Editor editor;
    public static Boolean inValidItemId = false;
    String itemId = null,
            jsonObjectString = null,
            titleActionBar = null,
            titleForToast = null;
    ProgressBar progressBar;
    TextView tvBar;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        progressBar = findViewById(R.id.progressbarProductDetail);
        progressBar.setVisibility(View.VISIBLE);

        tvBar = findViewById(R.id.progressProductTitle);
        tvBar.setVisibility(View.VISIBLE);

        customSharedProduct = new CustomSharedPreference(this, "ProductDetailsActivity");

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_image_facebook, null);
        getSupportActionBar().setCustomView(v);

        Intent intent = getIntent();
        jsonObjectString = intent.getStringExtra(RecyclerViewAdapterResults.MESSAGE);
        Log.d("jsonObjectStringProdDet", jsonObjectString);

        try {
            JSONObject jsonObject = new JSONObject(jsonObjectString);
            if(jsonObject.has("itemId"))
                itemId = jsonObject.getJSONArray("itemId").getString(0);
            Log.d("Item id clicked", itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Bundle args = new Bundle();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerSecond);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutSecond);
        tabLayout.addTab(tabLayout.newTab().setText("PRODUCT"));
        tabLayout.addTab(tabLayout.newTab().setText("SHIPPING"));
        tabLayout.addTab(tabLayout.newTab().setText("PHOTOS"));
        tabLayout.addTab(tabLayout.newTab().setText("SIMILAR"));

        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_active);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery_active);
        tabLayout.getTabAt(2).setIcon(R.drawable.google_active);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal_active);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        RequestQueue queue = Volley.newRequestQueue(this);
        //String tempItemId = "531424674674";
        //String productsApiCall = SERVER_TO_HIT + "productsTab?itemId=" + tempItemId;
        String productsApiCall = SERVER_TO_HIT + "productsTab?itemId=" + itemId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                productsApiCall, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    responseJson = new JSONObject(response.toString());
                    final ProductDetailsAdapter pagerAdapter = new ProductDetailsAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), String.valueOf(responseJson), args, jsonObjectString);

                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    tvBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("ResponseJSON", responseJson.toString());
                try {
                    if(responseJson.has("Item")) {
                        inValidItemId = false;
                        JSONObject jsonObjectTemp = new JSONObject(responseJson.toString());
                        jsonObjectTemp = jsonObjectTemp.getJSONObject("Item");
                        if (jsonObjectTemp.has("Title")) {
                            titleActionBar = jsonObjectTemp.getString("Title");
                            titleForToast = jsonObjectTemp.getString("Title");
                            if (titleActionBar.length() > 29)
                                titleActionBar = titleActionBar.substring(0, 29) + "...";

                            TextView tvTitle = findViewById(R.id.actionBarTitle);
                            tvTitle.setText(titleActionBar);
                        }
                    }else{
                        inValidItemId = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);

        floatingActionButton = findViewById(R.id.buttonWish);
        if(customSharedProduct.containsItemKey(itemId))
            floatingActionButton.setImageResource(R.drawable.cart_remove);
        else
            floatingActionButton.setImageResource(R.drawable.cart_plus);

        findViewById(R.id.buttonWish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = null;
                if(customSharedProduct.containsItemKey(itemId)){
                    floatingActionButton.setImageResource(R.drawable.cart_plus);
                    customSharedProduct.deleteFromSharedPref(itemId);

                    message = titleForToast + " removed from wish list.";
                }else{
                    floatingActionButton.setImageResource(R.drawable.cart_remove);
                    customSharedProduct.addToSharedPreferece(itemId, jsonObjectString);
                    message = titleForToast + " added to wish list.";
                }

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public JSONObject getResult(){
        Log.d("response sent", responseJson.toString());
        return responseJson;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void facebookButtonClicked(View view){
        Log.d("Facebook", "Facebook button clicked");
        String title = null,
                price = null,
                ViewItemURLForNaturalSearch = null,
                hashtag = null;
        try {
            if(responseJson.has("Item")) {
                JSONObject jsonObTemp = new JSONObject(responseJson.toString());
                jsonObTemp = jsonObTemp.getJSONObject("Item");
                if(jsonObTemp.has("Title"))
                    title = jsonObTemp.getString("Title");
                else
                    title = "N/A";

                if(jsonObTemp.has("CurrentPrice"))
                    price = jsonObTemp.getJSONObject("CurrentPrice").getString("Value");
                else
                    price = "N/A";

                if(jsonObTemp.has("ViewItemURLForNaturalSearch"))
                    ViewItemURLForNaturalSearch = jsonObTemp.getString("ViewItemURLForNaturalSearch");
                else
                    ViewItemURLForNaturalSearch = "N/A";

                hashtag = "%23CSCI571Spring2019Ebay";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String descriptionRaw = "Buy " + title + " at $" + price + " from link below.";
        String description = null;
        try {
            description = URLEncoder.encode(descriptionRaw, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String facebookApiCall;
        if(!ViewItemURLForNaturalSearch.equals("N/A"))
            facebookApiCall = "https://www.facebook.com/dialog/share?app_id=" + FB_ID + "&display=popup"  + "&quote=" + description + "&href=" + ViewItemURLForNaturalSearch  + "&hashtag=" + hashtag;
        else
            facebookApiCall = "https://www.facebook.com/dialog/share?app_id=" + FB_ID + "&display=popup"  + "&quote=" + description + "&href=https://www.ebay.com/&hashtag=" + hashtag;
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(facebookApiCall));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public boolean backButtonClicked(View view){
        finish();
        return true;
    }

}
