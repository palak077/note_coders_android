package com.example.note_coders_android.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import note.coders.android.R;
import note.coders.android.databinding.ActivityMainBinding;
import note.coders.android.databinding.ToolbarLayoutBinding;
import note.coders.android.ui.base.BaseActivity;
import note.coders.android.ui.interfaces.AlertDialogCallback;
import note.coders.android.ui.viewModels.NoteViewModel;
import note.coders.android.utils.Utils;

public class MainActivity extends BaseActivity<ActivityMainBinding, NoteViewModel> implements AlertDialogCallback {

    private int currentFragment = -1;
    private final int HOME_FRAGMENT = 1;
    public static Location location;
    public static ToolbarLayoutBinding toolbarLayout;
    public static SearchView searchView;
    private LocationManager locationManager;

    @Override
    public ActivityMainBinding getActivityBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    //we do not create new view model but instead ask for the already existing instance
    @Override
    public Class<NoteViewModel> getViewModel() {
        return NoteViewModel.class;
    }

    @Override
    public void setupTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbarLayout = binding.toolbarLayout;
        searchView = binding.toolbarLayout.searchViewToolbar;

        setupNavigation();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListenerGPS);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        isLocationEnabled();
    }

    @Override
    public void setupClickListeners() {
    }

    private void setupNavigation() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_all_notes || destination.getId() == R.id.navigation_notes_on_map || destination.getId() == R.id.addOrEditNoteFragment) {

                if (destination.getId() == R.id.addOrEditNoteFragment) {
                    binding.navView.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    binding.navView.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                getSupportActionBar().show();

                if (destination.getId() == R.id.navigation_all_notes) {
                    binding.toolbarLayout.searchViewToolbar.setVisibility(View.VISIBLE);
                    binding.toolbarLayout.toolbarTitle.setVisibility(View.GONE);
                } else {
                    binding.toolbarLayout.searchViewToolbar.setVisibility(View.GONE);
                    binding.toolbarLayout.toolbarTitle.setVisibility(View.VISIBLE);
                }

                if (destination.getId() == R.id.navigation_all_notes) {
                    currentFragment = HOME_FRAGMENT;
                    return;
                }

            } else {
                binding.navView.setVisibility(View.GONE);
                getSupportActionBar().hide();
            }

            currentFragment = -1;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == HOME_FRAGMENT) {
            Utils.showAlertDialog(this, getString(R.string.exit_app), getString(R.string.exit_app_body), this);
        } else {
            super.onBackPressed();
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkLocationPermission())
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else {
                    location = null;
                }
                return;
            }
        }
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            MainActivity.location = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private void isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.enable_location_title));
            alertDialog.setMessage(getString(R.string.location_not_enabled));
            alertDialog.setPositiveButton(getString(R.string.location_settings), (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            alertDialog.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }


    @Override
    public void onNegativeButtonClick(DialogInterface dialog) {
        dialog.dismiss();
    }

    @Override
    public void onPositiveButtonClick() {
        finishAndRemoveTask();
    }

}