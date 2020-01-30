package com.example.livenet.REST;

import java.util.ArrayList;


import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AmigosRest {
    @GET("amigos/{alias}")
    Call<ArrayList<String[]>> findAllByAlias(@Path("alias") String alias);

    @POST("agregaramigo")
    Call<String> agregarAmigo (@Body String[] amigos);

    @POST("borraramigo")
    Call<String> borraramigo(@Body String[] amigos);
}
