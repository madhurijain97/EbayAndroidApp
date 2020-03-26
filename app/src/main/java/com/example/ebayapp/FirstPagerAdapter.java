package com.example.ebayapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FirstPagerAdapter extends FragmentStatePagerAdapter {

    int globalNumberOfTabs;
    public FirstPagerAdapter(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.globalNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                SearchTab searchTab = new SearchTab();
                return searchTab;
            case 1:
                //WishListTab wishListTab = new WishListTab();
                WishListNew wishListTab = new WishListNew();
                return wishListTab;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return globalNumberOfTabs;
    }
}
