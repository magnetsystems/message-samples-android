package com.magnet.smartshopper.walmart.model;


public class SearchResponseObject {

  private String query;
  private String sort;
  private String responseGroup;
  private Integer totalResults;
  private Integer start;
  private Integer numItems;
  private java.util.List<Items> items;
  private java.util.List<String> facets;

  public String getQuery() {
    return query;
  }
  public void setQuery(String value) {
    this.query = value;
  }

  public String getSort() {
    return sort;
  }
  public void setSort(String value) {
    this.sort = value;
  }

  public String getResponseGroup() {
    return responseGroup;
  }
  public void setResponseGroup(String value) {
    this.responseGroup = value;
  }

  public Integer getTotalResults() {
    return totalResults;
  }
  public void setTotalResults(Integer value) {
    this.totalResults = value;
  }

  public Integer getStart() {
    return start;
  }
  public void setStart(Integer value) {
    this.start = value;
  }

  public Integer getNumItems() {
    return numItems;
  }
  public void setNumItems(Integer value) {
    this.numItems = value;
  }

  public java.util.List<Items> getItems() {
    return items;
  }
  public void setItems(java.util.List<Items> value) {
    this.items = value;
  }

  public java.util.List<String> getFacets() {
    return facets;
  }
  public void setFacets(java.util.List<String> value) {
    this.facets = value;
  }

}
