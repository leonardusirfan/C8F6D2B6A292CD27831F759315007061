package id.net.gmedia.pal.PiutangAct;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.PiutangModel;
import id.net.gmedia.pal.PetaOutlet;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppLocationManager;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.DateTimeChooser;
import id.net.gmedia.pal.Util.DialogFactory;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import id.net.gmedia.pal.Util.Haversine;
import id.net.gmedia.pal.Util.JSONBuilder;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;
import id.net.gmedia.pal.Util.SimpleObjectModel;

public class PiutangDetail extends AppCompatActivity {

    private String type = "tunai";
    private boolean tempo = false;

    private int loaded = 0;
    public CustomerModel customer;
    private GoogleLocationManager manager;
    private Location current_location;
    private LatLng outlet_location;

    private boolean mulai_lunasi = false;
    private boolean lunasi = false;
    private double sisa_pelunasan = 0;
    private double jumlah = 0;

    private LinearLayout layout_akun, layout_giro;
    private TextView txt_nama, txt_alamat, txt_piutang, txt_jumlah,
            txt_total_bayar, txt_jarak, txt_tgl_giro, txt_nomor_giro, txt_keterangan;
    private SlidingUpPanelLayout slidingpanel;
    private Spinner spn_akun;
    private List<SimpleObjectModel> listAkun = new ArrayList<>();
    private List<String> spinner_item;
    private ArrayAdapter<String> spinner_adapter;
    private Date today = new Date();

