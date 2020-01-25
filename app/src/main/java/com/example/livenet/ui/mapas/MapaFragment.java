package com.example.livenet.ui.mapas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.livenet.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private View root;
    private boolean permisos;
    private boolean hayLoc = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mPosicion;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
               // Toast.makeText(root.getContext(), location.toString(), Toast.LENGTH_SHORT).show();

                acercarCamara(location);
            }
        });
        mMap.setMyLocationEnabled(true);

        configurarIUMapa();


       /* mGoogleApiClient = new GoogleApiClient.Builder(root.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Crear el LocationRequest
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 segundos en milisegundos
                .setFastestInterval(1000); // 1 segundo en milisegundos
*/

    }

    private void acercarCamara(Location location) {
        if (!hayLoc) {
            CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 20);
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
        Toast.makeText(root.getContext(), location.toString(), Toast.LENGTH_SHORT).show();

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

}
