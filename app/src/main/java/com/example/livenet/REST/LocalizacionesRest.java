package com.example.livenet.REST;

import com.example.livenet.model.Localizacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LocalizacionesRest {

    //------------Localizaciones------------------
    @GET("localizaciones/")
    Call<List<Localizacion>> findAll();

    @GET("localizaciones/{alias}")
    Call<Localizacion> findByAlias(@Path("alias") String alias);

    @PUT("actualizarLoc")
    Call<Localizacion> create(@Body Localizacion loc);

    @DELETE("localizaciones/{alias}")
    Call<Localizacion> delete(@Path("alias") String alias);

    @PUT("localizaciones/{alias}")
    Call<Localizacion> update( @Body Localizacion localizacion);

    @POST("locamigos")
    Call<List<Localizacion>> findAllByAmigos(@Body List<String> amigos);
}
