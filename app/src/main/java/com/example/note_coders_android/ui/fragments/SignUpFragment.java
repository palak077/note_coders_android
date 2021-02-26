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
import com.example.note_coders_android.databinding.FragmentSignUpBinding;
import com.example.note_coders_android.ui.base.BaseFragment;
import com.example.note_coders_android.ui.viewModels.UserViewModel;

public class SignUpFragment extends BaseFragment<FragmentSignUpBinding, UserViewModel> {

    @Override
    public FragmentSignUpBinding getFragmentBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSignUpBinding.inflate(inflater, container, false);
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
        binding.signUpBtn.setOnClickListener(v -> checkEmailAndPassword());
    }

    private void showError(TextInputLayout editText, String value) {
        editText.setErrorEnabled(true);
        editText.setError(String.format(getString(R.string.cannot_be_empty), value));
    }

    private void checkEmailAndPassword() {
        if (TextUtils.isEmpty(binding.nameEt.getEditText().getText().toString().trim())) {
            showError(binding.nameEt, getString(R.string.name));
            return;
        }
        binding.nameEt.setErrorEnabled(false);

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

        if (TextUtils.isEmpty(binding.confirmPasswordEt.getEditText().getText().toString().trim())) {
            showError(binding.confirmPasswordEt, getString(R.string.confirm_password));
            return;
        }
        binding.confirmPasswordEt.setErrorEnabled(false);

        if (binding.passwordEt.getEditText().getText().toString().length() < 8) {
            binding.passwordEt.setErrorEnabled(true);
            binding.passwordEt.setError("Password must be greater than 8 characters");
            return;
        }
        binding.passwordEt.setErrorEnabled(false);



        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.getEditText().getText().toString()).matches()) {
            binding.emailEt.setErrorEnabled(false);

            if (binding.passwordEt.getEditText().getText().toString().equals(binding.confirmPasswordEt.getEditText().getText().toString())) {
                binding.confirmPasswordEt.setErrorEnabled(false);

                Executor executor = Executors.newSingleThreadExecutor();

                executor.execute(() -> {
                    User user = new User();
                    user.setName(binding.nameEt.getEditText().getText().toString().trim());
                    user.setEmail(binding.emailEt.getEditText().getText().toString().trim());
                    user.setPassword(binding.passwordEt.getEditText().getText().toString().trim());
                    viewModel.signUp(user);
//                    /// TODO: Sign Up Process here:: Inserting user data in Room Database
//
//                    User user1 = viewModel.findByEmail(binding.emailEt.getEditText().getText().toString().trim());

                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("login_user", true);
                    editor.commit();
                    /// TODO: Saving "true" that user is currently logged in


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

                });

            } else {
                binding.confirmPasswordEt.setErrorEnabled(true);
                binding.confirmPasswordEt.setError(getString(R.string.password_doesnt_match));
            }
        } else {
            binding.emailEt.setErrorEnabled(true);
            binding.emailEt.setError(getString(R.string.invalid_email));
        }
    }


}