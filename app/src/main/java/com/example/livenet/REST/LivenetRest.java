package com.example.livenet.REST;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface LivenetRest {


    @GET("localizaciones/")
    Call<List<Localizacion>> findAll();

    @POST("actualizarloc")
    Call<Localizacion> create(@Body Localizacion loc);

    @DELETE("localizaciones/{alias}")
    Call<Localizacion> delete(@Path("alias")String alias);

    @GET()
}
