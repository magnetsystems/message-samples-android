package com.magnet.smartshopper.walmart.model;



public class Items {

  private String name;
  private String salePrice;
  private String thumbnailImage;
  private String itemId;

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

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }
}
