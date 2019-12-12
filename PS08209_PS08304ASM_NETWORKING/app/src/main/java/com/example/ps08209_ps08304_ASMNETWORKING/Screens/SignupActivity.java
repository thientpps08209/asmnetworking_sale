package com.example.ps08209_ps08304_ASMNETWORKING.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ps08209_ps08304_ASMNETWORKING.R;
import com.example.ps08209_ps08304_ASMNETWORKING.utilities.CustomToast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SignupActivity extends AppCompatActivity {
    EditText ed_hoTen, ed_email, ed_password ,ed_sdt;
    TextView tv_login;
    Button btn_signup;
    CustomToast customToast = new CustomToast(SignupActivity.this);

    private Socket mSocket;

    {
        try {
//            mSocket = IO.socket("http://10.22.211.124:3000");
            mSocket = IO.socket("http://192.168.1.4:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSocket.connect();
        mSocket.on("register", onRegister);

        init();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ed_hoTen.getText().toString().trim();
                String email = ed_email.getText().toString().trim();
                String password = ed_password.getText().toString().trim();
                String phone = ed_sdt.getText().toString().trim();

                if (validate(name, email, password,phone)) {
                    mSocket.emit("register", name, email, password,phone);
                }
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void init() {
        ed_hoTen = findViewById(R.id.ed_hoTen);
        ed_email = findViewById(R.id.ed_email);
        ed_password = findViewById(R.id.ed_password);
        ed_sdt=findViewById(R.id.ed_phone);
        tv_login = findViewById(R.id.tv_login);
        btn_signup = findViewById(R.id.btn_signup);
    }

    private Emitter.Listener onRegister = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();

            if (data.equals("true")) {
                customToast.toast("Đang đăng ký...");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        setResult(RESULT_CANCELED);
                        customToast.toast("Đăng ký thành công");
                        finish();
                    }
                }, 1500);
            } else {
                Log.d("Error", "Đăng ký không thành công");
            }
        }
    };

    public boolean validate(String name, String email, String password,String phone) {
        try {
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền tên", Toast.LENGTH_SHORT).show();
                ed_hoTen.requestFocus();
                return false;
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền email", Toast.LENGTH_SHORT).show();
                ed_email.requestFocus();
                return false;
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền mật khẩu", Toast.LENGTH_SHORT).show();
                ed_password.requestFocus();
                return false;
            } else if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền mật khẩu", Toast.LENGTH_SHORT).show();
                ed_sdt.requestFocus();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
