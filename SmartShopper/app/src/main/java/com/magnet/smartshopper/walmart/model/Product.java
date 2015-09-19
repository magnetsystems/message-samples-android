package com.magnet.smartshopper.walmart.model;


import java.io.Serializable;

public class Product implements Serializable {

    private String id;
    private String name;
    private String salePrice;
    private String thumbnailImage;

    public String getSalePrice() {
        return salePrice;
    }
    public void setSalePrice(String value) {
        this.salePrice = value;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }
    public void setThumbnailImage(String value) {
        this.thumbnailImage = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
