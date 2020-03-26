package com.example.ebayapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerViewAdapterSimilarItems extends RecyclerView.Adapter<RecyclerViewAdapterSimilarItems.SimilarItemsViewHolder> {

    private Context mContext;
    private List<SimilarItems> mData;
    private int positionPublic;

    public RecyclerViewAdapterSimilarItems(Context mContext, List<SimilarItems> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public SimilarItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.similar_single_item, viewGroup, false);

        return new SimilarItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarItemsViewHolder similarItemsViewHolder, final int position) {

        positionPublic = position;
        similarItemsViewHolder.tvTitle.setText(mData.get(position).getTitle().toUpperCase());

        String shippingValue = mData.get(position).getShipping();
        String shippingCost = null;
        if(shippingValue != null && !shippingValue.equals("N/A") && Float.parseFloat(shippingValue) == 0)
            shippingCost = "Free Shipping";
        else if(shippingValue == null || shippingValue.equals("N/A"))
            shippingCost = "N/A";
        else
            shippingCost = "$" + shippingValue + " Shipping";

        similarItemsViewHolder.tvShipping.setText(shippingCost);

        String daysLeft = mData.get(position).getDaysLeft() + " Days Left";
        similarItemsViewHolder.tvDaysLeft.setText(daysLeft);

        String setPrice = "$" + String.valueOf(mData.get(position).getPrice());

        similarItemsViewHolder.tvPrice.setText(setPrice);
        //similarItemsViewHolder.thumbnail.setImageResource(mData.get(position).getThumbnail());
        similarItemsViewHolder.thumbnail.setImageResource(R.drawable.butterfly);
        if(mData.get(position).getThumbnail() != null && mData.get(position).getThumbnail() != "N/A")
            Picasso.get().load(mData.get(position).getThumbnail()).into(similarItemsViewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SimilarItemsViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvShipping, tvDaysLeft, tvPrice;
        ImageView thumbnail;
        RelativeLayout parentLayout;

        public SimilarItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            tvTitle = (TextView)itemView.findViewById(R.id.textViewTitleSimilar);
            tvShipping = (TextView)itemView.findViewById(R.id.textViewShippingSimilar);
            tvDaysLeft = (TextView) itemView.findViewById(R.id.textViewDaysLeftSimilar);
            tvPrice = (TextView)itemView.findViewById(R.id.textViewPriceSimilar);
            thumbnail = (ImageView) itemView.findViewById(R.id.imageViewSimilarItem);
            parentLayout = itemView.findViewById(R.id.parentLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("viewItemURL", mData.get(positionPublic).getViewItemURL());
                    Uri uri = Uri.parse(mData.get(positionPublic).getViewItemURL());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }
            });

        }
    }

}
