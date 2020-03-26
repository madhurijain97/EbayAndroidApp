package com.example.ebayapp;

public class SimilarItems {

    private String title, shipping, daysLeft, thumbnail, viewItemURL;
    private Float price;

    public SimilarItems(){

    }



    public SimilarItems(String title, String shipping, String daysLeft, Float price,
                        String thumbnail, String viewItemURL) {
        this.title = title;
        this.shipping = shipping;
        this.daysLeft = daysLeft;
        this.price = price;
        this.thumbnail = thumbnail;
        this.viewItemURL = viewItemURL;
    }

    public String getViewItemURL() {
        return viewItemURL;
    }

    public void setViewItemURL(String viewItemURL) {
        this.viewItemURL = viewItemURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(String daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
