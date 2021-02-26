package com.example.note_coders_android.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.navigation.NavOptions;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.note_coders_android.R;
import com.example.note_coders_android.data.entities.User;
import com.example.note_coders_android.databinding.FragmentSignInBinding;
import com.example.note_coders_android.ui.base.BaseFragment;
import com.example.note_coders_android.ui.viewModels.UserViewModel;
import com.example.note_coders_android.utils.Utils;

public class SignInFragment extends BaseFragment<FragmentSignInBinding, UserViewModel> {

    @Override
    public FragmentSignInBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSignInBinding.inflate(inflater, container, false);
    }

    @Override
    public Class<UserViewModel> getViewModel() {
        return UserViewModel.class;
    }

    @Override
    public void setupTheme() {
    }

    @Override
    public void setupClickListeners() {
        binding.loginBtn.setOnClickListener(v -> checkEmailAndPassword());
    }

    private void showError(TextInputLayout editText, String value) {
        editText.setErrorEnabled(true);
        editText.setError(String.format(getString(R.string.cannot_be_empty), value));
    }

    private void checkEmailAndPassword() {
        if (TextUtils.isEmpty(binding.emailEt.getEditText().getText().toString().trim())) {
            showError(binding.emailEt, getString(R.string.email));
            return;
        }
        binding.emailEt.setErrorEnabled(false);

        if (TextUtils.isEmpty(binding.passwordEt.getEditText().getText().toString().trim())) {
            showError(binding.passwordEt, getString(R.string.password));
            return;
        }
        binding.passwordEt.setErrorEnabled(false);

        if (binding.passwordEt.getEditText().getText().toString().length() < 8) {
            binding.passwordEt.setErrorEnabled(true);
            binding.passwordEt.setError(getString(R.string.invalid_password));
            return;
        }
        binding.passwordEt.setErrorEnabled(false);


        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.getEditText().getText().toString()).matches()) {
            binding.emailEt.setErrorEnabled(false);

            Executor executor = Executors.newSingleThreadExecutor();
            /// TODO: Executor to execute tasks in Threads (not in the main UI). It will not block the UI

            executor.execute(() -> {
                User user = viewModel.login(binding.emailEt.getEditText().getText().toString(), binding.passwordEt.getEditText().getText().toString());
                /// TODO: Checking here credential valid or not inside the ROOM Database

                if (user != null) {

                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("login_user", true);
                    editor.commit();

                    /// TODO: SAVING Login User true that user is currently logged in the account

                    requireActivity().runOnUiThread(() -> {
                        NavOptions navOptions = new NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_right)
                                .setExitAnim(R.anim.slide_out_left)
                                .setPopEnterAnim(R.anim.slide_in_left)
                                .setPopExitAnim(R.anim.slide_out_right)
                                .setPopUpTo(R.id.signUpFragment, true)
                                .build();

                        navController.navigate(R.id.action_global_homeFragment, null, navOptions);
                    });

                } else {
                    Utils.showSnackbar(binding.getRoot(), "Invalid User!");
                }
            });


        } else {
            binding.emailEt.setErrorEnabled(true);
            binding.emailEt.setError(getString(R.string.invalid_email));
        }
    }

}