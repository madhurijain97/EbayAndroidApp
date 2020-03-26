package com.example.ebayapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WishListNew extends Fragment {


    private OnFragmentInteractionListener mListener;
    private final String TAG  = "WishListNew";
    private CustomSharedPreference customSharedWish;
    private RelativeLayout relativeLayoutError, relativeLayout;
    private List<ResultCardview> wishList = new ArrayList<>();
    private static JSONArray jsonArray = new JSONArray();
    private static TextView textviewCount, textviewTotal;



    public WishListNew() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_wish_list_tab, container, false);
        customSharedWish = new CustomSharedPreference(getContext(), "WishListNew");
        int count = 0;
        float total = 0;


        Map<String, ?> allEntries = customSharedWish.getAllElementsInSharedPref();
        relativeLayoutError = relativeLayout.findViewById(R.id.wishListError);
        if(allEntries.size() == 0)
            relativeLayoutError.setVisibility(View.VISIBLE);
        else
            relativeLayoutError.setVisibility(View.GONE);

        count = allEntries.size();
        total = totalPriceOfItems(allEntries);
        textviewCount = relativeLayout.findViewById(R.id.wishListCount);
        textviewCount.setText("Wishlist total(" + count + "items):");

        textviewTotal = relativeLayout.findViewById(R.id.wishListPrice);
        textviewTotal.setText("$" + total);

        return relativeLayout;
    }

    public float totalPriceOfItems(Map<String, ?> allEntries){
        float totalCompute = 0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()){
            try{
                JSONObject jsonObject = new JSONObject(entry.getValue().toString());
                String price = jsonObject.getJSONArray("sellingStatus").getJSONObject(0).
                        getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                totalCompute += Float.parseFloat(price);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return totalCompute;
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
        super.onResume();
        float total = 0;
        jsonArray = new JSONArray(new ArrayList<String>());
        wishList.clear();
        Map<String, ?> allEntries = customSharedWish.getAllElementsInSharedPref();
        int count = allEntries.size();
        relativeLayoutError = relativeLayout.findViewById(R.id.wishListError);
        if(allEntries.size() == 0){
            relativeLayoutError.setVisibility(View.VISIBLE);
        }else {
            relativeLayoutError.setVisibility(View.GONE);
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                JSONObject jsonObject = null;
                String itemKey = null;
                try {
                    jsonObject = new JSONObject(entry.getValue().toString());
                    jsonArray.put(jsonObject);
                    itemKey = entry.getKey();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ResultCardview resultCardview = new ResultCardview();
                wishList.add(resultCardview.returnNewObject(jsonObject));
            }
            total = totalPriceOfItems(allEntries);
        }

        RecyclerView recyclerView = relativeLayout.findViewById(R.id.recyclerViewWishList);
        RvAdapterWishList recyclerViewAdapter = new RvAdapterWishList(this.getContext(), wishList, customSharedWish, relativeLayout);
        recyclerViewAdapter.setJsonObject(jsonArray);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        recyclerView.setAdapter(recyclerViewAdapter);

        textviewCount = relativeLayout.findViewById(R.id.wishListCount);
        textviewCount.setText("Wishlist total( " + count + " items):");

        textviewTotal = relativeLayout.findViewById(R.id.wishListPrice);
        textviewTotal.setText("$ " + total);

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
