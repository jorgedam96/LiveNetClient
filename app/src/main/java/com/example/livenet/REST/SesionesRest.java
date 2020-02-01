package com.example.livenet.REST;

import com.example.livenet.model.Sesion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SesionesRest {
    @GET("sesion/{token}")
    Call<Integer> findByToken(@Path("token") String token);

    @POST("insertsesion")
    Call<Sesion> insertsesion(@Body Sesion sesionBody);

    @GET("cerrarsesion/{token}")
    Call<String> borrarSesion(@Path("token") String token);
}
