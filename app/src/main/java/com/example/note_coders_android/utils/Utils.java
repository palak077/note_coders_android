package com.example.note_coders_android.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import com.example.note_coders_android.R;
import com.example.note_coders_android.ui.interfaces.AlertDialogCallback;

public class Utils {

    public static void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", v -> snackbar.dismiss());
        ((TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setSingleLine(false);
        snackbar.show();
    }

    public static Dialog createDialog(ViewBinding layoutResViewBinding, int drawableId, Boolean cancellable) {
        Dialog dialog = new Dialog(layoutResViewBinding.getRoot().getContext());
        dialog.setContentView(layoutResViewBinding.getRoot());
        dialog.setCancelable(cancellable);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(layoutResViewBinding.getRoot().getContext(), drawableId));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Boolean hasPermissions(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void showAlertDialog(Context context, String title, String subTitle, AlertDialogCallback listener) {
        Resources resources = context.getResources();

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(subTitle)
                .setCancelable(true)
                .setNegativeButton(resources.getString(R.string.cancel), (dialog, which) -> listener.onNegativeButtonClick(dialog))
                .setPositiveButton(resources.getString(R.string.exit), (dialog, which) -> listener.onPositiveButtonClick())
                .show();
    }

}