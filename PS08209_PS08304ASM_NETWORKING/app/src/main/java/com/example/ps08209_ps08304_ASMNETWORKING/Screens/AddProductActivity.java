package com.example.ps08209_ps08304_ASMNETWORKING.Screens;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ps08209_ps08304_ASMNETWORKING.R;
import com.example.ps08209_ps08304_ASMNETWORKING.utilities.CustomToast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

public class AddProductActivity extends AppCompatActivity {

    EditText ed_maSp, ed_tenSp, ed_nsx, ed_giaBan;
    Button btn_add, btn_cancel;
    CustomToast customToast = new CustomToast(AddProductActivity.this);
    ImageView iv_addImg;
    Uri uri;
    String hinhSp;

    private Socket mSocket;

    {
        try {
//            mSocket = IO.socket("http://10.22.211.191:3000");
            mSocket = IO.socket("http://192.168.1.4:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener onAddProduct = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();

            if (data.equals("true")) {
                customToast.toast("Đang thêm sản phẩm...");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                        setResult(RESULT_OK);
                        customToast.toast("Đã thêm sản phẩm");
                        finish();
                    }
                }, 1000);
            } else {
                Log.d("Error", "Thêm không thành công");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm sản phẩm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mSocket.connect();
        mSocket.on("addProduct", onAddProduct);

        // anh xa
        init();

        iv_addImg.setOnClickListener(uploadImg);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String masp = ed_maSp.getText().toString().trim();
                String tensp = ed_tenSp.getText().toString().trim();
                String nsx = ed_nsx.getText().toString().trim();
                String giaban = ed_giaBan.getText().toString().trim();

//                Toast.makeText(AddProductActivity.this, "STR: " + hinhSp + " .END", Toast.LENGTH_SHORT).show();

                if (validate(masp, tensp, nsx, giaban)) {
                    if (hinhSp == null) {
                        Toast.makeText(AddProductActivity.this, "Vui lòng chọn hình", Toast.LENGTH_SHORT).show();
                    } else {
                        mSocket.emit("addProduct", masp, tensp, nsx, giaban, hinhSp);
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void init() {
        ed_maSp = findViewById(R.id.ed_maSp);
        ed_tenSp = findViewById(R.id.ed_tenSp);
        ed_nsx = findViewById(R.id.ed_nsx);
        ed_giaBan = findViewById(R.id.ed_gianBan);
        btn_add = findViewById(R.id.btn_add);
        btn_cancel = findViewById(R.id.btn_cancel);
        iv_addImg = findViewById(R.id.iv_addImg);
    }

    // LOAD IMG
    View.OnClickListener uploadImg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Android < 5
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, REQUEST_CODE_FOLDER);
            //Android >= 6, permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        }
    };

    // IMG PERMISSION
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            // GET IMG DATA
//            uri = data.getData();
            hinhSp = data.getDataString();

            // SET IMG
            Picasso.with(AddProductActivity.this).load(hinhSp).into(iv_addImg);

            // test
//            Toast.makeText(this, "STR: " + hinhSp + " END", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 111);
            } else {
                Toast.makeText(this, "Đã hủy cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean validate(String maSp, String tenSp, String nsx, String giaBan) {
        double giaban = Double.parseDouble(giaBan);
        try {
            if (maSp.trim().length() == 0) {
                ed_maSp.requestFocus();
                ed_maSp.setError("Vui lòng nhập mã sản phẩm");
                return false;
            } else if (tenSp.trim().length() == 0) {
                ed_tenSp.requestFocus();
                ed_tenSp.setError("Vui lòng nhập tên sản phẩm");
                return false;
            } else if (nsx.trim().length() == 0) {
                ed_nsx.requestFocus();
                ed_nsx.setError("Vui lòng nhập nhà sản xuất");
                return false;
            } else if (giaBan.trim().length() == 0) {
                ed_giaBan.requestFocus();
                ed_giaBan.setError("Vui lòng nhập giá bán");
                return false;
            } else if (giaban < 0) {
                ed_giaBan.requestFocus();
                ed_giaBan.setError("Giá bán không được là số âm");
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            ed_giaBan.requestFocus();
            ed_giaBan.setError("Giá bán phải lớn hơn 0");
            return false;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
