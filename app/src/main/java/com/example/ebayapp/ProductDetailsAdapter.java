package com.example.ebayapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONObject;

public class ProductDetailsAdapter extends FragmentStatePagerAdapter {

    int globalNumberOfTabs;
    String response;
    String previousResponse;
    Bundle bundle;
    FragmentManager fm;

    public ProductDetailsAdapter(FragmentManager fm, int numberOfTabs, String responseJson, Bundle bundle, String previousResponse){
        super(fm);
        this.fm = fm;
        this.globalNumberOfTabs = numberOfTabs;
        this.response = responseJson;
        this.bundle = bundle;
        this.previousResponse = previousResponse;
    }

    @Override
    public Fragment getItem(int position) {
        bundle.putString("individualItemString", response);
        bundle.putString("previousResponse", previousResponse);
        switch(position) {
            case 0:
                ProductFragment productTab = new ProductFragment();
                if(bundle != null)
                    productTab.setArguments(bundle);
                //productTab.getArgumentsOfProduct();
                return productTab;
            case 1:
                ShippingFragment shippingTab = new ShippingFragment();
                if(bundle != null)
                    shippingTab.setArguments(bundle);
                //shippingTab.getArgumentOfShipping();
                return shippingTab;
            case 2:
                PhotosFragment photosTab = new PhotosFragment();
                if(bundle != null)
                    photosTab.setArguments(bundle);
                //photosTab.getArgumentForPhotos();
                return photosTab;
            case 3:
                SimilarItemsFragment similarItemsTab = new SimilarItemsFragment();
                if(bundle != null)
                    similarItemsTab.setArguments(bundle);
                similarItemsTab.getArgumentsOfSimilarItems();
                return similarItemsTab;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return globalNumberOfTabs;
    }

}
