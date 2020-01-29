package com.example.livenet.ui.mapas;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.livenet.MainActivity;
import com.example.livenet.R;
import com.example.livenet.REST.APIUtils;
import com.example.livenet.REST.LocalizacionesRest;
import com.example.livenet.model.Localizacion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaFragment extends Fragment implements OnMapReadyCallback{


    private View root;
    private GoogleMap mMap;
    private boolean acercarZoom = true;
    private Location anterior;
    private Location ultima;
    private CardView btnCam;
    private Handler handler;
    private Runnable runnable;
    private LocalizacionesRest locRest;
    private Timer timer;
    private String aliasLogeado = "";
    private Marker miMarker;
    private ArrayList<Marker> marcadores = new ArrayList<>();

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        aliasLogeado = ((MainActivity) getActivity()).getLogged().getAlias();
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

        locRest = APIUtils.getLocService();

        ((MainActivity)getActivity()).comprobarAmigos();
        ((MainActivity)getActivity()).callFriends();

        return root;
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
                ultima = location;

                // Toast.makeText(root.getContext(), location.toString(), Toast.LENGTH_SHORT).show();
                acercarCamara(location);

                //ponerMiMarcador(location);
               /* if (miMarker == null) {

                    miMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), R.drawable.defaultphoto)))
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
                .position(new LatLng(l.getLatitude(), l.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), R.drawable.defaultphoto)))
                .title("Tú"));

    }

    private void acercarCamara(Location location) {
        if (acercarZoom) {
            CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(
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
            timer.schedule(doAsyncTask, 0, 5000);
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

                Call<Localizacion> call = locRest.create(new Localizacion(
                        ((MainActivity) getActivity()).getLogged().getAlias(),
                        ultima.getLatitude(),
                        ultima.getLongitude(),
                        new java.util.Date(),ultima.getAccuracy()));

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
            List<String> amigos = new ArrayList<>();
            amigos.add("emilio");
            amigos.add("jorge");

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
        try {
            mMap.clear();
           // ponerMiMarcador(ultima);

            for (int i = 0; i < localizaciones.size(); i++) {
                if (!localizaciones.get(i).getAlias().equals(aliasLogeado)) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(root.getContext(), R.drawable.defaultphoto)))
                            .title(localizaciones.get(i).getAlias()));

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


    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);

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
