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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecyclerViewAdapterResults extends RecyclerView.Adapter<RecyclerViewAdapterResults.resultsViewHolder> {

    private static Context context;
    private static List<ResultCardview> results;
    private static final String TAG = "RecyclrViewAdaptrReslts";
    private static final String POSITION = "ItemClickedPosition";
    public static final String MESSAGE = "ItemClickedDetail";
    //public static final String WISH_LIST = "wishListSharedPreference";
    private static JSONArray responseArray;
    private static resultsViewHolder vieHolderForResults;
    CustomSharedPreference customSharedPreference;

    public RecyclerViewAdapterResults(Context context, List<ResultCardview> results) {
        this.context = context;
        this.results = results;
        customSharedPreference = new CustomSharedPreference(context, "RecyclerViewAdapterResults");
    }

    @NonNull
    @Override
    public resultsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_item_results, viewGroup, false);
        return new resultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final resultsViewHolder resultsViewHolder, final int position) {
        vieHolderForResults = resultsViewHolder;

        String titleSetting = results.get(position).getTitle();
        String shippingSetting = results.get(position).getShipping();
        String conditionSetting = results.get(position).getCondition();
        Float initialPrice = results.get(position).getPrice();
        String zipCodeSetting = results.get(position).getZipcode();

        if(titleSetting == null || titleSetting.equals("N/A"))
            resultsViewHolder.tvTitle.setText("N/A");
        else
            resultsViewHolder.tvTitle.setText(titleSetting.toUpperCase());

        if(shippingSetting != null && !shippingSetting.equals("N/A"))
            resultsViewHolder.tvShipping.setText(shippingSetting);
        else
            resultsViewHolder.tvShipping.setText("N/A");

        if(conditionSetting != null && !conditionSetting.equals("N/A"))
            resultsViewHolder.tvCondition.setText(conditionSetting);
        else
            resultsViewHolder.tvCondition.setText("N/A");

        if(initialPrice != null && !initialPrice.toString().equals("N/A"))
            resultsViewHolder.tvPrice.setText("$" + String.valueOf(initialPrice));
        else
            resultsViewHolder.tvPrice.setText("N/A");

        if(zipCodeSetting != null && !zipCodeSetting.equals("N/A"))
            resultsViewHolder.tvZipcode.setText(zipCodeSetting);
        else
            resultsViewHolder.tvZipcode.setText("N/A");

        if (results.get(position).getImgResult() != null && !results.get(position).getImgResult().isEmpty() &&
                !results.get(position).getImgResult().equals("N/A")) {
            Picasso.get().load(results.get(position).getImgResult()).error(context.getResources().getDrawable(R.drawable.default_image)).into(resultsViewHolder.imgResults);
        }

        System.out.println("Results page " + customSharedPreference.getCountOfSharedPref());

        if(customSharedPreference.containsItemKey(results.get(position).getItemId())){
            results.get(position).setImgResultCart(R.drawable.cart_remove);
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cart_remove);
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.red));
            resultsViewHolder.imgResultsCart.setImageResource(results.get(position).getImgResultCart());
        } else {
            resultsViewHolder.imgResultsCart.setImageResource(R.drawable.cart_plus);
            results.get(position).setImgResultCart(R.drawable.cart_plus);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static void setJsonObject(JSONArray jsonArray) {
        responseArray = jsonArray;
    }

    public class resultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgResults, imgResultsCart;
        TextView tvTitle, tvZipcode, tvShipping, tvCondition, tvPrice;

        public resultsViewHolder(View itemView) {
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
                Log.d(TAG, "cart icon in result clicked");
                String message = null;
                try {
                    JSONObject clickedObjectValue = responseArray.getJSONObject(getAdapterPosition());
                    String itemKey = clickedObjectValue.getJSONArray("itemId").getString(0);

                    if(customSharedPreference.containsItemKey(itemKey)){
                        customSharedPreference.deleteFromSharedPref(itemKey);
                        results.get(getAdapterPosition()).setImgResultCart(R.drawable.cart_plus);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cart_plus);
                        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.black));
                        message = results.get(getAdapterPosition()).getTitle() + " removed from wish list";

                    }else{
                        customSharedPreference.addToSharedPreferece(itemKey, clickedObjectValue.toString());
                        results.get(getAdapterPosition()).setImgResultCart(R.drawable.cart_remove);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cart_remove);
                        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.red));
                        message = results.get(getAdapterPosition()).getTitle() + " added to wish list";
                    }
                    imgResultsCart.setImageResource(results.get(getAdapterPosition()).getImgResultCart());
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                RecyclerViewAdapterResults.resultsViewHolder viewHolder = (RecyclerViewAdapterResults.resultsViewHolder) view.getTag();
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
                intent.putExtra(MESSAGE, jsonObject.toString());
                itemView.getContext().startActivity(intent);
            }
        }
    }
}