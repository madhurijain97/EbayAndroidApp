package com.example.ebayapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.ebayapp.RecyclerViewAdapterResults.MESSAGE;
//import static com.example.ebayapp.RecyclerViewAdapterResults.WISH_LIST;

public class RecyclerViewAdapterWishList extends RecyclerView.Adapter<RecyclerViewAdapterWishList.wishViewHolder> {

    private static Context context;
    private static List<ResultCardview> resultsWish;
    public static  String TAG = "RecyclrViewAdaptrWish";
    public static JSONArray responseArray;
    //private static SharedPreferences sharedPreferences;
    //private static SharedPreferences.Editor editor;
    private static CustomSharedPreference customSharedWish;
    public static int totalItems, positionPublic;
    private static RelativeLayout relativeLayout;
    private static WishListTab wishListTab;
    float total;

    public RecyclerViewAdapterWishList(Context context, List<ResultCardview> resultsWish, RelativeLayout relativeLayout) {
        this.context = context;
        this.resultsWish = resultsWish;
        this.relativeLayout = relativeLayout;
        Log.d(TAG, String.valueOf(resultsWish));
    }

    @NonNull
    @Override
    public RecyclerViewAdapterWishList.wishViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_item_results, viewGroup, false);
        customSharedWish = new CustomSharedPreference(context, "RecyclerViewWshList");
        wishListTab = new WishListTab();
        return new RecyclerViewAdapterWishList.wishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterWishList.wishViewHolder wishViewHolder, int position) {

        JSONObject clickedObjectValue = null;

        String titleSetting = resultsWish.get(position).getTitle();

        wishViewHolder.tvTitle.setText(titleSetting.toUpperCase());
        wishViewHolder.tvShipping.setText(resultsWish.get(position).getShipping());
        wishViewHolder.tvCondition.setText(resultsWish.get(position).getCondition());

        String price = "$" + resultsWish.get(position).getPrice();

        wishViewHolder.tvPrice.setText(price);
        wishViewHolder.tvZipcode.setText(resultsWish.get(position).getZipcode());

        if (resultsWish.get(position).getImgResult() != null && !resultsWish.get(position).getImgResult().isEmpty()) {
            Picasso.get().load(resultsWish.get(position).getImgResult()).into(wishViewHolder.imgResults);
        }

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cart_remove);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.red));
        wishViewHolder.imgResultsCart.setImageResource(resultsWish.get(position).getImgResultCart());

        /*wishViewHolder.imgResultsCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject clickedObjectValue = responseArray.getJSONObject(position);
                    String itemKey = clickedObjectValue.getJSONArray("itemId").getString(0);
                    customSharedWish.deleteFromSharedPref(itemKey);

                    Map<String, ?> allEntries = customSharedWish.getAllElementsInSharedPref();
                    //resultsWish.remove(position);
                    //notifyItemRemoved(position);
                    //notifyItemRangeChanged(position, resultsWish.size());
                    notifyDataSetChanged();
                    Log.d("resultsSize", String.valueOf(resultsWish.size()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, titleSetting + " removed from wish list", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return resultsWish.size();
    }



    public static void setJsonObject(JSONArray jsonArray) {
        responseArray = jsonArray;
    }

    public class wishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgResults, imgResultsCart;
        TextView tvTitle, tvZipcode, tvShipping, tvCondition, tvPrice;

        public wishViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            imgResults = (ImageView) itemView.findViewById(R.id.imageItem);
            imgResultsCart = (ImageView) itemView.findViewById(R.id.imageCartIcon);
            tvTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            tvZipcode = (TextView) itemView.findViewById(R.id.textViewZipCode);
            tvShipping = (TextView) itemView.findViewById(R.id.textViewShipping);
            tvCondition = (TextView) itemView.findViewById(R.id.textViewConditionCard);
            tvPrice = (TextView) itemView.findViewById(R.id.textViewPriceCard);

            itemView.setOnClickListener(this);
            imgResultsCart.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.imageCartIcon) {
                Log.d(TAG, "cart icon clicked in wish list");
                try {

                    Map<String, ?> entriesInSharedPref = customSharedWish.getAllElementsInSharedPref();
                    for(Map.Entry<String, ?> entry : entriesInSharedPref.entrySet()){
                        Log.d("itemKeyInSharedPref", entry.getValue().toString());
                    }

                    JSONObject clickedObjectValue = responseArray.getJSONObject(getAdapterPosition());

                    String itemKey = clickedObjectValue.getJSONArray("itemId").getString(0);
                    Toast.makeText(context, "" + itemKey, Toast.LENGTH_SHORT).show();
                    Log.d("contains" ,""+customSharedWish.containsItemKey(itemKey));
                    Log.d("itemKeyClicked", itemKey);

                    customSharedWish.deleteFromSharedPref(itemKey);
                    responseArray.remove(getAdapterPosition());

                    Map<String, ?> allEntries = customSharedWish.getAllElementsInSharedPref();
                    Log.d("size2 - ", ""+entriesInSharedPref.size());
                    for(Map.Entry<String, ?> entry : entriesInSharedPref.entrySet()){
                        Log.d("itemKeyInSharedPref2 - ", entry.getValue().toString());
                    }
                    resultsWish = new ArrayList<>();

                        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());

                            try {
                                ResultCardview resultCardview = new ResultCardview();
                                resultsWish.add(resultCardview.returnNewObject(new JSONObject(entry.getValue().toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//                    resultsWish.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    if(resultsWish.size() == 0)
                        relativeLayout.setVisibility(View.VISIBLE);


                    //notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                RecyclerViewAdapterWishList.wishViewHolder viewHolder = (RecyclerViewAdapterWishList.wishViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                JSONObject jsonObject = null;
                try {
                    jsonObject = responseArray.getJSONObject(position);
                    Toast.makeText(context, jsonObject.getJSONArray("title").getString(0), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Card view clicked");
                Intent intent = new Intent(itemView.getContext(), ProductDetails.class);
                //intent.putExtra(POSITION, position);
                intent.putExtra(MESSAGE, jsonObject.toString());
                itemView.getContext().startActivity(intent);


            }
        }
    }

}