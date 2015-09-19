package com.magnet.smartshopper.walmart;

import com.magnet.smartshopper.walmart.model.SearchResponseObject;
import retrofit.http.*;

public interface SearchItemServiceClient {

  /**
   * 
   * GET /getitemservice/v1/search
   * @param query style:Query optional:false
   * @param format style:Query optional:false
   * @param apiKey style:Query optional:false
   */
  @GET("/search")
  SearchResponseObject search(
          @Query("query") String query,
          @Query("format") String format,
          @Query("apiKey") String apiKey);

}
