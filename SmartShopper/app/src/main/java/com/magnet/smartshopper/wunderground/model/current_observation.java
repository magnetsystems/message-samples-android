
package com.magnet.smartshopper.wunderground.model;




public class current_observation {

  
  
  private Float temp_f;
  private String icon_url;

  
  private Float temp_c;

  public Float getTemp_f() {
    return temp_f;
  }
  public void setTemp_f(Float value) {
    this.temp_f = value;
  }

  public Float getTemp_c() {
    return temp_c;
  }
  public void setTemp_c(Float value) {
    this.temp_c = value;
  }

  public String getIcon_url() {
    return icon_url;
  }

  public void setIcon_url(String icon_url) {
    this.icon_url = icon_url;
  }
}
