package com.example.livenet.REST;

public class APIUtils {

    // IP
    private static final String server = "80.102.104.105";

   // private static final String server = "192.168.0.12";

    // Puerto
    private static final String port = "27015";
    // IP
    private static final String API_URL = "http://" + server + ":" + port + "/";

    private APIUtils() {
    }


    public static LocalizacionesRest getLocService() {
        return RetrofitClient.getClient(API_URL).create(LocalizacionesRest.class);
    }

    public static UsuariosRest getUsuService() {
        return RetrofitClient.getClient(API_URL).create(UsuariosRest.class);
    }

    public static MensajesRest getMessageService(){

        return RetrofitClient.getClient(API_URL).create(MensajesRest.class);
    }

    public static AmigosRest getAmigosService(){

        return RetrofitClient.getClient(API_URL).create(AmigosRest.class);
    }

    public static SesionesRest getSesionesService(){

        return RetrofitClient.getClient(API_URL).create(SesionesRest.class);
    }

}
