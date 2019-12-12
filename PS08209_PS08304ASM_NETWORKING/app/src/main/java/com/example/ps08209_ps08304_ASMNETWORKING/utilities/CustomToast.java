package com.example.ps08209_ps08304_ASMNETWORKING.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class CustomToast {

    public Context context;

    public CustomToast(Context context) {
        this.context = context;
    }

    // Toast
    public void toast(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
