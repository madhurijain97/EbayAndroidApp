package com.example.ebayapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;


public class CustomSharedPreference {

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    Context context;
    public static final String WISH_LIST_PREF = "wishListSharedPref";

    CustomSharedPreference(Context context, String comingFrom){
        this.context = context;
        Log.d("comingFrom", comingFrom);
        sharedPreferences = context.getSharedPreferences(WISH_LIST_PREF, Context.MODE_PRIVATE);
    }

    public void addToSharedPreferece(String itemKey, String itemObject){
        Log.d("Adding", itemObject);
        editor = sharedPreferences.edit();
        editor.putString(itemKey, itemObject);
        editor.apply();
    }

    public void deleteFromSharedPref(String itemKey){
        Log.d("Deleting", itemKey);
        editor = sharedPreferences.edit();
        editor.remove(itemKey);
        editor.apply();

    }

    public int getCountOfSharedPref(){
        Map<String, ?> allEntries = sharedPreferences.getAll();
        return allEntries.size();
    }

    public boolean containsItemKey(String itemKey){
        if(sharedPreferences.contains(itemKey))
            return true;
        return false;
    }

    public Map getAllElementsInSharedPref(){
        Map<String, ?> allEntries = sharedPreferences.getAll();
        return allEntries;
    }

    public void printAllKeys(){
        Map<String, ?> entriesInSharedPref = getAllElementsInSharedPref();
        Log.d("size - ", ""+entriesInSharedPref.size());
        for(Map.Entry<String, ?> entry : entriesInSharedPref.entrySet()){
            Log.d("itemKeyInSharedPref - ", entry.getValue().toString());
        }
    }

    public void clearSharedPreference(){
        sharedPreferences.edit().clear().commit();
    }
}
