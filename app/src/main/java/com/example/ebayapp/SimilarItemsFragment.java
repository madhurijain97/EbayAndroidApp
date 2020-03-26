package com.example.ebayapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.ebayapp.MainActivity.SERVER_TO_HIT;


public class SimilarItemsFragment extends Fragment {
    public static final String TAG = "SimilarItemsFragment";
    List <SimilarItems> listSimilarItems;
    Spinner parameterSpinner, orderBySpinner;
    String parameter, orderBy = "Asc";
    ProgressBar progressBar;
    TextView tvPbar;

    private OnFragmentInteractionListener mListener;

    public SimilarItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_similar_items, container, false);
        listSimilarItems = new ArrayList<>();
        String responseJson = getArguments().getString("individualItemString");

        progressBar = linearLayout.findViewById(R.id.progressbarSimilar);
        progressBar.setVisibility(View.VISIBLE);

        tvPbar = (TextView)linearLayout.findViewById(R.id.progressProductTitleSimilar);
        tvPbar.setVisibility(View.VISIBLE);

        String itemId = null;
        try {
            JSONObject jsonObject = new JSONObject(responseJson);
            if(jsonObject.has("Item"))
                jsonObject = jsonObject.getJSONObject("Item");
            else{
                Toast.makeText(getContext(), "No item found", Toast.LENGTH_SHORT).show();
                throw null;
            }

            if(jsonObject.has("ItemID"))
                itemId = jsonObject.getString("ItemID");
            else{
                Toast.makeText(getContext(), "No item id found", Toast.LENGTH_SHORT).show();
                throw null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String similarItemsApiCall = SERVER_TO_HIT + "similarItemsTab?itemId=" + itemId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                similarItemsApiCall, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                jsonParse(response, linearLayout);
                progressBar.setVisibility(View.GONE);
                tvPbar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonArrayRequest);


        parameterSpinner = linearLayout.findViewById(R.id.parameterSpinner);
        orderBySpinner = linearLayout.findViewById(R.id.orderBySpinner);

        parameterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String parameterSelected = parameterSpinner.getSelectedItem().toString();
                Log.d("parameterSelected", parameterSelected);
                switch(parameterSelected){
                    case "Name":
                        parameter = "Title";
                        break;
                    case "Price":
                        parameter = "Price";
                        break;
                    case "Days":
                        parameter = "DaysLeft";
                        break;
                    default:
                        parameter = "Default";
                        break;
                }
                Log.d("ValueOfParameter1", parameter + " ");
                if(!parameter.equals("Default") && parameter != null){
                    orderBySpinner.setEnabled(true);
                    List<SimilarItems> returnedList = createSortedItems(listSimilarItems, parameter, orderBy);
                    RecyclerView mRecyclerView = (RecyclerView)linearLayout.findViewById(R.id.recyclerViewSimilarItems);
                    RecyclerViewAdapterSimilarItems mAdapter = new RecyclerViewAdapterSimilarItems(getContext(), returnedList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                if(parameter.equals("Default") && parameter != null){
                    orderBySpinner.setEnabled(false);
                    RecyclerView mRecyclerView = (RecyclerView)linearLayout.findViewById(R.id.recyclerViewSimilarItems);
                    RecyclerViewAdapterSimilarItems mAdapter = new RecyclerViewAdapterSimilarItems(getContext(), listSimilarItems);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                if(parameter == null){
                    orderBySpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String orderBySelected = orderBySpinner.getSelectedItem().toString();
                Log.d("orderBySelected", orderBySelected);
                switch(orderBySelected){
                    case "Descending":
                        orderBy = "desc";
                        break;
                    default:
                        orderBy = "Asc";
                        break;

                }
                Log.d("ValueOfParameter2", parameter + " ");
                if(!parameter.equals("Default") && parameter != null){
                    List<SimilarItems> returnedList = createSortedItems(listSimilarItems, parameter, orderBy);
                    RecyclerView mRecyclerView = (RecyclerView)linearLayout.findViewById(R.id.recyclerViewSimilarItems);
                    RecyclerViewAdapterSimilarItems mAdapter = new RecyclerViewAdapterSimilarItems(getContext(), returnedList);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                if(parameter.equals("Default") && parameter != null){
                    RecyclerView mRecyclerView = (RecyclerView)linearLayout.findViewById(R.id.recyclerViewSimilarItems);
                    RecyclerViewAdapterSimilarItems mAdapter = new RecyclerViewAdapterSimilarItems(getContext(), listSimilarItems);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return linearLayout;
    }

    public void jsonParse(JSONArray jsonArray, LinearLayout linearLayout) {
        JSONObject jsonObject;
        RelativeLayout relativeLayout = linearLayout.findViewById(R.id.similarItemsError);
        try {
            if (jsonArray.length() == 0) {
                relativeLayout.setVisibility(View.VISIBLE);
                orderBySpinner.setEnabled(false);
                parameterSpinner.setEnabled(false);
            } else {
                relativeLayout.setVisibility(View.GONE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String title = null,
                            shipping = null,
                            daysLeft = null,
                            imageURL = null,
                            viwItemURl = null;
                    Float price = null;
                    jsonObject = jsonArray.getJSONObject(i);

                    if(jsonObject.has("title"))
                        title = jsonObject.getString("title");

                    if(jsonObject.has("shippingCost"))
                        shipping = jsonObject.getString("shippingCost");

                    if(jsonObject.has("timeLeft"))
                        daysLeft = jsonObject.getString("timeLeft");

                    if(jsonObject.has("price"))
                        price = Float.valueOf(jsonObject.getString("price"));

                    if(jsonObject.has("imageURL"))
                        imageURL = jsonObject.getString("imageURL");

                    if(jsonObject.has("viewItemURL"))
                        viwItemURl = jsonObject.getString("viewItemURL");

                    listSimilarItems.add(new SimilarItems(title, shipping, daysLeft, price, imageURL, viwItemURl));

                }
                RecyclerView mRecyclerView = (RecyclerView) linearLayout.findViewById(R.id.recyclerViewSimilarItems);
                RecyclerViewAdapterSimilarItems mAdapter = new RecyclerViewAdapterSimilarItems(getContext(), listSimilarItems);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public List<SimilarItems> createSortedItems(List<SimilarItems> similarItems, String sortBy, String order) {
        Log.d("Entered createSortedItems", sortBy + " " + order);
        List<SimilarItems> similarItemsCopy = new ArrayList<>(similarItems);

        if (sortBy.equals("Price")) {
            similarItemsCopy.sort(new Comparators.AscSortPriceComparator());
            if (order.equals("desc")) {
                Collections.reverse(similarItemsCopy);
            }
        } else if (sortBy.equals("Title")) {
            similarItemsCopy.sort(new Comparators.AscSortTitleComparator());
            if (order.equals("desc")) {
                Collections.reverse(similarItemsCopy);
            }
        } else if (sortBy.equals("Shipping")) {
            similarItemsCopy.sort(new Comparators.AscSortShippingComparator());
            if (order.equals("desc")) {
                Collections.reverse(similarItemsCopy);
            }
        } else if (sortBy.equals("DaysLeft")) {
            similarItemsCopy.sort(new Comparators.AscSortDaysLeftComparator());
            if (order.equals("desc")) {
                Collections.reverse(similarItemsCopy);
            }
        }

        return similarItemsCopy;
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

    public void getArgumentsOfSimilarItems(){
        String responseJson = getArguments().getString("individualItemString");
        Log.d(TAG, responseJson.toString());
    }
}
