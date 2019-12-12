package com.example.ps08209_ps08304_ASMNETWORKING.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ps08209_ps08304_ASMNETWORKING.Screens.LoginActivity;
import com.example.ps08209_ps08304_ASMNETWORKING.R;

import java.util.Objects;

public class CustomAlertDialogActivity {

    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View view;
    private AlertDialog alertDialog;

    @SuppressLint("InflateParams")
    public void customDialog1(String title, String msg, Context context) {
        builder = new AlertDialog.Builder(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        TextView dialogMsg = view.findViewById(R.id.dialogMsg);
        Button btn_ok = view.findViewById(R.id.btn_ok);

        dialogTitle.setText(title);
        dialogMsg.setText(msg);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        //animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        alertDialog.show();
    }

    @SuppressLint("InflateParams")
    public void logoutDialog(String title, String msg, final Context context, String cancelButton) {
        builder = new AlertDialog.Builder(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        TextView dialogMsg = view.findViewById(R.id.dialogMsg);
        LinearLayout separator = view.findViewById(R.id.separator);
        Button btn_ok = view.findViewById(R.id.btn_ok);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);

        dialogTitle.setText(title);
        dialogMsg.setText(msg);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).startActivity(new Intent(context, LoginActivity.class));
                ((Activity) context).finish();
            }
        });

        if (cancelButton.equals("true")) {
            separator.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        }

        //animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        alertDialog.show();
    }
}