    private List<PiutangModel> listPiutang = new ArrayList<>();
    private LoadMoreScrollListener loadMoreScrollListener;
    private PiutangDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Piutang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_CUSTOMER)){
            Gson gson = new Gson();
            customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        }

        RadioGroup rb_parent = findViewById(R.id.rb_parent);
        layout_akun = findViewById(R.id.layout_akun);
        layout_giro = findViewById(R.id.layout_giro);
        slidingpanel = findViewById(R.id.layout_parent);
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_total_bayar = findViewById(R.id.txt_total_bayar);
        txt_jarak = findViewById(R.id.txt_jarak);
        spn_akun = findViewById(R.id.spn_akun);
        txt_tgl_giro = findViewById(R.id.txt_tgl_giro);
        txt_tgl_giro.setText(Converter.DToString(today));
        txt_nomor_giro = findViewById(R.id.txt_nomor_giro);
        txt_keterangan = findViewById(R.id.txt_keterangan);

        RecyclerView rv_piutang = findViewById(R.id.rv_piutang);
        rv_piutang.setItemAnimator(new DefaultItemAnimator());
        rv_piutang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PiutangDetailAdapter(this, listPiutang);
        rv_piutang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadPiutang(false);
            }
        });

        rb_parent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_tunai:
                        layout_akun.setVisibility(View.VISIBLE);
                        layout_giro.setVisibility(View.GONE);
                        type = "tunai";

                        spinner_item.clear();
                        spinner_adapter.notifyDataSetChanged();
                        loadAkun();
                        break;
                    case R.id.rb_bank:
                        layout_akun.setVisibility(View.VISIBLE);
                        layout_giro.setVisibility(View.GONE);
                        type = "bank";

                        spinner_item.clear();
                        spinner_adapter.notifyDataSetChanged();
                        loadAkun();
                        break;
                    case R.id.rb_giro:
                        type = "giro";
                        layout_akun.setVisibility(View.GONE);
                        layout_giro.setVisibility(View.VISIBLE);
                        break;
                        default:
                            layout_akun.setVisibility(View.VISIBLE);
                            layout_giro.setVisibility(View.GONE);
                            type = "";
                            break;
                }
            }
        });

        findViewById(R.id.btn_lunasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sisa_pelunasan > 0){
                    Toast.makeText(PiutangDetail.this, "Sisa pelunasan harus dihabiskan", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(manager == null){
                        manager = new GoogleLocationManager(PiutangDetail.this, new GoogleLocationManager.LocationUpdateListener() {
                            @Override
                            public void onChange(Location location) {
                                double distance = Haversine.distance(location.getLatitude(),
                                        location.getLongitude(),outlet_location.latitude, outlet_location.longitude);
                                if(distance >= 1){
                                    txt_jarak.setText(String.format(Locale.getDefault(), "%.2f Km", distance));
                                }
                                else{
                                    txt_jarak.setText(String.format(Locale.getDefault(), "%.2f m", distance * 1000));
                                }
                                current_location = location;
                            }
                        });
                        manager.startLocationUpdates();
                    }

                    if(spinner_adapter == null || spinner_item == null){
                        spinner_item = new ArrayList<>();
                        spinner_adapter = new ArrayAdapter<>(
                                PiutangDetail.this, android.R.layout.simple_spinner_item, spinner_item);
                        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spn_akun.setAdapter(spinner_adapter);
                        loadAkun();
                    }

                    txt_total_bayar.setText(Converter.doubleToRupiah(jumlah));
                    slidingpanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    lunasi = true;
                }
            }
        });

        findViewById(R.id.btn_peta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(outlet_location == null){
                    return;
                }

                Gson gson = new Gson();
                Intent i = new Intent(PiutangDetail.this, PetaOutlet.class);

                i.putExtra(Constant.EXTRA_LOKASI_OUTLET, gson.toJson(outlet_location));
                if(current_location != null){
                    i.putExtra(Constant.EXTRA_LOKASI_USER, gson.toJson(new LatLng(current_location.getLatitude(),
                            current_location.getLongitude())));
                }
                startActivity(i);
            }
        });

        findViewById(R.id.img_tgl_giro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(PiutangDetail.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_giro.setText(dateString);
                    }
                }, today.getTime());
            }
        });

        findViewById(R.id.img_lunasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(PiutangDetail.this,
                        R.layout.popup_piutang, 85, 30);

                final TextView txt_nominal, btn_batal, btn_bayar;
                txt_nominal = dialog.findViewById(R.id.txt_nominal);

                double jum_piutang = 0;
                for(PiutangModel p : listPiutang){
                    jum_piutang += p.getJumlah();
                }

                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f", jum_piutang));
                btn_bayar = dialog.findViewById(R.id.btn_bayar);
                btn_batal = dialog.findViewById(R.id.btn_batal);

                btn_batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                btn_bayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txt_nominal.getText().toString().equals("")){
                            Toast.makeText(PiutangDetail.this, "Nominal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            jumlah = Double.parseDouble(txt_nominal.getText().toString());
                            sisa_pelunasan = jumlah;
                            String jum = "Sisa : " + Converter.doubleToRupiah(sisa_pelunasan);
                            txt_jumlah.setText(jum);

                            txt_jumlah.setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_lunasi).setVisibility(View.VISIBLE);
                            findViewById(R.id.img_lunasi).setVisibility(View.GONE);

                            mulai_lunasi = true;

                            for(PiutangModel p : listPiutang){
                                p.setSelected(false);
                            }

                            adapter.setLunasi(mulai_lunasi);
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        findViewById(R.id.btn_bayar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<JSONObject> listBayar = new ArrayList<>();
                double pelunasan = jumlah;

                for(PiutangModel p : listPiutang){
                    if(p.isSelected()){
                        JSONBuilder nota = new JSONBuilder();
                        nota.add("nomor", p.getId());
                        if(pelunasan > p.getJumlah()){
                            nota.add("bayar", p.getJumlah());
                            pelunasan -= p.getJumlah();
                            listBayar.add(nota.create());
                        }
                        else{
                            nota.add("bayar", pelunasan);
                            listBayar.add(nota.create());
                            break;
                        }
                    }
                }

                if(current_location != null){
                    lunasiPiutang(listBayar);
                }
                else{
                    Toast.makeText(PiutangDetail.this, "Lokasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadPiutang(true);
    }

    public boolean isSisa(){
        System.out.println("SISA : " + sisa_pelunasan);
        return sisa_pelunasan > 0;
    }

    private void lunasiPiutang(List<JSONObject> listBayar){
        JSONBuilder body = new JSONBuilder();
        body.add("nota_jual", new JSONArray(listBayar));
        body.add("kode_pelanggan", customer.getId());
        body.add("cara_bayar", type.equals("bank")?"transfer":type);
        if(type.equals("giro")){
            body.add("kode_giro", txt_nomor_giro.getText().toString());
        }
        else{
            body.add("kode_akun", listAkun.get(spn_akun.
                    getSelectedItemPosition()).getId());
        }
        body.add("keterangan", txt_keterangan.getText().toString());
        body.add("user_latitude", current_location.getLatitude());
        body.add("user_longitude", current_location.getLongitude());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PIUTANG_PELUNASAN,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(PiutangDetail.this, "Pelunasan berhasil", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(PiutangDetail.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void updateJumlah(double update){
        sisa_pelunasan -=  update;
        if(sisa_pelunasan < 0){
            String jum = "Sisa : " + Converter.doubleToRupiah(0);
            txt_jumlah.setText(jum);
        }
        else{
            String jum = "Sisa : " + Converter.doubleToRupiah(sisa_pelunasan);
            txt_jumlah.setText(jum);
        }
    }

    private void loadAkun(){
        String parameter = String.format(Locale.getDefault(), "?tipe=%s", type);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_AKUN + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            listAkun.clear();
                            spinner_item.clear();

                            JSONArray array = new JSONObject(result).getJSONArray("akun_list");
                            for(int i = 0; i < array.length(); i++){
                                listAkun.add(new SimpleObjectModel(array.getJSONObject(i).getString("kode_akun"),
                                        array.getJSONObject(i).getString("nama_akun")));
                                spinner_item.add(array.getJSONObject(i).getString("nama_akun"));
                            }

                            spinner_adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PiutangDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadPiutang(final boolean init){
        AppLoading.getInstance().showLoading(this);
        if(init){
            loaded = 0;
        }

        String parameter = String.format(Locale.getDefault(), "%s?start=%d&limit=%d",
                customer.getId(), loaded, 10);
        if(tempo){
            parameter += "&filter=tempo";
        }
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PIUTANG_CUSTOMER + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    /*@Override
                    public void onEmpty(String message) {
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                        loadMoreScrollListener.cantLoad();
                    }*/

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listPiutang.clear();
                            }

                            JSONObject obj = new JSONObject(result);

                            txt_nama.setText(customer.getNama());
                            txt_alamat.setText(obj.getJSONObject("pelanggan").getString("alamat"));
                            txt_piutang.setText(Converter.doubleToRupiah(customer.getTotalPiutang()));
                            outlet_location = new LatLng(obj.getJSONObject("pelanggan").
                                    getDouble("latitude"), obj.getJSONObject("pelanggan").getDouble("longitude"));

                            JSONArray array = obj.getJSONArray("nota_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject nota = array.getJSONObject(i);

                                int type = nota.getString("tipe").equals("canvas")?Constant.PENJUALAN_CANVAS:Constant.PENJUALAN_SO;
                                listPiutang.add(new PiutangModel(nota.getString("nomor_nota"),
                                        nota.getString("nomor_nota"), nota.getDouble("piutang") - nota.getDouble("bayar"),
                                        nota.getString("tanggal"), nota.getString("tanggal_tempo"), type));
                                loaded += 1;
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PiutangDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("PiutangDetail", e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }
                }));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_piutang_detail, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_filter:
                PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_filter));
                popup.getMenuInflater().inflate(R.menu.submenu_piutang_detail, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_tempo){
                            tempo = true;
                            loadPiutang(true);
                        }
                        else{
                            tempo = false;
                            loadPiutang(true);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(lunasi){
            slidingpanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            lunasi = false;
        }
        else if(mulai_lunasi){
            mulai_lunasi = false;
            adapter.setLunasi(false);

            txt_jumlah.setVisibility(View.GONE);
            findViewById(R.id.btn_lunasi).setVisibility(View.GONE);
            findViewById(R.id.img_lunasi).setVisibility(View.VISIBLE);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(manager != null){
            manager.stopLocationUpdates();
        }
    }
}
