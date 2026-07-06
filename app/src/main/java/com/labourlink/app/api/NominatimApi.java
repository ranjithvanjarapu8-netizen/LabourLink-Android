package com.labourlink.app.api;

import com.labourlink.app.models.LocationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NominatimApi {

    @GET("search")
    Call<List<LocationResponse>> searchLocation(

            @Header("User-Agent") String userAgent,

            @Query("q") String location,

            @Query("format") String format,

            @Query("limit") int limit

    );

}