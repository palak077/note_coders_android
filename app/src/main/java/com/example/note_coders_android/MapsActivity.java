package com.example.note_coders_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    boolean mLocationPermissionGranted = false;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private GoogleApiClient googleApiClient;
    private Location mLastKnownLocation;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Location userLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    boolean isEdit = false;
    String completeAddress = "";
    Geocoder geocoder;
    List<Address> addresses;
    Location currentLocation;
    LatLng objLatLng = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        objLatLng = getIntent().getExtras().getParcelable("Latlng");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.CANADA);
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng temp = null;
        if (objLatLng != null) {
            temp = new LatLng(objLatLng.latitude, objLatLng.longitude);
         //   mMap.addMarker(new MarkerOptions().position(temp)
           //         .title(getAddressTitle(temp)));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));

        }
        else {
            temp = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        MarkerOptions markerOptions = new MarkerOptions().position(temp)
                .title(getAddressTitle(temp));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(temp));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
        mMap.addMarker(markerOptions);

    }

    public String getAddressTitle(LatLng location) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            completeAddress = completeAddress + addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getCountryName();

        } catch (IOException e) {

        }
        return completeAddress;

    }
}


