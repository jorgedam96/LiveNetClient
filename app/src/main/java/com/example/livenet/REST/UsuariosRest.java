package com.example.livenet.REST;

import com.example.livenet.model.Localizacion;
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;

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

public interface UsuariosRest {


 //------------Usuarios------------------
    @GET("usuarios/")
    Call<List<Usuario>> findAll();

    @GET("usuarios/{alias}")
    Call<Usuario> findByAlias(@Path("alias") String alias);

    @POST("usuarios/login")
    Call<Usuario> login(@Body LoginBody loginBody);


    @POST("insertar_usuario")
    Call<Usuario> create(@Body Usuario usuario);

    @DELETE("usuarios/{alias}")
    Call<Usuario> delete(@Path("alias") String alias);

    @PUT("usuarios/{alias}")
    Call<Usuario> update(@Path("alias") String alias, @Body Usuario usuario);


}
