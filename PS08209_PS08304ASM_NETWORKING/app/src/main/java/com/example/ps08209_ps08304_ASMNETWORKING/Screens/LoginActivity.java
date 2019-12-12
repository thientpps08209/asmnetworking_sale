package com.example.ps08209_ps08304_ASMNETWORKING.Screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ps08209_ps08304_ASMNETWORKING.R;
import com.example.ps08209_ps08304_ASMNETWORKING.utilities.CustomAlertDialogActivity;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {

    EditText ed_username, ed_password;
    Button btn_login, btn_signup;
    CheckBox chk_rememberMe;
    SharedPreferences preferences;
    public static final String PREFS_NAME = "Prefsfile";
    Boolean isChecked, b;
    CustomAlertDialogActivity customAlertDialogActivity = new CustomAlertDialogActivity();

    private Socket mSocket;

    {
        try {
//            mSocket = IO.socket("http://10.22.211.191:3000");
            mSocket = IO.socket("http://192.168.1.4:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // toast non-ui
    public void toast(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // lang nghe su kien login va xu ly
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();

            if (data.equals("true")) {
                toast(LoginActivity.this, "Đăng nhập thành công");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
                finish();
            } else if (data.equals("false")) {
                toast(LoginActivity.this, "Đăng nhập thất bại");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //connect socket
        mSocket.connect();
        mSocket.on("login", onLogin);

        //shared prefs
        //tham khảo: https://www.youtube.com/watch?v=rbl21jfOEFg và http://androidtmc.blogspot.com/2015/08/luu-tru-du-lieu-voi-shared-preferences.html
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        init();

        loadPreferencesData();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, SignupActivity.class), 999);
            }
        });
    }

    private void init() {
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        chk_rememberMe = findViewById(R.id.chk_rememberMe);
    }

    public void validateForm() {
        final String un = ed_username.getText().toString().trim();
        final String pw = ed_password.getText().toString().trim();
//        if (un.isEmpty() || pw.isEmpty()) {
//            customAlertDialogActivity.customDialog1("Thông báo", "Vui lòng nhập đủ thông tin", LoginActivity.this);
//        } else if (un.equals("admin") && pw.equals("admin")) {
//            //load data nếu đã chọn nhớ đăng nhập
//            if (chk_rememberMe.isChecked()) {
//                savePreferencesData(un, pw);
//            } else {
//                clearPreferencesData();
//            }
//            //chuyển activity
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
//            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//        } else {
//            customAlertDialogActivity.customDialog1("Thông báo", "Thông tin đăng nhập sai", LoginActivity.this);
//            mSocket.emit("login", un, pw);
//        }

        if (!un.isEmpty() || !pw.isEmpty()) {
            mSocket.emit("login", un, pw);

            //load data nếu đã chọn nhớ đăng nhập
            if (chk_rememberMe.isChecked()) {
                savePreferencesData(un, pw);
            } else {
                clearPreferencesData();
            }
        } else {
            customAlertDialogActivity.customDialog1("Thông báo", "Thông tin đăng nhập sai", LoginActivity.this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_CANCELED || resultCode == RESULT_OK) {
            ed_username.getText().clear();
            ed_password.getText().clear();
            chk_rememberMe.setChecked(false);
        }
    }

    private void loadPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (sp.contains("pref_name")) {
            String u = sp.getString("pref_name", "Not found");
            ed_username.setText(u);
        }
        if (sp.contains("pref_pass")) {
            String pass = sp.getString("pref_pass", "Not found");
            ed_password.setText(pass);
        }
        if (sp.contains("pref_check")) {
            b = sp.getBoolean("pref_check", false);
            chk_rememberMe.setChecked(b);
        }
    }

    private void savePreferencesData(String un, String pw) {
        isChecked = chk_rememberMe.isChecked();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pref_name", un);
        editor.putString("pref_pass", pw);
        editor.putBoolean("pref_check", isChecked);
        editor.apply();
    }

    private void clearPreferencesData() {
        preferences.edit().clear().apply();
    }

    @Override
    public void onBackPressed() {
        //prevent backpress to go back to main
    }
}
