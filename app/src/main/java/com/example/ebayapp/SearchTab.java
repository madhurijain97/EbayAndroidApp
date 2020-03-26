package com.example.ebayapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.example.ebayapp.MainActivity.SERVER_TO_HIT;

public class SearchTab extends Fragment implements View.OnClickListener {
    public static final String TAG = "SearchTabFragment";
    View rootView;
    RadioButton radioCurrent, radioZip;

    private OnFragmentInteractionListener mListener;

    public SearchTab() {
        // Required empty public constructor
    }
    public static SearchTab newInstance(String param1, String param2) {
        SearchTab fragment = new SearchTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = layoutInflater.inflate(R.layout.fragment_search_tab, container, false);
        Spinner spinnerCategory = view.findViewById(R.id.dropdownCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.categoryOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = layoutInflater.inflate(R.layout.fragment_search_tab, container, false);
        Button clearButton = (Button) rootView.findViewById(R.id.buttonClear);

        final AutoCompleteTextView zipcode = rootView.findViewById(R.id.autocompleteZipcode);
        zipcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_UP) ) {
                    String zipcodeAutocompleteApiCall = SERVER_TO_HIT + "autocomplete?zipSoFar=" + ((EditText)rootView.findViewById(R.id.autocompleteZipcode)).getText().toString();
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest zipcodeRequest = new StringRequest(Request.Method.GET, zipcodeAutocompleteApiCall, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            Log.d("zipcode", response);

                            List<String> postalCodeList = new ArrayList<>();
                            try {
                                JSONArray postalCodes = new JSONArray(response);
                                for (int i=0 ; i<postalCodes.length(); i++) {
                                    postalCodeList.add(postalCodes.getJSONObject(i).getString("postalCode"));
                                }
                            } catch (Exception e) {
                                // do nothing
                            }

                            String[] zipCodes = postalCodeList.toArray(new String[0]);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getActivity(), android.R.layout.select_dialog_item, zipCodes);

                            zipcode.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "onErrorResponse when zipcode autocomplete API called", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(zipcodeRequest);

                    return true;
                }
                return false;

            }
        });

        radioCurrent = rootView.findViewById(R.id.radioCurrentLocation);
        radioCurrent.setOnClickListener(radio_listener);
        radioZip = rootView.findViewById(R.id.radioZipCode);
        radioZip.setOnClickListener(radio_listener);

        return rootView;
    }

    View.OnClickListener radio_listener = new View.OnClickListener(){
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.radioCurrentLocation:
                    radioZip.setChecked(false);
                    AutoCompleteTextView zipcodeEdit = rootView.findViewById(R.id.autocompleteZipcode);
                    zipcodeEdit.setFocusable(false);
                    break;
                case R.id.radioZipCode:
                    radioCurrent.setChecked(false);
                    AutoCompleteTextView zipcodeEdit2 = rootView.findViewById(R.id.autocompleteZipcode);
                    zipcodeEdit2.setFocusableInTouchMode(true);
                    break;
                default:
                    radioCurrent.setChecked(true);
                    radioZip.setChecked(false);
                    AutoCompleteTextView zipcodeEdit3 = rootView.findViewById(R.id.autocompleteZipcode);
                    zipcodeEdit3.setFocusable(false);
                    break;
            }
        }
    };

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

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch(view.getId()){
            case R.id.buttonClear:
                Log.d(TAG,"Clear button clicked i switch case");
                break;
            default:
                Log.d(TAG,"Entered in defaultImage switch case");
                break;
        }
    }

    public void replaceFragment(Fragment newFragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
