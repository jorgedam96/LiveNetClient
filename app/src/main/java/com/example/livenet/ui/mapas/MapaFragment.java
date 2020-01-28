package com.example.livenet.ui.mapas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.livenet.model.LoginBody;
import com.example.livenet.model.Usuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final int LOCATION_REQUEST_CODE = 1;
    private View root;
    private boolean permisos;
    private boolean hayLoc = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mPosicion;
    private boolean acercarZoom = true;
    private Location anterior;
    private Location ultima;
    private CardView btnCam;
    private Handler handler;
    private Runnable runnable;
    private LocalizacionesRest locRest;
    private Timer timer;
    private String aliasLogeado;

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
        mPosicion = LocationServices.getFusedLocationProviderClient(root.getContext());

        inicializarBotonCam();

        locRest = APIUtils.getLocService();

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

        listenerLocalizacion();
        listenerBotonLocGoogle();

        mMap.setMyLocationEnabled(true);
        configurarIUMapa();

        //marcardorConCara();
        activarHiloUbicacionesRest();


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


            }
        });

    }

    private void acercarCamara(Location location) {
        if (acercarZoom) {
            CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 17);
            mMap.animateCamera(cam);
            hayLoc = true;
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    private void configurarIUMapa() {

        mMap.setOnMarkerClickListener(this);
        if (permisos) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(root.getContext(), R.raw.estilo_mapa));


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
                                Log.e("timer", Objects.requireNonNull(e.getMessage()));
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
                        new java.util.Date()));

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
            amigos.add("jeje");

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
        mMap.clear();
        for (int i = 0; i < localizaciones.size(); i++) {
            if (!aliasLogeado.equals(localizaciones.get(i).getAlias())) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                        .title(localizaciones.get(i).getAlias())
                        .snippet(localizaciones.get(i).getFecha_hora().toString())
                );

                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(localizaciones.get(i).getLatitud(), localizaciones.get(i).getLongitud()))
                        .radius(20)
                        .fillColor(Color.argb(60, 150, 226, 255))
                        .strokeColor(Color.argb(100, 150, 226, 255)));

            }
        }


    }


    private void marcardorConCara(LatLng latLng) {


        MarkerOptions options = new MarkerOptions().position(latLng);
        Bitmap bitmap = createUserBitmap();
        if (bitmap != null) {
            options.title("Ketan Ramani");
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            options.anchor(0.5f, 0.907f);
            Marker marker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
    }

    private Bitmap createUserBitmap() {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
