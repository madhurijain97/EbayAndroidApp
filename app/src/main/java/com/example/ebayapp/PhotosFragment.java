package com.example.ebayapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.ebayapp.MainActivity.SERVER_TO_HIT;


public class PhotosFragment extends Fragment {
    public static final String TAG = "PhotosFragment";
    public static final int numberOfPhotos = 8;
    LinearLayout linearLayout, linearLayoutPhotos;
    LayoutInflater layoutInflater;
    JSONObject responseJson;

    ProgressBar progressBar;
    TextView tvPbar;
    private OnFragmentInteractionListener mListener;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_photos, container, false);
        linearLayoutPhotos = linearLayout.findViewById(R.id.linearLayoutPhotos);
        layoutInflater = LayoutInflater.from(this.getContext());

        progressBar = linearLayout.findViewById(R.id.progressbarPhotos);
        progressBar.setVisibility(View.VISIBLE);

        tvPbar = linearLayout.findViewById(R.id.progressProductTitlePhotos);
        tvPbar.setVisibility(View.VISIBLE);

        String responseJson = getArguments().getString("individualItemString");
        Log.d("responseJsonPhotos", responseJson);

        String previousResponse = getArguments().getString("previousResponse");

        JSONObject jsonObject = null;
        String titleForPhotos = null;

        try {
            /*jsonObject = new JSONObject(responseJson);
            jsonObject = jsonObject.getJSONObject("Item");
            titleForPhotos = jsonObject.getString("Title");*/
            jsonObject = new JSONObject(previousResponse);
            titleForPhotos = jsonObject.getJSONArray("title").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String photosApiCall = SERVER_TO_HIT + "photosTab?keyword=" + titleForPhotos;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                photosApiCall, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                jsonParse(response,linearLayout, linearLayoutPhotos, layoutInflater);
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
        return linearLayout;
    }

    public void jsonParse(JSONArray response, LinearLayout linearLayout, LinearLayout linearLayoutPhotos,
                          LayoutInflater layoutInflater){
        JSONObject jsonObject;
        Log.d("responseInPhotosTab",response.toString());

        try{
            JSONArray jsonArray = new JSONArray(response.toString());
            RelativeLayout relativeLayoutError = linearLayout.findViewById(R.id.photosError);
            if(jsonArray.length() == 0)
                relativeLayoutError.setVisibility(View.VISIBLE);
            else {
                relativeLayoutError.setVisibility(View.GONE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    View view = layoutInflater.inflate(R.layout.photos_item, linearLayoutPhotos, false);
                    ImageView imageView = view.findViewById(R.id.imageViewPhotos);

                    jsonObject = response.getJSONObject(i);
                    String link;
                    if(jsonObject.has("link")) {
                        link = jsonObject.getString("link");
                        Picasso.get().load(link).into(imageView);
                    }
                    linearLayoutPhotos.addView(view);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public void getArgumentForPhotos(){
        String responseJson = getArguments().getString("individualItemString");
        Log.d("getArgumentForPhotos", responseJson.toString());
    }

}