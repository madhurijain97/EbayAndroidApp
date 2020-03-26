package com.example.ebayapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.ebayapp.RecyclerViewAdapterResults.MESSAGE;

public class RvAdapterWishList extends RecyclerView.Adapter<RvAdapterWishList.viewHolder> {

    Context context;
    List<ResultCardview> resultWish;
    CustomSharedPreference customSharedAdapter;
    private static final String TAG = "RvAdapterWishList";
    private static JSONArray responseArray;
    RelativeLayout relativeLayout;
    private static TextView textviewCount, textviewTotal;

    public RvAdapterWishList(Context context, List<ResultCardview> resultWish, CustomSharedPreference customSharedAdapter,
                             RelativeLayout relativeLayout){
        this.context = context;
        this.resultWish = resultWish;
        this.customSharedAdapter = customSharedAdapter;
        this.relativeLayout = relativeLayout;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.cardview_item_results, viewGroup, false);
        return new RvAdapterWishList.viewHolder(view);
    }

    public static void setJsonObject(JSONArray jsonArray) {
        responseArray = jsonArray;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int position) {

        String titleSetting = resultWish.get(position).getTitle();
        viewHolder.tvTitle.setText(titleSetting.toUpperCase());
        viewHolder.tvShipping.setText(resultWish.get(position).getShipping());
        viewHolder.tvCondition.setText(resultWish.get(position).getCondition());

        String price = "$" + resultWish.get(position).getPrice();
        viewHolder.tvPrice.setText(price);
        viewHolder.tvZipcode.setText(resultWish.get(position).getZipcode());

        if (resultWish.get(position).getImgResult() != null && !resultWish.get(position).getImgResult().isEmpty()) {
            Picasso.get().load(resultWish.get(position).getImgResult()).error(context.getResources().getDrawable(R.drawable.default_image)).into(viewHolder.imgResults);
        }

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cart_remove);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, R.color.red));
        viewHolder.imgResultsCart.setImageResource(resultWish.get(position).getImgResultCart());
    }

    @Override
    public int getItemCount() {
        return resultWish.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgResults, imgResultsCart;
        TextView tvTitle, tvZipcode, tvShipping, tvCondition, tvPrice;

        public viewHolder(@NonNull View itemView) {
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
            ResultCardview cardView = null;
            int position = getAdapterPosition();
            Log.d("positionInViewHolder", ""+position);
            if (view.getId() == R.id.imageCartIcon) {
                Log.d(TAG, "cart icon clicked in wish list");
                try {
                    System.out.println("CartIconClicked");
                    System.out.println(resultWish.get(position));

                    cardView = resultWish.get(position);
                    String itemKey = cardView.getItemId();
                    Log.d("removalItemKey", itemKey);
                    customSharedAdapter.deleteFromSharedPref(itemKey);
                    resultWish.remove(position);
                    responseArray.remove(position);
                    notifyDataSetChanged();

                    RelativeLayout relativeLayoutError = relativeLayout.findViewById(R.id.wishListError);
                    if(customSharedAdapter.getCountOfSharedPref() == 0){
                        relativeLayoutError.setVisibility(View.VISIBLE);
                    }else{
                        relativeLayoutError.setVisibility(View.GONE);
                    }

                    int count = customSharedAdapter.getCountOfSharedPref();
                    float total = computeTotalPriceOfItems(customSharedAdapter.getAllElementsInSharedPref());

                    textviewCount = relativeLayout.findViewById(R.id.wishListCount);
                    textviewCount.setText("Wishlist total( " + count + " items):");

                    textviewTotal = relativeLayout.findViewById(R.id.wishListPrice);
                    textviewTotal.setText("$ " + total);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                JSONObject jsonObject = null;
                try {
                    System.out.println("Printing ResponseArray");
                    System.out.println(responseArray);
                    Log.d("positionWhenCardClicked", position + "");
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

        public float computeTotalPriceOfItems(Map<String, ?> allEntries){
            float totalCompute = 0;
            for (Map.Entry<String, ?> entry : allEntries.entrySet()){
                try{
                    JSONObject jsonObject = new JSONObject(entry.getValue().toString());
                    String price;
                    if(jsonObject.has("sellingStatus")) {
                        price = jsonObject.getJSONArray("sellingStatus").getJSONObject(0).
                                getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                        totalCompute += Float.parseFloat(price);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return totalCompute;
        }
    }


}
