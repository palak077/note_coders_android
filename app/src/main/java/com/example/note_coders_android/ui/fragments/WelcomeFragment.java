package com.example.note_coders_android.ui.fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import note.coders.android.R;
import note.coders.android.databinding.FragmentWelcomeBinding;
import note.coders.android.ui.base.BaseFragment;
import note.coders.android.ui.viewModels.NoteViewModel;

public class WelcomeFragment extends BaseFragment<FragmentWelcomeBinding, NoteViewModel> {

    @Override
    public FragmentWelcomeBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWelcomeBinding.inflate(inflater, container, false);
    }

    @Override
    public Class<NoteViewModel> getViewModel() {
        return NoteViewModel.class;
    }

    @Override
    public void setupTheme() { }

    @Override
    public void setupClickListeners() {
        binding.signInBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_welcomeFragment_to_signInFragment);
        });
        binding.signUpBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_welcomeFragment_to_signUpFragment);
        });
    }


}