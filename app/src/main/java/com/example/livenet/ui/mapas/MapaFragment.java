package com.example.livenet.ui.mapas;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.BBDD.DBC;
import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.LocalizacionesRest;
import com.example.livenet.REST.UsuariosRest;
import com.example.livenet.model.FireUser;
import com.example.livenet.model.Localizacion;
import com.example.livenet.model.Usuario;
import com.example.livenet.util.MyB64;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MapaFragment extends Fragment implements OnMapReadyCallback {


    private View root;
    private GoogleMap mMap;
    private boolean acercarZoom = true;
    private Location anterior;
    private Location ultima;
    private CardView btnCam;
    private CardView btnVoice;
    private Handler handler;
    private Runnable runnable;
    private LocalizacionesRest locRest;
    private Timer timer;
    private String aliasLogeado = "";
    private Marker miMarker;
    private ArrayList<Marker> marcadores = new ArrayList<>();
    private DBC dbc;
    Bitmap b = null;
    private boolean foto;
    private HashMap<String, Bitmap> marcadoresNombre;
    private int contador = 0;
    private boolean buscarFoto = true;
    private boolean camaraUnaVez = true;
    private int tiempoTimer = 100;
    private TextToSpeech textToSpeech;
    private final int REQUEST_CODE_RECONOCIMIENTO = 23;
    private List<Localizacion> listaLocs;
    private CameraUpdate cam;


    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        aliasLogeado = ((MainActivity) getActivity()).getLogged().getAlias();
        marcadoresNombre = new HashMap<String, Bitmap>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        root = inflater.inflate(R.layout.fragment_mapa, container, false);
        try {
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            FragmentManager frm = getActivity().getSupportFragmentManager();

            frm.beginTransaction().replace(R.id.mapContainer, supportMapFragment).commit();
            supportMapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e("mapa", e.getMessage());
        }

        inicializarBotonCam();
        inicializarBotonVoz();

        locRest = APIUtils.getLocService();

        return root;
    }

    private void inicializarBotonVoz() {
        btnVoice = root.findViewById(R.id.btnVoz);
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(root.getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        //reconoce en el idioma del telefono
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cómo quiere ordenar la lista?");
                        try {
                            startActivityForResult(intent, REQUEST_CODE_RECONOCIMIENTO);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
    }

    private void inicializarBotonCam() {
        btnCam = root.findViewById(R.id.btnCamaraMapa);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acercarZoom = false;
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {


                listenerLocalizacion();
                listenerBotonLocGoogle();

                mMap.setMyLocationEnabled(true);
                configurarIUMapa();

                //marcardorConCara();
                activarHiloUbicacionesRest();

            }
        });
    }


    private void listenerBotonLocGoogle() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                acercarZoom = true;
                acercarCamara(ultima);
                return false;
            }
        });
    }

    private void listenerLocalizacion() {
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (miMarker != null) {
                    miMarker.remove();
                }


                miMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), marcadoresNombre.get(aliasLogeado))))
                        .title("Tú"));

                ultima = location;
                if (camaraUnaVez) {
                    acercarCamara(location);
                    camaraUnaVez = false;
                }
                // Toast.makeText(root.getContext(), location.toString(), Toast.LENGTH_SHORT).show();
                //acercarCamara(location);

                //ponerMiMarcador(location);
              /*  if (miMarker == null) {

                    miMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(ultima.getLatitude(), ultima.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), marcadoresNombre.get(aliasLogeado))))
                            .title("Tú"));
                } else {
                    animateMarker(location.getLatitude(), location.getLongitude(), miMarker);
                }
*/

            }
        });

    }

    private void ponerMiMarcador(Location l) {
        if (miMarker != null) {
            miMarker.remove();
        }
        miMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(ultima.getLatitude(), ultima.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), marcadoresNombre.get(aliasLogeado))))
                .title("Tú"));
    }

    private void acercarCamara(Location location) {
        if (acercarZoom) {
            cam = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 17);
            mMap.animateCamera(cam);
        }

    }


    private void configurarIUMapa() {

        //mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(root.getContext(), R.raw.estilo_mapa));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(false);
        mMap.setTrafficEnabled(false);


    }

    private void activarHiloUbicacionesRest() {
        try {

            timer = new Timer();
            TimerTask doAsyncTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Objects.requireNonNull(getActivity()).runOnUiThread(runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                        enviarUbicacion();
                                        solicitarUbicacionesRest();
                                        acercarCamara(ultima);

                                    }
                                });

                            } catch (Exception e) {
                                if (e.getMessage() != null)
                                    Log.e("timer", e.getMessage());
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsyncTask, 0, 4000);
        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("timer", e.getMessage());
        }

    }

    private void enviarUbicacion() {
        if (anterior != ultima) {
            try {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                String fechaStr = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(System.currentTimeMillis());
                Call<Localizacion> call = locRest.create(new Localizacion(
                        ((MainActivity) getActivity()).getLogged().getAlias(),
                        ultima.getLatitude(),
                        ultima.getLongitude(),
                        fechaStr,
                        ultima.getAccuracy()));
                Log.i("fecha", fechaStr);


                call.enqueue(new Callback<Localizacion>() {
                    @Override
                    public void onResponse(Call<Localizacion> call, Response<Localizacion> response) {
                        Log.i("enviarUbicacion", response.toString());
                        anterior = ultima;
                    }

                    @Override
                    public void onFailure(Call<Localizacion> call, Throwable t) {
                        Log.e("enviarUbicacion", t.toString());

                    }
                });
            } catch (Exception e) {
                Log.e("enviarUbicacion", e.getMessage());
            }
        }
    }

    private void solicitarUbicacionesRest() {

        try {
            dbc = new DBC(getActivity(), "localCfgBD", null, 1);
            ArrayList<FireUser> fbUser = dbc.seleccionarData();
            dbc.close();
            List<String> amigos = new ArrayList<>();
            buscarFotoUsuarios(aliasLogeado);
            for (FireUser f : fbUser) {
                amigos.add(f.getUsername());
                buscarFotoUsuarios(f.getUsername());
            }
            buscarFoto = false;


            Call<List<Localizacion>> call = locRest.findAllByAmigos(amigos);
            call.enqueue(new Callback<List<Localizacion>>() {
                @Override
                public void onResponse(Call<List<Localizacion>> call, Response<List<Localizacion>> response) {
                    if (response.isSuccessful()) {
                        //hay respuesta
                        Log.i("respuesta locs", response.toString());
                        if (response.code() == 200) {
                            //codigo correcto
                            if (response.body() != null) {
                                recorrerListaLocs(response.body());
                            }
                        } else {
                            Log.e("respuesta locs", String.valueOf(response.code()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Localizacion>> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            Log.e("SolicitarUbicaciones", e.getMessage());
        }
    }

    private void recorrerListaLocs(List<Localizacion> localizaciones) {
        listaLocs = localizaciones;
        try {

            mMap.clear();
            miMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(ultima.getLatitude(), ultima.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), marcadoresNombre.get(aliasLogeado))))
                    .title("Tú"));

            for (int i = 0; i < localizaciones.size(); i++) {
                if (!localizaciones.get(i).getAlias().equals(aliasLogeado)) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), marcadoresNombre.get(localizaciones.get(i).getAlias()))))
                            .title(localizaciones.get(i).getAlias())
                            .snippet(localizaciones.get(i).getFecha_hora()));
                    Log.e("fecha", "fecha: " + localizaciones.get(i).getFecha_hora());
                    mMap.addCircle(new CircleOptions()
                            .center(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                            .radius(localizaciones.get(i).getAccuracy())
                            .fillColor(Color.argb(60, 150, 226, 255))
                            .strokeColor(Color.argb(100, 150, 226, 255)));
                }
            }


        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("recorrer locs", e.getMessage());
        }
    }

    private void buscarFotoUsuarios(String alias) {
        if (buscarFoto) {
            try {
                marcadoresNombre = new HashMap<String, Bitmap>();

                UsuariosRest usuRest = APIUtils.getUsuService();

                Call<Usuario> call = usuRest.findByAlias(alias);
                Log.i("For contador", String.valueOf(contador));

                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        marcadoresNombre.put(alias, MyB64.base64ToBitmap(response.body().getFoto()));

                        Log.i("response contador", String.valueOf(response));
                        Log.i("hashmap", marcadoresNombre.toString());

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                    }
                });


            } catch (
                    Exception e) {
                if (e.getMessage() != null) {
                    Log.e("buscarFoto", e.getMessage());
                }
            }
        }
    }

    public static Bitmap createCustomMarker(Context context, Bitmap resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageBitmap(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    @Override
    public void onDestroy() {
        try {
            handler.removeCallbacksAndMessages(null);
            if (timer != null)
                timer.cancel();
        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("timerDestroy", e.getMessage());
        }
        super.onDestroy();
    }

    public static void animateMarker(double lat, double lon, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(lat, lon);

            //final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(600); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        //marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_CODE_RECONOCIMIENTO) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> voz = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //según la primera ocurrencia de cada palabra llama a rellenarjuegos() con un where
                if (voz != null) {
                    try {

                        for (Localizacion l : listaLocs) {
                            if (l.getAlias() != null) {
                                acercarZoom = false;
                                if (voz.toString().toLowerCase().contains(l.getAlias())) {
                                    contestaspeech(l,l.getAlias());
                                }else if (voz.toString().toLowerCase().contains(aliasLogeado)||voz.toString().toLowerCase().contains("estoy")){
                                    contestaspeech(l,aliasLogeado);
                                }else if(voz.toString().toLowerCase().contains("1 + 1")){

                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("lista locss", "Listaloccsss" + e.getMessage());
                    }
                }

            }
        }
        acercarZoom = true;

    }

    private void contestaspeech(Localizacion l,String alias) {
        textToSpeech.speak("acercando cámara a " + alias, TextToSpeech.QUEUE_FLUSH, null);
        cam = CameraUpdateFactory.newLatLngZoom(
                new LatLng(l.getLatitud(), l.getLongitud()), 20);
        mMap.animateCamera(cam);
    }


    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}

/*


for (int i = 0; i < localizaciones.size(); i++) {
                if (!localizaciones.get(i).getAlias().equals(aliasLogeado)) {
                    marcadores.add(mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), R.drawable.defaultphoto)))
                            .title(localizaciones.get(i).getAlias())
                            .visible(false)));
                }

            }

            for (int j = 0; j < marcadores.size(); j++) {
                marcadores.get(j).setVisible(true);
                animateMarker(localizaciones.get(j).getLatitud(), localizaciones.get(j).getLongitud(), marcadores.get(j));
            }*/
