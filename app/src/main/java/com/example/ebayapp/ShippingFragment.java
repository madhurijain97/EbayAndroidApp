package com.example.ebayapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.example.ebayapp.ProductDetails.inValidItemId;


public class ShippingFragment extends Fragment {
    public static final String TAG = "ShippingFragment";
    private OnFragmentInteractionListener mListener;
    JSONObject indiJson, prevJson, custom;
    String storeName = null,
            storeURL = null,
            feedbackScore = null,
            popularity = null,
            feedbackStar = null;

    String shippingCost = null,
            globalShipping = null,
            handlingTime = null,
            condition = null;

    String policy = null,
            returnsWithin = null,
            refundMode = null,
            shippedBy = null;

    public ShippingFragment() {
        // Required empty public constructor
    }

    public static ShippingFragment newInstance(String param1, String param2) {
        ShippingFragment fragment = new ShippingFragment();
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
        // Inflate the layout for this fragment
        ScrollView linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_shipping, container, false);
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        RelativeLayout shippingErrorLayout = linearLayout.findViewById(R.id.ShippingError);

        if(inValidItemId){
            shippingErrorLayout.setVisibility(View.VISIBLE);
            return linearLayout;
        }else
            shippingErrorLayout.setVisibility(View.GONE);


        String responseJson = getArguments().getString("individualItemString");
        String previousResponse = getArguments().getString("previousResponse");
        Log.d("previousResShipping", previousResponse.toString());
        Log.d("indiResp", responseJson.toString());



