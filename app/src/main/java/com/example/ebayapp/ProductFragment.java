package com.example.ebayapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    public static final String TAG = "ProductFragment";
    Boolean itemSpecPresent = true,
        highlightsPresent = true,
        galleryPresent = true,
        pricePresent = true,
        shippingPresent = true;
    ProgressBar progressBar;
    TextView tvPbar;

    private OnFragmentInteractionListener mListener;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_product, container, false);
        progressBar = linearLayout.findViewById(R.id.progressbarProdFrag);
        tvPbar = (TextView)linearLayout.findViewById(R.id.progressBarTitleProdFrag);

        LinearLayout gallery = linearLayout.findViewById(R.id.gallery);
        HorizontalScrollView scrollView = linearLayout.findViewById(R.id.horizontalScrollview);
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        RelativeLayout relativeLayoutError = linearLayout.findViewById(R.id.productsError);

        if(ProductDetails.inValidItemId){
            relativeLayoutError.setVisibility(View.VISIBLE);
            return linearLayout;
        }else{
            relativeLayoutError.setVisibility(View.GONE);
        }

        String responseJson = getArguments().getString("individualItemString");
        String previousResponse = getArguments().getString("previousResponse");

        JSONObject jsonObject = null,
                secondJson = null;
        String custom = null,
                title = null,
                price = null,
                shippingCost = null,
                brand = null,
                subtitle = null;
        JSONArray jsonArray = null,
                jsonCustom = null;
        List<String> itemSpecificsList = new ArrayList<>();

        try {
            jsonObject = new JSONObject(responseJson);
            if(jsonObject.has("Item"))
                jsonObject = (JSONObject) jsonObject.getJSONObject("Item");
            else{
                Toast.makeText(getContext(), "Item details not found", Toast.LENGTH_SHORT).show();
                throw null;
            }

            if(jsonObject.has("PictureURL")) {
                jsonArray = jsonObject.getJSONArray("PictureURL");
            }else{
                Toast.makeText(getContext(), "No Picture URL found", Toast.LENGTH_SHORT).show();
            }

            if(jsonObject.has("CurrentPrice"))
                price = "$" + jsonObject.getJSONObject("CurrentPrice").getString("Value");
            else {
                pricePresent = false;
                price = null;
            }

            secondJson = new JSONObject(previousResponse);
            Log.d("secondJson", secondJson.toString());

            shippingCost = "N/A";
            if(secondJson.has("shippingInfo")){
                JSONObject jsonShipping = secondJson.getJSONArray("shippingInfo").getJSONObject(0);
                if(jsonShipping.has("shippingServiceCost")) {
                    shippingPresent = true;
                    shippingCost = jsonShipping.getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                    if (shippingCost.equals("0.0"))
                        shippingCost = "Free Shipping";
                    else
                        shippingCost = "$" + shippingCost + "Shipping";
                }
            }else{
                shippingPresent = false;
            }

            Log.d("jsonObjectBeforeCheckingTitle", jsonObject.toString());
            TextView tvTitle = (TextView)linearLayout.findViewById(R.id.productTitletv);
            if(jsonObject.has("Title")) {
                title = jsonObject.getString("Title");
                tvTitle.setVisibility(View.VISIBLE);
                Log.d("Title is: ", title);
            }else
                tvTitle.setVisibility(View.GONE);


            if(secondJson.has("subtitle"))
                subtitle = secondJson.getJSONArray("subtitle").getString(0);
            else
                subtitle = null;

            //Get Item Specifics
            LinearLayout linearLayoutItemSpecific = (LinearLayout)linearLayout.findViewById(R.id.specificationHeading);
            if(jsonObject.has("ItemSpecifics")){
                itemSpecPresent = true;
                linearLayoutItemSpecific.setVisibility(View.VISIBLE);

                jsonCustom = jsonObject.getJSONObject("ItemSpecifics").getJSONArray("NameValueList");

                for(int i = 0; i < jsonCustom.length(); i++){
                    if(jsonCustom.getJSONObject(i).getString("Name").equals("Brand"))
                        brand = jsonCustom.getJSONObject(i).getJSONArray("Value").getString(0);
                    else
                        itemSpecificsList.add(jsonCustom.getJSONObject(i).getJSONArray("Value").getString(0));
                }
            }else{
                itemSpecPresent = false;
                linearLayoutItemSpecific.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(jsonArray != null && jsonArray.length() > 0) {
            scrollView.setVisibility(View.VISIBLE);
            galleryPresent = true;

            for (int i = 0; i < jsonArray.length(); i++) {
                View view = layoutInflater.inflate(R.layout.gallery_item, gallery, false);
                ImageView imageView = view.findViewById(R.id.imageView);
                //imageView.setImageResource(R.mipmap.ic_launcher);
                try {
                    //Picasso.get().load(jsonArray.getString(i)).into(imageView);
                    //Picasso.get().load(jsonArray.getString(i)).fit().centerInside().into(imageView);
                    Picasso.get().load(jsonArray.getString(i)).resize(200, 250).into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gallery.addView(view);

            }
        }else{
            galleryPresent = false;
            scrollView.setVisibility(View.GONE);
        }
        TextView titleTv = (TextView)linearLayout.findViewById(R.id.productTitletv);
        if(title != null)
            titleTv.setText(title);
        else
            titleTv.setText("N/A");

        TextView priceTv = (TextView)linearLayout.findViewById(R.id.priceProduct);
        if(price == null)
            priceTv.setText("N/A");
        else
            priceTv.setText(price);

        TextView shippingTv = (TextView)linearLayout.findViewById(R.id.shippingProduct);
        if(shippingCost == null)
            shippingTv.setText("N/A");
        else
            shippingTv.setText(shippingCost);


        //highlight -- needs subtitle, price, brand
        LinearLayout subtitlell = (LinearLayout)linearLayout.findViewById(R.id.subtitleProduct);
        if(subtitle != null){
            subtitlell.setVisibility(View.VISIBLE);
            TextView tvSubtitle = (TextView)linearLayout.findViewById(R.id.textviewSubtitle);
            tvSubtitle.setText(subtitle);
        }else
            subtitlell.setVisibility(View.GONE);


        LinearLayout pricell = (LinearLayout)linearLayout.findViewById(R.id.priceProductFrag);
        if(price != null){
            pricell.setVisibility(View.VISIBLE);
            TextView pricetv = (TextView)linearLayout.findViewById(R.id.textviewPrice);
            pricetv.setText(price);
        }else
            pricell.setVisibility(View.GONE);


        LinearLayout brandll = (LinearLayout)linearLayout.findViewById(R.id.brandProduct);
        LinearLayout itemSpecll = (LinearLayout)linearLayout.findViewById(R.id.itemSpecificsLinearLayout);
        TextView rowTv;
        int N;


        if(brand != null) {
            brandll.setVisibility(View.VISIBLE);
            TextView tvbrand = (TextView) linearLayout.findViewById(R.id.textviewBrand);
            tvbrand.setText(brand);
        }else{
            brandll.setVisibility(View.GONE);
        }

        LinearLayout linearhighlights = (LinearLayout)linearLayout.findViewById(R.id.highlightsll);
        if(subtitle == null && price == null && brand == null){
            linearhighlights.setVisibility(View.GONE);
            highlightsPresent = false;
        }else{
            linearhighlights.setVisibility(View.VISIBLE);
            highlightsPresent = true;
        }

        if(jsonCustom != null && itemSpecPresent){
            if(jsonCustom != null) {
                N = jsonCustom.length();
                TextView[] itemSpecTv = new TextView[N];
                itemSpecll.setVisibility(View.VISIBLE);
                String displayText;
                if (brand != null) {
                    rowTv = new TextView(this.getContext());
                    displayText = "\u2022 " + brand;
                    rowTv.setText(displayText);
                    itemSpecll.addView(rowTv);
                    itemSpecTv[0] = rowTv;
                }

                for (int i = 0; i < itemSpecificsList.size(); i++) {
                    rowTv = new TextView(this.getContext());
                    String temp = itemSpecificsList.get(i);
                    displayText = "\u2022" + " " + temp.substring(0, 1).toUpperCase() + temp.substring(1);
                    rowTv.setText(displayText);
                    itemSpecll.addView(rowTv);
                    itemSpecTv[0] = rowTv;
                }
            }
        }else {
            itemSpecll.setVisibility(View.GONE);
        }

        if(!highlightsPresent && !pricePresent && !shippingPresent && !itemSpecPresent && !galleryPresent) {
            relativeLayoutError.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No Product Details Present", Toast.LENGTH_SHORT).show();
        }else{
            relativeLayoutError.setVisibility(View.GONE);
        }


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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}