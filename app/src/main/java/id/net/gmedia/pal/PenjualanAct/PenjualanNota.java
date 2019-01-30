package id.net.gmedia.pal.PenjualanAct;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.DaftarSoAct.DaftarSO;
import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.PetaOutlet;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.RiwayatAct.Riwayat;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.AppLocationManager;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.DateTimeChooser;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import id.net.gmedia.pal.Util.Haversine;
import id.net.gmedia.pal.Util.JSONBuilder;

public class PenjualanNota extends AppCompatActivity {

    public int JENIS_PENJUALAN;
    public CustomerModel customer;
    private TextView txt_total, txt_jarak, txt_tempo;

    private String tempo = "";
    private Calendar c;

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
        txt_tempo = findViewById(R.id.txt_tempo);

        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        txt_nama.setText(customer.getNama());
        txt_alamat.setText(customer.getAlamat());
        txt_jarak.setText(R.string.penjualan_detail_jarak);

        c = Calendar.getInstance();
        /*tempo = Converter.DToString(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));*/

        findViewById(R.id.layout_tempo).setVisibility(View.VISIBLE);
        if(getIntent().hasExtra(Constant.EXTRA_TANGGAL_TEMPO)){
            tempo = getIntent().getStringExtra(Constant.EXTRA_TANGGAL_TEMPO);
        }
        txt_tempo.setText(tempo);

        findViewById(R.id.img_kalender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(PenjualanNota.this,
                        new DateTimeChooser.OnDateTimeSelected() {
                            @Override
                            public void onFinished(String dateString) {
                                tempo = dateString;
                                txt_tempo.setText(tempo);
                            }
                        }, c.getTimeInMillis());
            }
        });

        RecyclerView rv_nota = findViewById(R.id.rv_nota);
        rv_nota.setItemAnimator(new DefaultItemAnimator());
        rv_nota.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        PenjualanNotaAdapter adapter = new PenjualanNotaAdapter(this, AppKeranjangPenjualan.getInstance().getBarang(), JENIS_PENJUALAN);
        rv_nota.setAdapter(adapter);

        initBarang();

        findViewById(R.id.img_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PenjualanNota.this, PenjualanBarang.class);
                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, JENIS_PENJUALAN);
                i.putExtra(Constant.EXTRA_TANGGAL_TEMPO, tempo);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_location == null){
                    Toast.makeText(PenjualanNota.this, "Lokasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
                else if(tempo.equals("")){
                    Toast.makeText(PenjualanNota.this, "Tanggal tempo harus diisi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkoutBarang(JENIS_PENJUALAN);
                }
            }
        });

        manager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
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
            obj.add("jumlah", b.getJumlah());
            obj.add("diskon", b.getDiskon());
            obj.add("satuan", b.getSatuan());
            array_barang.add(obj.create());
        }

        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());
        body.add("barang", new JSONArray(array_barang));
        body.add("user_latitude", current_location.getLatitude());
        body.add("user_longitude", current_location.getLongitude());
        body.add("tanggal_tempo", tempo);

        //System.out.println("REQUEST : " + body.create());
        ApiVolleyManager.getInstance().addRequest(this, url, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String message) {
                Toast.makeText(PenjualanNota.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                Toast.makeText(PenjualanNota.this, "Penjualan berhasil", Toast.LENGTH_SHORT).show();
                AppKeranjangPenjualan.getInstance().selesaikanTransaksi();

                if(type == Constant.PENJUALAN_SO){
                    Intent resultIntent = new Intent(PenjualanNota.this, DaftarSO.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(PenjualanNota.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    stackBuilder.startActivities();
                    /*Intent i = new Intent(PenjualanNota.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //startActivity(i);*/
                }
                else{
                    Intent resultIntent = new Intent(PenjualanNota.this, Riwayat.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(PenjualanNota.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    stackBuilder.startActivities();
                    /*Intent i = new Intent(PenjualanNota.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);*/
                }


            }

            @Override
            public void onFail(String message) {
                Toast.makeText(PenjualanNota.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void initBarang(){
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
