package com.example.ebayapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import static com.example.ebayapp.RecyclerViewAdapterResults.WISH_LIST;

public class WishListTab extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String TAG = "WishListTab";
    //private static SharedPreferences sharedPreferences;
    private static CustomSharedPreference customSharedWishListTab;
    RelativeLayout relativeLayout;
    private static int totalItems;

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    List<ResultCardview> listForWishList;

    RelativeLayout linearLayout;
    JSONArray jsonArray = new JSONArray();
    float total = 0;

    public WishListTab() {
        // Required empty public constructor
    }

    public static WishListTab newInstance(String param1, String param2) {
        WishListTab fragment = new WishListTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customSharedWishListTab = new CustomSharedPreference(this.getContext(), "WishListTab");
        linearLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_wish_list_tab, container, false);

        listForWishList = new ArrayList<>();
        Map<String, ?> allEntries = customSharedWishListTab.getAllElementsInSharedPref();

        relativeLayout = linearLayout.findViewById(R.id.wishListError);




        return linearLayout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        Log.d(TAG, "In OnResume");

        super.onResume();

        customSharedWishListTab.sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        listForWishList = new ArrayList<>();
        Map<String, ?> allEntries = customSharedWishListTab.getAllElementsInSharedPref();

        if(allEntries.size() == 0){
            relativeLayout.setVisibility(View.VISIBLE);
        }else{
            relativeLayout.setVisibility(View.GONE);
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                JSONObject jsonObject = null;
                String itemId = null;

                /*if (listForWishList.contains(entry.getValue().toString()))
                    continue;*/
                try {
                    jsonObject = new JSONObject(entry.getValue().toString());
                    jsonArray.put(jsonObject);
                    Log.d("jsonArrayInWishList", jsonArray.toString());
                    Log.d("jsonObjectInWishList", jsonObject.toString());
                    itemId = entry.getKey();
                    String title = jsonObject.getJSONArray("title").getString(0);
                    String zipCode = jsonObject.getJSONArray("postalCode").getString(0);

                    String shippingEnter = "N/A";
                    try {
                        String shipping = jsonObject.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost")
                                .getJSONObject(0).getString("__value__");
                        if (Float.parseFloat(shipping) == 0)
                            shippingEnter = "Free Shipping";
                        else if (Float.parseFloat(shipping) > 0)
                            shippingEnter = "$" + (String) shipping;
                    } catch (Exception e) {
                        shippingEnter = "N/A";
                    }

                    String conditionEnter;
                    try {
                        String condition = jsonObject.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                        conditionEnter = condition;

                    } catch (Exception e) {
                        conditionEnter = "N/A";
                    }

                    String price = jsonObject.getJSONArray("sellingStatus").getJSONObject(0).
                            getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                    total += Float.parseFloat(price);
                    Float priceEnter = Float.parseFloat(price);
                    String galleryURL = jsonObject.getJSONArray("galleryURL").getString(0);
                    String check = itemId + ", " + title + ", " + zipCode + ", " + shippingEnter + ", " + conditionEnter + ", " + priceEnter + ", " + galleryURL;
                    listForWishList.add(new ResultCardview(galleryURL, R.drawable.cart_remove, title, zipCode, shippingEnter, conditionEnter, priceEnter, itemId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            TextView tvCount = linearLayout.findViewById(R.id.wishListCount);
            totalItems = allEntries.size();
            String wishListString = "Wishlist Total (" + totalItems + " items):";
            tvCount.setText(wishListString);

            TextView tvPrice = linearLayout.findViewById(R.id.wishListPrice);
            String wishListTotal = "$" + Float.toString(total);
            tvPrice.setText(wishListTotal);

            RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerViewWishList);
            RecyclerViewAdapterWishList recyclerViewAdapter = new RecyclerViewAdapterWishList(this.getContext(), listForWishList, relativeLayout);
            recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
            recyclerViewAdapter.setJsonObject(jsonArray);
            recyclerView.setAdapter(recyclerViewAdapter);
            System.out.println("listForWish");
            System.out.println(listForWishList);
        }
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        //super.setUserVisibleHint(isVisibleToUser);

        Log.d("visibilityOfWishList", String.valueOf(isVisibleToUser));
        if(isVisibleToUser) {
            List<ResultCardview> listForWishList = new ArrayList<>();
            Map<String, ?> allEntries = customSharedWishListTab.getAllElementsInSharedPref();

            Log.d("sizeOfSharedPrefMap",  String.valueOf(allEntries.size()));
            total = 0;

            RelativeLayout relativeLayout = (RelativeLayout)linearLayout.findViewById(R.id.wishListError);
            if(allEntries.size() == 0){
                //inflate a different layout
                relativeLayout.setVisibility(View.VISIBLE);
            }else {
                relativeLayout.setVisibility(View.GONE);
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    JSONObject jsonObject = null;
                    String itemId = null;

                    if (listForWishList.contains(entry.getValue().toString()))
                        continue;
                    try {
                        jsonObject = new JSONObject(entry.getValue().toString());
                        jsonArray.put(jsonObject);
                        Log.d("jsonArrayInWishList", jsonArray.toString());
                        Log.d("jsonObjectInWishList", jsonObject.toString());
                        itemId = entry.getKey();
                        String title = jsonObject.getJSONArray("title").getString(0);
                        String zipCode = jsonObject.getJSONArray("postalCode").getString(0);

                        String shippingEnter = "N/A";
                        try {
                            String shipping = jsonObject.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost")
                                    .getJSONObject(0).getString("__value__");
                            if (Float.parseFloat(shipping) == 0)
                                shippingEnter = "Free Shipping";
                            else if (Float.parseFloat(shipping) > 0)
                                shippingEnter = "$" + (String) shipping;
                        } catch (Exception e) {
                            shippingEnter = "N/A";
                        }

                        String conditionEnter;
                        try {
                            String condition = jsonObject.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                            conditionEnter = condition;

                        } catch (Exception e) {
                            conditionEnter = "N/A";
                        }

                        String price = jsonObject.getJSONArray("sellingStatus").getJSONObject(0).
                                getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                        total += Float.parseFloat(price);
                        Float priceEnter = Float.parseFloat(price);
                        String galleryURL = jsonObject.getJSONArray("galleryURL").getString(0);
                        String check = itemId + ", " + title + ", " + zipCode + ", " + shippingEnter + ", " + conditionEnter + ", " + priceEnter + ", " + galleryURL;
                        listForWishList.add(new ResultCardview(galleryURL, R.drawable.cart_remove, title, zipCode, shippingEnter, conditionEnter, priceEnter, itemId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView tvCount = (TextView) linearLayout.findViewById(R.id.wishListCount);
                    totalItems = allEntries.size();
                    String wishListString = "Wishlist Total (" + totalItems + " items):";
                    tvCount.setText(wishListString);

                    TextView tvPrice = (TextView) linearLayout.findViewById(R.id.wishListPrice);
                    String wishListTotal = "$" + Float.toString(total);
                    tvPrice.setText(wishListTotal);


                }
                RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.recyclerViewWishList);
                RecyclerViewAdapterWishList recyclerViewAdapter = new RecyclerViewAdapterWishList(this.getContext(), listForWishList, linearLayout, totalItems, total);
                recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
                recyclerViewAdapter.setJsonObject(jsonArray);
                recyclerView.setAdapter(recyclerViewAdapter);
                System.out.println("listForWish");
                System.out.println(listForWishList);
            }
        }
    }*/

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}