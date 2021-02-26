package com.example.note_coders_android.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;

import note.coders.android.R;

public abstract class BaseActivity<viewBinding extends ViewBinding, viewModel extends ViewModel> extends AppCompatActivity {

    protected viewBinding binding;
    protected viewModel viewModel;
    protected NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getActivityBinding(getLayoutInflater());
        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(getViewModel());
        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        setupTheme();
        setupClickListeners();
    }

    public abstract viewBinding getActivityBinding(LayoutInflater inflater);

    public abstract Class<viewModel> getViewModel();

    public abstract void setupTheme();

    public abstract void setupClickListeners();

}