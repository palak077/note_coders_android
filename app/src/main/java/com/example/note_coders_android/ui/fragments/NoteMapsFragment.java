package com.example.note_coders_android.ui.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.example.note_coders_android.R;
import com.example.note_coders_android.data.entities.Note;
import com.example.note_coders_android.databinding.FragmentNoteMapsBinding;
import com.example.note_coders_android.ui.base.BaseFragment;
import com.example.note_coders_android.ui.viewModels.NoteViewModel;

public class NoteMapsFragment extends BaseFragment<FragmentNoteMapsBinding, NoteViewModel> {

    private GoogleMap mGoogleMap;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            if (getArguments() != null) {
                /// Getting Arguments of Edit Note .. Here we are getting those selected Note lat and long
                mGoogleMap.clear();
                double latitude = getArguments().getDouble("latitude");
                double longitude = getArguments().getDouble("longitude");

                addMarker(latitude, longitude, true);

            } else {
                /// OBSERVING Notes to add every marker of Locations
                viewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
                    mGoogleMap.clear();
                    for (Note note : notes) {
                        Location location = note.getLocation();
                        if (location != null) {
                            addMarker(location.getLatitude(), location.getLongitude(), true);
                        }
                    }
                });
            }
        }
    };

    @Override
    public FragmentNoteMapsBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentNoteMapsBinding.inflate(inflater, container, false);
    }

    @Override
    public Class<NoteViewModel> getViewModel() {
        return NoteViewModel.class;
    }

    @Override
    public void setupTheme() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void setupClickListeners() {
    }

    private void addMarker(double latitudeValue, double longitudeValue, boolean moveCamera) {
        LatLng userNote = new LatLng(latitudeValue, longitudeValue);
        String address = getCurrentLocationName(latitudeValue, longitudeValue);

        /// TODO: Adding Marker in MAP here by Latitude and Longitude
        mGoogleMap.addMarker(new MarkerOptions().position(userNote).title(address));
        if (moveCamera) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(userNote));
        }
    }

    private String getCurrentLocationName(double latitudeValue, double longitudeValue) {
        String address;
        Geocoder geocoder = null;

        try {
            geocoder = new Geocoder(requireContext(), Locale.getDefault());
        } catch (Exception e) {
            Log.d("main", Objects.requireNonNull(e.getMessage()));
        }

        if (geocoder != null) {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latitudeValue, longitudeValue, 1);
                ArrayList<String> addressesLine = new ArrayList<>();

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    addressesLine.add(addresses.get(0).getAddressLine(i));
                }

                address = TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")), addressesLine);
                return address;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}