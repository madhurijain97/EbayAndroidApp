package com.example.ebayapp;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultCardview {

    public static final String TAG = "ResultCardView";

    private String imgResult;
    private int imgResultCart;
    private String title, zipcode, shipping, condition, itemId, galleryURL;
    private Float price, priceEnter;
    private static int itemCount = -1;
    private int count = 0;

    public ResultCardview() {

    }

    public ResultCardview(String imgResult, int imgResultCart, String title, String zipcode,
                          String shipping, String condition, Float price, String itemId) {
        this.imgResult = imgResult;
        this.imgResultCart = imgResultCart;
        this.title = title;
        this.zipcode = zipcode;
        this.shipping = shipping;
        this.condition = condition;
        this.price = price;
        this.itemId = itemId;
        this.itemCount += 1;
        Log.d(TAG, "Object created");

    }

    public ResultCardview returnNewObject(JSONObject jsonObject) {
        try {
            if(jsonObject.has("itemId"))
                itemId = jsonObject.getJSONArray("itemId").getString(0);
            else
                itemId = "N/A";

            if(jsonObject.has("title"))
                title = jsonObject.getJSONArray("title").getString(0);
            else
                title = "N/A";

            if(jsonObject.has("postalCode"))
                zipcode = jsonObject.getJSONArray("postalCode").getString(0);
            else
                zipcode = "N/A";

            String shippingEnter = "N/A";
            try {
                String shipping = jsonObject.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost")
                        .getJSONObject(0).getString("__value__");
                if (Float.parseFloat(shipping) == 0)
                    shippingEnter = "Free Shipping";
                else if (Float.parseFloat(shipping) > 0)
                    shippingEnter = "$" + (String) shipping;
            } catch (Exception e) {
                shippingEnter = "N/A";
            }

            String conditionEnter;
            try {
                String condition = jsonObject.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                conditionEnter = condition;

            } catch (Exception e) {
                conditionEnter = "N/A";
            }

            try{
                String price = jsonObject.getJSONArray("sellingStatus").getJSONObject(0).
                    getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                priceEnter = Float.parseFloat(price);
            }catch(Exception e){
                priceEnter = Float.parseFloat("0");
            }

            if(jsonObject.has("galleryURL"))
                galleryURL = jsonObject.getJSONArray("galleryURL").getString(0);
            else
                galleryURL = "N/A";

            String check = itemId + ", " + title + ", " + zipcode + ", " + shippingEnter + ", " + conditionEnter + ", " + priceEnter + ", " + galleryURL;
            if(CustomSharedPreference.sharedPreferences.contains(itemId))
                return new ResultCardview(galleryURL, R.drawable.cart_remove, title, zipcode, shippingEnter, conditionEnter, priceEnter, itemId);
            else
                return new ResultCardview(galleryURL, R.drawable.cart_plus, title, zipcode, shippingEnter, conditionEnter, priceEnter, itemId);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getImgResult() {
        return imgResult;
    }

    public void setImgResult(String imgResult) {
        this.imgResult = imgResult;
    }

    public int getImgResultCart() {
        return imgResultCart;
    }

    public void setImgResultCart(int imgResultCart) {
        this.imgResultCart = imgResultCart;
    }

    public String getTitle() {
        return title;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getShipping() {
        return shipping;
    }

    public String getCondition() {
        return condition;
    }

    public Float getPrice() {
        return price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public static int getItemCount() {
        return itemCount;
    }

    public static void setItemCount(int itemCount) {
        ResultCardview.itemCount = itemCount;
    }
}
