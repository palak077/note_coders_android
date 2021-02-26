package com.example.note_coders_android.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.NavOptions;

import note.coders.android.R;
import note.coders.android.databinding.FragmentSplashBinding;
import note.coders.android.ui.base.BaseFragment;
import note.coders.android.ui.viewModels.NoteViewModel;

public class SplashFragment extends BaseFragment<FragmentSplashBinding, NoteViewModel> {

    @Override
    public FragmentSplashBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSplashBinding.inflate(inflater, container, false);
    }

    @Override
    public Class<NoteViewModel> getViewModel() {
        return NoteViewModel.class;
    }

    @Override
    public void setupTheme() {
    }

    @Override
    public void setupClickListeners() {
    }

    @Override
    public void onResume() {
        super.onResume();
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(R.id.splashFragment, true)
                .build();

        ///// CHECKING HERE USER LOGIN OR NOT
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        boolean loginUser = sharedPref.getBoolean("login_user", false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (loginUser) {
                navController.navigate(R.id.action_global_homeFragment, null, navOptions);
            } else {
                navController.navigate(R.id.action_splashFragment_to_welcomeFragment, null, navOptions);
            }
        }, 1500);

    }
}