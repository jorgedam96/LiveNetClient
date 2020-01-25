package com.example.livenet.REST;

import com.example.livenet.model.Mensaje;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface MensajesRest {

    @GET("/mensaje/recibir/{destino}")
    Call<List<Mensaje>> findByDestino(@Path("destino") String destino);

    @POST("/mensaje/enviar")
    Call<Mensaje> create(@Body Mensaje mensaje);
}
