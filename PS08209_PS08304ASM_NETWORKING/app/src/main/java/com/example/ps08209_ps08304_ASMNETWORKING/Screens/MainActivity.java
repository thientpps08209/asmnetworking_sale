package com.example.ps08209_ps08304_ASMNETWORKING.Screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ps08209_ps08304_ASMNETWORKING.R;
import com.example.ps08209_ps08304_ASMNETWORKING.adapter.ProductAdapter;
import com.example.ps08209_ps08304_ASMNETWORKING.model.Products;
import com.example.ps08209_ps08304_ASMNETWORKING.utilities.CustomAlertDialogActivity;
import com.example.ps08209_ps08304_ASMNETWORKING.utilities.SwipeToDeleteCallback;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {
    ArrayList<Products> productsList = new ArrayList<>();
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;

    String id, maSp, tenSp, nsx, hinhSp;
    double giaBan;
    private AlertDialog alertDialog;
    public LinearLayout dialog_layout;
    private EditText ed_maSp, ed_tenSp, ed_nsx, ed_giaBan;
    public Button btn_ok, btn_update;
    private ImageView iv_icon;

    private Locale localeVN = new Locale("vi", "VN");
    private NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

    private Socket mSocket;

    {
        try {
//            mSocket = IO.socket("http://10.22.211.191:3000");
            mSocket = IO.socket("http://192.168.1.4:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener onGetProduct = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject jsonObject = (JSONObject) args[0];

                        try {

                            id = jsonObject.getString("_id");
                            maSp = jsonObject.getString("maSp");
                            tenSp = jsonObject.getString("tenSp");
                            nsx = jsonObject.getString("nsx");
                            giaBan = jsonObject.getDouble("giaBan");
                            hinhSp = jsonObject.getString("hinhSp");

                            Products products = new Products(id, maSp, tenSp, nsx, giaBan, hinhSp);
                            productsList.add(products);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!productsList.isEmpty()) {
                            setAdapter();
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("error", "Can't add product");
                        }

                    } catch (Exception e) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
        }
    };

    private Emitter.Listener onDeleteProduct = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();

            if (data.equals("true")) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Log.d("Lỗi", "Không thể xóa");
            }
        }
    };

    private Emitter.Listener onUpdateProduct = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String data = args[0].toString();

            if (data.equals("true")) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Log.d(" Lỗi", "Không update được");
            }
        }
    };


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect socket
        mSocket.connect();
        mSocket.on("getProduct", onGetProduct);
        mSocket.emit("getProduct", "Get products successfully!");

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Quản lý sản phẩm");

        //khỏi tạo
        init();

        //set adapter
        setAdapter();

        // xoa item
        enableSwipeToDeleteAndUndo();

        // refresh list data
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                // Clear list hien tai
                productsList.clear();
                productAdapter.notifyDataSetChanged();
                // Get lai list moi
                mSocket.emit("getProduct", "Get products successfully!");
            }
        });

        // auto hide fab
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        // fab event
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddProductActivity.class), 999);
            }
        });
    }

    public void init() {
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipteLayout);
        fab = findViewById(R.id.fab);
    }

    public void setAdapter() {
        productAdapter = new ProductAdapter(getApplicationContext(), productsList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(MainActivity.this);
    }

    // XOA ITEM
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                Products products = productsList.get(viewHolder.getAdapterPosition());
                mSocket.on("deleteProduct", onDeleteProduct);
                mSocket.emit("deleteProduct", products.get_id());
//                Toast.makeText(MainActivity.this, products.get_id(), Toast.LENGTH_SHORT).show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_products, menu);

        MenuItem searchItem = menu.findItem(R.id.timKiem);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Tìm sản phẩm...");

        //set cỡ chữ
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextSize(16);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String input = query.toLowerCase();
                productAdapter.getFilter().filter(input);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                String input = query.toLowerCase();
                productAdapter.getFilter().filter(input);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timKiem:

                break;
            case R.id.logout:
                CustomAlertDialogActivity customAlertDialogActivity = new CustomAlertDialogActivity();
                customAlertDialogActivity.logoutDialog("Xác nhận", "Bạn muốn đăng xuất?", MainActivity.this, "true");
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // LOAD DATA AFTER INSERT
        if (requestCode == 999 && resultCode == RESULT_OK) {
            // Clear list hien tai
            productsList.clear();
            productAdapter.notifyDataSetChanged();
            // Get lai list moi
            mSocket.emit("getProduct", "Get products successfully!");
        }

        // LOAD IMG FROM STORAGE
        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            // GET IMG DATA
//            uri = data.getData();
            hinhSp = data.getDataString();

            // SET IMG
            Picasso.with(MainActivity.this).load(hinhSp).into(iv_icon);
        }
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    // UPDATE ITEM
    @Override
    public void onItemClick(final int position) {

        Products products = productsList.get(position);

//        Toast.makeText(this, products.get_id(), Toast.LENGTH_SHORT).show();
        // get data
        id = products.get_id();
        maSp = products.getMaSp();
        tenSp = products.getTenSp();
        nsx = products.getNsx();
        giaBan = products.getGiaBan();
        hinhSp = products.getHinhSp();

        // show dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.edit_product_dialog, null);
        builder.setView(view);
        alertDialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog_layout = view.findViewById(R.id.dialog_layout);
        ed_maSp = view.findViewById(R.id.ed_maSp);
        ed_tenSp = view.findViewById(R.id.ed_tenSp);
        ed_nsx = view.findViewById(R.id.ed_nsx);
        ed_giaBan = view.findViewById(R.id.ed_gianBan);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_update = view.findViewById(R.id.btn_update);
        iv_icon = view.findViewById(R.id.iv_icon);

        // set data to edittext
        ed_maSp.setText(maSp);
        ed_tenSp.setText(tenSp);
        ed_nsx.setText(nsx);
        // format giaBan
        final String giaFormat = currencyVN.format(giaBan);
//        ed_giaBan.setText(giaFormat);
        ed_giaBan.setText(String.valueOf(giaBan));
        Picasso.with(MainActivity.this).load(hinhSp).into(iv_icon);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // LOAD IMG
        iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get new data
                String maMoi = ed_maSp.getText().toString().trim();
                String tenMoi = ed_tenSp.getText().toString().trim();
                String nsxMoi = ed_nsx.getText().toString().trim();
                String giaMoi = ed_giaBan.getText().toString().trim();

                try {
                    if (validate(maMoi, tenMoi, nsxMoi, giaMoi)) {
//                        double gia = Double.parseDouble(giaMoi);
                        // update item tren server
                        mSocket.on("updateProduct", onUpdateProduct);
                        mSocket.emit("updateProduct", id, maMoi, tenMoi, nsxMoi, giaMoi, hinhSp);
                        alertDialog.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        alertDialog.show();
    }

    private boolean validate(String maSp, String tenSp, String nsx, String giaBan) {

        try {
            double giaban = Double.parseDouble(giaBan);
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
            } else if (giaban <= 0) {
                ed_giaBan.requestFocus();
                ed_giaBan.setError("Giá bán lớn hơn 0");
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
