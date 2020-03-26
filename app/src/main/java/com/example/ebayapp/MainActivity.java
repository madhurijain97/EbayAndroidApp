package com.example.ebayapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

//import static com.example.ebayapp.RecyclerViewAdapterResults.WISH_LIST;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SearchTab.OnFragmentInteractionListener, WishListNew.OnFragmentInteractionListener{

    public static final String TAG = "MainActivity";
    public static final String PASSAPICALL = "apiCall";
    //public static final String SERVER_TO_HIT = "http://10.0.2.2:8081/";
    public static final String SERVER_TO_HIT = "http://androidmadhurijainebay.us-east-2.elasticbeanstalk.com/";
    EditText keywordEdit, milesFrom, zipcodeEditText;
    Spinner spinner;
    CheckBox localPickup, freeShipping, newCondition, usedCondition, unspecCondition, enableNearbySearch;
    RadioButton radioCurrent, radioZipCode;
    TextView errorZip, tvKeywordError;
    LinearLayout currentLinearLayout, linearLayoutZip;
    public static String keyword;
    public static String locationApiCall;
    Boolean loctionReceived = true;
    CustomSharedPreference customSharedMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customSharedMainActivity = new CustomSharedPreference(this, "MainActivity");
        //customSharedMainActivity.clearSharedPreference();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Wish List"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerFirst);
        final FirstPagerAdapter pagerAdapter = new FirstPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        final String locationApiCallString = "http://ip-api.com/json";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest userLocationRequest = new StringRequest(Request.Method.GET, locationApiCallString, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("UserLocation", response);
                jsonParse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationApiCall = null;
                //Toast.makeText(getApplicationContext(), "onErrorResponse when location API called", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(userLocationRequest);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_container, new SearchTab());
        fragmentTransaction.commit();
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(getResources().getColor(R.color.tabGrey), getResources().getColor(R.color.white));


    }

    public void jsonParse(String  jsonObject){
        JSONObject locationJson;
        try{
            locationJson = new JSONObject(jsonObject.toString());
            locationApiCall = locationJson.getString("zip");
        }catch(Exception exception){

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "An item is selected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "No item is selected", Toast.LENGTH_LONG).show();
    }

    public void searchButtonClicked(View view){

        Boolean isValid = true;


        keywordEdit = findViewById(R.id.editTextKeyword);
        spinner = findViewById(R.id.dropdownCategory);
        localPickup = (CheckBox)findViewById(R.id.checkboxLocalPickup);
        freeShipping = (CheckBox) findViewById(R.id.checkboxFreeShipping);
        newCondition = (CheckBox) findViewById(R.id.checkboxNew);
        usedCondition = (CheckBox)findViewById(R.id.checkboxUsed);
        unspecCondition = (CheckBox)findViewById(R.id.checkboxUspecified);
        enableNearbySearch = (CheckBox) findViewById(R.id.checkboxNearbySearch);
        milesFrom = (EditText) findViewById(R.id.editTextMilesFrom);
        radioCurrent = (RadioButton) findViewById(R.id.radioCurrentLocation);
        radioZipCode = (RadioButton) findViewById(R.id.radioZipCode);
        zipcodeEditText = (EditText)findViewById(R.id.autocompleteZipcode);
        currentLinearLayout = (LinearLayout)findViewById(R.id.linearLayoutcurrentLocation);
        linearLayoutZip = (LinearLayout) findViewById(R.id.linearLayoutZipcode);
        errorZip = (TextView)findViewById(R.id.textViewZipCodeError);
        tvKeywordError = (TextView)findViewById(R.id.textViewKeywordError);

        keyword = keywordEdit.getText().toString();
        if(keyword.length() == 0 || keyword.trim().length() == 0) {
            tvKeywordError.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please fix all fields with errors", Toast.LENGTH_LONG).show();
            isValid = false;
        }else{
            tvKeywordError.setVisibility(View.GONE);
            isValid = true;
        }

        String option = spinner.getSelectedItem().toString();
        String categoryCode = null;
        switch(option){
            case "All":
                categoryCode = "AllCategories";
                break;
            case "Art":
                categoryCode = "550";
                break;
            case "Baby":
                categoryCode = "2984";
                break;
            case "Books":
                categoryCode = "267";
                break;
            case "Clothing, Shoes and Accessories":
                categoryCode = "11450";
                break;
            case "Computers/Tablets and Networking":
                categoryCode = "58058";
                break;
            case "Health and Beauty":
                categoryCode = "26395";
                break;
            case "Music":
                categoryCode = "11233";
                break;
            case "Video Games and Consoles":
                categoryCode = "1249";
                break;
            default:
                Log.d(TAG, "Entered default option for category");
                break;

        }

        Boolean localBool = localPickup.isChecked();
        Boolean freeBool = freeShipping.isChecked();
        Boolean newConditionBool = newCondition.isChecked();
        Boolean usedCOditionBool = usedCondition.isChecked();
        Boolean unspecConditionBool = unspecCondition.isChecked();
        Boolean nearbySearchBool = enableNearbySearch.isChecked();


        String miles = "0";
        String zipCode = "0";
        if (nearbySearchBool == true){

            if(milesFrom.getText().toString().length() == 0)
                miles = "10";
            else{
                miles = milesFrom.getText().toString();
            }


            if(radioCurrent.isChecked()) {
                if(locationApiCall != null) {
                    zipCode = locationApiCall;
                    loctionReceived = true;
                }
                else {
                    isValid = false;
                    loctionReceived = false;
                    Toast.makeText(this, "Location not obtained from api call", Toast.LENGTH_SHORT).show();
                }
            }else{
                String zipcodeText = zipcodeEditText.getText().toString();
                if(zipcodeText.length() < 5 || !zipcodeText.matches("[0-9]+")){
                    errorZip.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please fix all fields with errors", Toast.LENGTH_LONG).show();
                    isValid = false;
                }else{
                    isValid = true;
                    zipCode = zipcodeEditText.getText().toString();
                    errorZip.setVisibility(View.GONE);
                }
            }
        }

        Log.d("isValidWithoutKeyword", isValid.toString());
        if(isValid){
            String apiCall = "?keyword=" + keyword;
            apiCall += "&category=" + categoryCode;
            apiCall += "&zipCodeText=" + zipCode;
            apiCall += "&distance=" + miles;
            apiCall += "&freeShipping=" + freeBool;
            apiCall += "&localPickUp=" + localBool;
            apiCall += "&conditionNew=" + newConditionBool;
            apiCall += "&conditionUsed=" + usedCOditionBool;
            apiCall += "&conditionUnspecified=" + unspecConditionBool;
            Log.d("FirstApiCall", apiCall);

            Intent intent = new Intent(getApplicationContext(), ResultTabActivity.class);
            intent.putExtra(PASSAPICALL, apiCall);
            Log.d("AfterPutSTring", apiCall);
            startActivity(intent);
        }else{
            if(loctionReceived == true)
                Toast.makeText(this, "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
        }

    }

    public void resetForm(View view){
        Toast.makeText(getApplicationContext(), "In clear button", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void enableNearbySearch(View view){

        keywordEdit = findViewById(R.id.editTextKeyword);
        spinner = findViewById(R.id.dropdownCategory);
        localPickup = (CheckBox)findViewById(R.id.checkboxLocalPickup);
        freeShipping = (CheckBox) findViewById(R.id.checkboxFreeShipping);
        newCondition = (CheckBox) findViewById(R.id.checkboxNew);
        usedCondition = (CheckBox)findViewById(R.id.checkboxUsed);
        unspecCondition = (CheckBox)findViewById(R.id.checkboxUspecified);
        enableNearbySearch = (CheckBox) findViewById(R.id.checkboxNearbySearch);
        milesFrom = (EditText) findViewById(R.id.editTextMilesFrom);
        radioCurrent = (RadioButton) findViewById(R.id.radioCurrentLocation);
        radioZipCode = (RadioButton) findViewById(R.id.radioZipCode);
        zipcodeEditText = (EditText)findViewById(R.id.autocompleteZipcode);
        currentLinearLayout = (LinearLayout)findViewById(R.id.linearLayoutcurrentLocation);
        linearLayoutZip = (LinearLayout) findViewById(R.id.linearLayoutZipcode);
        errorZip = (TextView)findViewById(R.id.textViewZipCodeError);
        tvKeywordError = (TextView)findViewById(R.id.textViewKeywordError);

        if(enableNearbySearch.isChecked()) {
            Log.d(TAG, "Enable nearby search checkbox checked");
            milesFrom.setVisibility(View.VISIBLE);
            radioCurrent.setChecked(true);
            errorZip.setVisibility(View.GONE);
            currentLinearLayout.setVisibility(View.VISIBLE);
            linearLayoutZip.setVisibility(View.VISIBLE);
        }
        else{
            radioCurrent.setChecked(true);
            radioZipCode.setChecked(false);
            milesFrom.setText("");
            zipcodeEditText.setText("");
            zipcodeEditText.setFocusable(false);
            Log.d(TAG, "Enble nearby search checkbox unchecked");
            milesFrom.setVisibility(View.GONE);
            errorZip.setVisibility(View.GONE);
            currentLinearLayout.setVisibility(View.GONE);
            linearLayoutZip.setVisibility(View.GONE);
        }

    }

    public void clearButtonClicked(View view){

        keywordEdit.setText("");
        spinner.setSelection(0);
        //localPickup.setChecked(false);
        //freeShipping.setChecked(false);
        //newCondition.setChecked(false);
        //usedCondition.setChecked(false);
        //unspecCondition.setChecked(false);
        enableNearbySearch.setChecked(false);
        milesFrom.setText("");
        zipcodeEditText.setText("");
        radioCurrent.setChecked(true);
        radioZipCode.setChecked(false);
        milesFrom.setVisibility(View.GONE);
        currentLinearLayout.setVisibility(View.GONE);
        linearLayoutZip.setVisibility(View.GONE);
        errorZip.setVisibility(View.GONE);
        tvKeywordError.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();
        errorZip = (TextView)findViewById(R.id.textViewZipCodeError);
        errorZip.setVisibility(View.GONE);
        tvKeywordError = (TextView)findViewById(R.id.textViewKeywordError);
        tvKeywordError.setVisibility(View.GONE);
    }

}