        try {
            indiJson = new JSONObject(responseJson);
            prevJson = new JSONObject(previousResponse);
            indiJson = indiJson.getJSONObject("Item");
            Log.d("indiJsonInShippingFragment", indiJson.toString());
            Log.d("prevJsonInShippingFragment", prevJson.toString());
            //SoldBy
            if(indiJson.has("Storefront")){
                custom = indiJson.getJSONObject("Storefront");
                if(custom.has("StoreName"))
                    storeName = custom.getString("StoreName");
                if(custom.has("StoreURL"))
                    storeURL = custom.getString("StoreURL");
            }

            if(indiJson.has("Seller")){
                custom = indiJson.getJSONObject("Seller");
                if(custom.has("FeedbackScore"))
                    feedbackScore = custom.getString("FeedbackScore");
                if(custom.has("PositiveFeedbackPercent"))
                popularity = custom.getString("PositiveFeedbackPercent");
                if(custom.has("FeedbackRatingStar"))
                    feedbackStar = custom.getString("FeedbackRatingStar");
            }

            //ShippingInfo
            shippingCost = "N/A";
            if(prevJson.has("shippingInfo")){
                custom = prevJson.getJSONArray("shippingInfo").getJSONObject(0);
                if(custom.has("shippingServiceCost")) {
                    shippingCost = custom.getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                    if (shippingCost.equals("0.0"))
                        shippingCost = "Free Shipping";
                    else
                        shippingCost = "$" + shippingCost;
                }
            }

            if(indiJson.has("GlobalShipping")){
                if(indiJson.getString("GlobalShipping").equals("true"))
                    globalShipping = "Yes";
                else
                    globalShipping = "No";
            }

            if(indiJson.has("HandlingTime")){
                if(indiJson.getString("HandlingTime").equals("0") || indiJson.getString("HandlingTime").equals("1"))
                    handlingTime = indiJson.getString("HandlingTime") + " " + "day";
                else
                    handlingTime = indiJson.getString("HandlingTime") + " " + "days";
            }

            if(indiJson.has("ConditionDescription"))
                condition = indiJson.getString("ConditionDescription");


            //Return Policy
            if(indiJson.has("ReturnPolicy")){
                custom = indiJson.getJSONObject("ReturnPolicy");

                if(custom.has("ReturnsAccepted"))
                    policy = custom.getString("ReturnsAccepted");
                if(custom.has("ReturnsWithin"))
                    returnsWithin = custom.getString("ReturnsWithin");
                if(custom.has("Refund"))
                    refundMode =  custom.getString("Refund");
                if(custom.has("ShippingCostPaidBy"))
                    shippedBy = custom.getString("ShippingCostPaidBy");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        TableLayout soldByTable = linearLayout.findViewById(R.id.soldByTable);
        LinearLayout soldByLayout = linearLayout.findViewById(R.id.soldByLinearLayout);
        if(storeName != null || storeURL != null || feedbackScore != null || popularity != null || feedbackStar != null){
            soldByTable.setVisibility(View.VISIBLE);
            soldByLayout.setVisibility(View.VISIBLE);

            if(storeName != null){
                TableRow tableRow = linearLayout.findViewById(R.id.storeNameRow);
                tableRow.setVisibility(View.VISIBLE);
                TextView tvStoreName = linearLayout.findViewById(R.id.storeName);
                TextView tvStoreValue = linearLayout.findViewById(R.id.storeValue);
                tvStoreName.setText("Store Name");
                tvStoreValue.setText(storeName);
                tvStoreValue.setPaintFlags(tvStoreValue.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                tvStoreValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(storeURL); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

            }

            if(feedbackScore != null){
                TableRow tableRow = linearLayout.findViewById(R.id.feedbackRow);
                tableRow.setVisibility(View.VISIBLE);
                TextView feedbackName = linearLayout.findViewById(R.id.feedbackScoreName);
                TextView feedbackValue = linearLayout.findViewById(R.id.feedbackScoreValue);
                feedbackName.setText("Feedback Score");
                feedbackValue.setText(feedbackScore);
            }

            if(popularity != null){
                TableRow tableRow = linearLayout.findViewById(R.id.popularityRow);
                tableRow.setVisibility(View.VISIBLE);
                CircularScoreView circularScoreView = linearLayout.findViewById(R.id.score_view);
                circularScoreView.setScore((int)Float.parseFloat(popularity));
            }

            if(feedbackStar != null){
                TableRow tableRow = linearLayout.findViewById(R.id.feedbackStarRow);
                tableRow.setVisibility(View.VISIBLE);
                TextView tvFeedbackScoreName = linearLayout.findViewById(R.id.feedbackStarName);
                ImageView tvFeedbackScoreValue =  linearLayout.findViewById(R.id.feedbackStarValue);
                tvFeedbackScoreName.setText("Feedback Star");

                Log.d(TAG, feedbackScore);
                Drawable drawable;
                if(Integer.parseInt(feedbackScore) >= 10000){
                    drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.star_circle);
                }else{
                    drawable = ContextCompat.getDrawable(this.getContext(), R.drawable.star_circle_outline);
                }
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable);

                int shootIndex = feedbackStar.toLowerCase().indexOf("shooting");
                if(shootIndex != -1)
                    feedbackStar = feedbackStar.substring(0, shootIndex);

                if(feedbackStar.toLowerCase().equals("yellow"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.staryellow));
                else if(feedbackStar.toLowerCase().equals("blue"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.starBlue));
                else if(feedbackStar.toLowerCase().equals("turquoise"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.starTurquoise));
                else if(feedbackStar.toLowerCase().equals("purple"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.starPurple));
                else if(feedbackStar.toLowerCase().equals("red"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.starRed));
                else if(feedbackStar.toLowerCase().equals("green"))
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.starGreen));
                else
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this.getContext(), R.color.black));

                tvFeedbackScoreValue.setImageDrawable(drawable);
            }


        }else{
            soldByTable.setVisibility(View.GONE);
            soldByLayout.setVisibility(View.GONE);
        }

        TableLayout shippingInfoTable = linearLayout.findViewById(R.id.shippingInfoTable);
        LinearLayout shippingLayout = linearLayout.findViewById(R.id.shippingLayout);
        if(shippingCost != null || globalShipping != null || handlingTime != null || condition != null){
            shippingInfoTable.setVisibility(View.VISIBLE);
            shippingLayout.setVisibility(View.VISIBLE);

            if(shippingCost != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Shipping Cost");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(shippingCost);
                shippingInfoTable.addView(tableRow);
            }

            if(globalShipping != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Global Shipping");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(globalShipping);
                shippingInfoTable.addView(tableRow);
            }

            if(handlingTime != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Handling Time");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(handlingTime);
                shippingInfoTable.addView(tableRow);
            }

            if(condition != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Condition");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(condition);
                //((TextView)tableRow.findViewById(R.id.attributeValue)).setWidth(220);
                shippingInfoTable.addView(tableRow);
            }

        }else{
            shippingInfoTable.setVisibility(View.GONE);
            shippingLayout.setVisibility(View.GONE);
        }

        TableLayout returnPolicyTable = linearLayout.findViewById(R.id.returnPolicyTable);
        LinearLayout returnPolicyLayout = linearLayout.findViewById(R.id.returnPolicyLayout);
        if(policy != null || returnsWithin != null || refundMode != null || shippedBy != null){
            returnPolicyTable.setVisibility(View.VISIBLE);
            returnPolicyLayout.setVisibility(View.VISIBLE);

            if(policy != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Policy");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(policy);
                returnPolicyTable.addView(tableRow);
            }
            if(returnsWithin != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Returns Within");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(returnsWithin);
                returnPolicyTable.addView(tableRow);
            }
            if(refundMode != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Refund Mode");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(refundMode);
                returnPolicyTable.addView(tableRow);
            }
            if(shippedBy != null){
                TableRow tableRow = (TableRow)layoutInflater.inflate(R.layout.table_row, null);
                ((TextView)tableRow.findViewById(R.id.attributeName)).setText("Shipped By");
                ((TextView)tableRow.findViewById(R.id.attributeValue)).setText(shippedBy);
                returnPolicyTable.addView(tableRow);
            }
        }
        else{
            returnPolicyTable.setVisibility(View.GONE);
            returnPolicyLayout.setVisibility(View.GONE);
        }

        if(storeName == null && storeURL == null && feedbackScore == null && popularity == null && feedbackStar == null
        && shippingCost == null && globalShipping == null && handlingTime == null && condition != null &&
                policy == null && returnsWithin == null && refundMode == null && shippedBy != null){
            Toast.makeText(getContext(), "No Shipping Info", Toast.LENGTH_SHORT).show();

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

    public void getArgumentOfShipping(){
        String responseJson = getArguments().getString("individualItemString");
        Log.d(TAG, responseJson.toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
