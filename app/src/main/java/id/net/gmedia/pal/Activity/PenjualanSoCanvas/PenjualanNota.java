package id.net.gmedia.pal.Activity.PenjualanSoCanvas;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Activity.DaftarSO.DaftarSO;
import id.net.gmedia.pal.Activity.Riwayat;
import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Adapter.PenjualanNotaAdapter;
import id.net.gmedia.pal.PetaOutlet;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppLocationManager;

import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.GoogleLocationManager;

import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.Haversine;
import com.leonardus.irfan.JSONBuilder;

public class PenjualanNota extends AppCompatActivity {

    //Variabel global jenis penjualan
    public int JENIS_PENJUALAN;
    //Variabel global customer
    public CustomerModel customer;

    //Variabel UI
    private TextView txt_total, txt_jarak, txt_tempo;
    public AppCompatSpinner spn_bayar;

    //Variabel lokasi
    private GoogleLocationManager manager;
    private Location current_location;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_nota);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Nota Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        TextView txt_nama, txt_alamat;
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_total = findViewById(R.id.txt_total);
        txt_jarak = findViewById(R.id.txt_jarak);
        spn_bayar = findViewById(R.id.spn_bayar);
        txt_tempo = findViewById(R.id.txt_tempo);

        //Inisialisasi data global jenis penjualan, customer, cara bayat
        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        if(getIntent().hasExtra(Constant.EXTRA_CARA_BAYAR)){
            spn_bayar.setSelection(getIntent().getIntExtra(Constant.EXTRA_CARA_BAYAR, 0));
        }
        if(getIntent().hasExtra(Constant.EXTRA_TEMPO)){
            txt_tempo.setText(getIntent().getStringExtra(Constant.EXTRA_TEMPO));
        }

        //Inisialisasi nilai UI
        txt_nama.setText(customer.getNama());
        txt_alamat.setText(customer.getAlamat());
        txt_jarak.setText(R.string.penjualan_detail_jarak);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_nota = findViewById(R.id.rv_nota);
        rv_nota.setItemAnimator(new DefaultItemAnimator());
        rv_nota.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        PenjualanNotaAdapter adapter = new PenjualanNotaAdapter(this, AppKeranjangPenjualan.getInstance().getBarang(), JENIS_PENJUALAN);
        rv_nota.setAdapter(adapter);

        //Muat data barang
        initBarang();

        //Button tambah barang baru
        findViewById(R.id.img_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PenjualanNota.this, PenjualanBarang.class);
                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, JENIS_PENJUALAN);
                i.putExtra(Constant.EXTRA_TEMPO, txt_tempo.getText().toString());
                if(spn_bayar.getSelectedItemPosition() != 0){
                    i.putExtra(Constant.EXTRA_CARA_BAYAR, spn_bayar.getSelectedItemPosition());
                }
                startActivity(i);
            }
        });

        findViewById(R.id.img_kalender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(PenjualanNota.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tempo.setText(dateString);
                    }
                });
            }
        });

        //Button checkout barang
        findViewById(R.id.btn_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validasi input nota
                if(current_location == null){
                    Toast.makeText(PenjualanNota.this, "Lokasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
                else if(spn_bayar.getSelectedItemPosition() == 0){
                    Toast.makeText(PenjualanNota.this, "Pilih cara bayar terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                /*else if(spn_bayar.getSelectedItemPosition() > 1 && txt_tempo.getText().toString().equals("")){
                    Toast.makeText(PenjualanNota.this, "Tanggal tempo belum terisi", Toast.LENGTH_SHORT).show();
                }*/
                else{
                    //checkout barang
                    checkoutBarang(JENIS_PENJUALAN);
                }
            }
        });

        /*spn_bayar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_tempo.setText("");
                if(position > 1){
                    findViewById(R.id.layout_tempo).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.layout_tempo).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        //Inisialisasi manager lokasi
        manager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                //Update jarak customer
                double distance = Haversine.distance(location.getLatitude(),
                        location.getLongitude(), customer.getLatitude(), customer.getLongitude());
                String string_lokasi = "( Jarak dengan outlet : ";
                if(distance >= 1){
                    string_lokasi +=  String.format(Locale.getDefault(), "%.2f Km )", distance);
                }
                else{
                    string_lokasi +=  String.format(Locale.getDefault(), "%.2f m )", distance * 1000);
                }
                txt_jarak.setText(string_lokasi);
                current_location = location;
            }
        });
        manager.startLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case AppLocationManager.ACTIVATE_LOCATION: {
                if (manager != null) {
                    manager.startLocationUpdates();
                }
                break;
            }
            default:super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case AppLocationManager.PERMISSION_LOCATION:{
                if(manager != null){
                    manager.startLocationUpdates();
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkoutBarang(final int type){
        //simpan nota checkout ke Web service
        String url;
        if(type == Constant.PENJUALAN_SO){
            url = Constant.URL_CHECKOUT_SO;
        }
        else if(type == Constant.PENJUALAN_CANVAS){
            url = Constant.URL_CHECKOUT_CANVAS;
        }
        else{
            return;
        }

        List<JSONObject> array_barang = new ArrayList<>();
        for(BarangModel b : AppKeranjangPenjualan.getInstance().getBarang()){
            JSONBuilder obj = new JSONBuilder();
            obj.add("kode_barang", b.getId());
            if(type == Constant.PENJUALAN_SO){
                obj.add("jumlah_gudang", b.getJumlah() - b.getJumlah_potong());
                obj.add("jumlah_canvas", b.getJumlah_potong());
            }
            else{
                obj.add("jumlah", b.getJumlah());
            }

            obj.add("diskon", b.getDiskon());
            obj.add("satuan", b.getSatuan());
            obj.add("no_batch", b.getNo_batch());
            array_barang.add(obj.create());
        }

        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());
        body.add("barang", new JSONArray(array_barang));
        body.add("user_latitude", current_location.getLatitude());
        body.add("user_longitude", current_location.getLongitude());
        body.add("cara_bayar", spn_bayar.getSelectedItem().toString());

        //System.out.println("REQUEST : " + body.create());
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading, new AppLoading.CancelListener() {
            @Override
            public void onCancel() {
                ApiVolleyManager.getInstance().cancelRequest();
            }
        });
        ApiVolleyManager.getInstance().addRequest(this, url, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onEmpty(String message) {
                AppLoading.getInstance().stopLoading();
                Toast.makeText(PenjualanNota.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                AppLoading.getInstance().stopLoading();
                Toast.makeText(PenjualanNota.this, "Penjualan berhasil", Toast.LENGTH_SHORT).show();
                AppKeranjangPenjualan.getInstance().selesaikanTransaksi();

                if(type == Constant.PENJUALAN_SO){
                    Intent resultIntent = new Intent(PenjualanNota.this, DaftarSO.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(PenjualanNota.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    stackBuilder.startActivities();
                }
                else{
                    Intent resultIntent = new Intent(PenjualanNota.this, Riwayat.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(PenjualanNota.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    stackBuilder.startActivities();
                }
            }

            @Override
            public void onFail(String message) {
                AppLoading.getInstance().stopLoading();
                Toast.makeText(PenjualanNota.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void initBarang(){
        //Hitung total barang
        double sum = 0;
        for(BarangModel b : AppKeranjangPenjualan.getInstance().getBarang()){
            sum += (b.getSubtotal() - b.getDiskon());
        }
        txt_total.setText(Converter.doubleToRupiah(sum));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Batalkan Penjualan");
        builder.setMessage("Anda yakin ingin membatalkan penjualan?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppKeranjangPenjualan.getInstance().selesaikanTransaksi();
                Intent i = new Intent(PenjualanNota.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_penjualan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_map:
                //Buka aktivity map antara sales & customer/outlet
                Gson gson = new Gson();
                Intent i = new Intent(this, PetaOutlet.class);
                i.putExtra(Constant.EXTRA_LOKASI_OUTLET, gson.toJson(new LatLng(customer.getLatitude(), customer.getLongitude())));
                if(current_location != null){
                    i.putExtra(Constant.EXTRA_LOKASI_USER, gson.toJson(new LatLng(current_location.getLatitude(),
                            current_location.getLongitude())));
                }
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stopLocationUpdates();
    }
}