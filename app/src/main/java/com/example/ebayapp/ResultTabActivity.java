package com.example.ebayapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.ebayapp.MainActivity.SERVER_TO_HIT;

public class ResultTabActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "ResultTabActivity";
    List<ResultCardview> listResults;
    ProgressDialog dialog;
    String title;
    ProgressBar progressBar;
    TextView tvPbar;
    Boolean itemsPresent = false;
    JSONArray jsonArray;
    RelativeLayout relativeLayout;
    public static JSONObject jsonObjectResponse = null,
            jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result_tab);
        ActionBar actionBar = getSupportActionBar();
        progressBar = findViewById(R.id.progressbarResultTab);
        progressBar.setVisibility(View.VISIBLE);

        tvPbar = (TextView)findViewById(R.id.progressBarTitle);
        tvPbar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String apiCall = intent.getStringExtra(MainActivity.PASSAPICALL);

        RequestQueue queue = Volley.newRequestQueue(this);

        String searchApiCall = SERVER_TO_HIT + "searchButton" + apiCall;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                searchApiCall, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParse(response);
                try {
                    jsonObjectResponse = new JSONObject(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                tvPbar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);
        actionBar.setTitle("Search Results");


    }

    private void jsonParse(JSONObject response){
        listResults = new ArrayList<>();
        jsonArray = null;
        relativeLayout = findViewById(R.id.resultsError);
        try {
            if(response.has("findItemsAdvancedResponse"))
                jsonArray = response.getJSONArray("findItemsAdvancedResponse");
            else
                return;

            jsonObject = jsonArray.getJSONObject(0);

            if(jsonObject.has("searchResult"))
                jsonArray = jsonObject.getJSONArray("searchResult");
            else
                return;

            jsonObject = jsonArray.getJSONObject(0);
            if(!jsonObject.has("item")){
                itemsPresent = false;
                relativeLayout.setVisibility(View.VISIBLE);
            }else {
                itemsPresent = true;
                jsonArray = jsonObject.getJSONArray("item");

                if (jsonArray.length() == 0) {
                    itemsPresent = false;
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    itemsPresent = true;
                    relativeLayout.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        ResultCardview resultCardview = new ResultCardview();
                        listResults.add(resultCardview.returnNewObject(jsonObject));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inflateProductCoutLayout();
        setRecyclerViewAdapter();
    }

    public void inflateProductCoutLayout(){
        LinearLayout countll = findViewById(R.id.resultTabProductCount);
        if(itemsPresent){
            countll.setVisibility(View.VISIBLE);
            TextView tvProdCount = findViewById(R.id.tvCount);
            TextView tvProdName = findViewById(R.id.tvProductName);

            Log.d("tvProdCount", tvProdCount.toString());
            Log.d("tvProdName", tvProdName.toString());

            String count = String.valueOf(jsonArray.length());
            tvProdCount.setText(count);
            tvProdName.setText(MainActivity.keyword);

        }
        else
            countll.setVisibility(View.GONE);
    }

    public void setRecyclerViewAdapter(){
        RecyclerView recyclerView = findViewById(R.id.recyclerViewResults);
        RecyclerViewAdapterResults recyclerViewAdapter = new RecyclerViewAdapterResults(this, listResults);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewAdapter.setJsonObject(jsonArray);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public  void onResume(){
        super.onResume();
        if(listResults != null)
            setRecyclerViewAdapter();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textViewTitle:
                Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }
}