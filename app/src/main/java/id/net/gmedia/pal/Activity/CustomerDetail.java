package id.net.gmedia.pal.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;
import com.otaliastudios.zoom.ZoomLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.R;

import com.leonardus.irfan.AppLocationManager;

import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import id.net.gmedia.pal.Util.ScrollableMapView;
import id.net.gmedia.pal.Util.SearchableSpinnerDialog;

public class CustomerDetail extends AppCompatActivity implements OnMapReadyCallback {

    //Variabel konstan id upload gambar
    private final int UPLOAD_KTP = 999;
    private final int UPLOAD_OUTLET = 998;

    //Variable global Customer
    private String id_customer = "";

    //Variabel lokasi & maps
    private GoogleMap mMap;
    private GoogleLocationManager manager;
    private LatLng lokasi;
    private Marker marker;

    //Variabel UI
    private EditText txt_nama, txt_alamat, txt_no_ktp, txt_area, txt_npwp, txt_no_hp,
            txt_nama_pemilik, txt_negara;
    private TextView txt_kota, txt_provinsi;
    private ImageView img_ktp, img_outlet, overlay_ktp, overlay_outlet;
    private ProgressBar bar_ktp, bar_outlet;
    private ImageView img_galeri_selected;
    private ConstraintLayout layout_overlay;
    private CardView layout_galeri_selected;
    private ZoomLayout layout_zoom;
    private Button btn_next, btn_previous;
    private CollapsingToolbarLayout collapsing;
    private ProgressBar pb_map;

    //flag apakah galeri sedang menampilkan foto detail secara popup atau tidak
    private boolean detail = false;

    private boolean loading_location = false;

    //Variabel penampung list gambar yang ditampilkan di galeri (popup)
    private List<String> listImage = new ArrayList<>();

    //Variabel UI galeri (animasi, foto tampil)
    private Animation anim_popin, anim_popout;
    private int selectedImage = 0;
    private int imgHeight = 0;
    private int imgWidth = 0;

    //Variabel penampung upload foto ktp & outlet
    private UploadModel fotoKtp;
    private List<UploadModel> pendingFotoOutlet = new ArrayList<>();
    private List<String> listFotoKtp = new ArrayList<>();
    private List<String> listFotoOutlet = new ArrayList<>();

    //Variabel dropdown kota & provinsi
    private SearchableSpinnerDialog dialogKota;
    private SearchableSpinnerDialog dialogProvinsi;
    private List<SimpleObjectModel> listKota = new ArrayList<>();
    private List<SimpleObjectModel> listProvinsi = new ArrayList<>();
    private String kotaSelected = "";
    private String provinsiSelected = "";
    private String idProvinsi = "";
    private int loadedKota = 0;
    private int loadedProvinsi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        //Inisialisasi UI
        ImageView img_upload_ktp, img_upload_outlet;
        txt_nama = findViewById(R.id.txt_nama);
        txt_nama_pemilik = findViewById(R.id.txt_nama_pemilik);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_no_ktp = findViewById(R.id.txt_no_ktp);
        txt_area = findViewById(R.id.txt_area);
        txt_npwp = findViewById(R.id.txt_npwp);
        txt_no_hp = findViewById(R.id.txt_no_hp);
        txt_kota = findViewById(R.id.txt_kota);
        txt_provinsi = findViewById(R.id.txt_provinsi);
        txt_provinsi = findViewById(R.id.txt_provinsi);
        txt_negara = findViewById(R.id.txt_negara);
        img_outlet = findViewById(R.id.img_outlet);
        img_ktp = findViewById(R.id.img_ktp);
        overlay_ktp = findViewById(R.id.overlay_ktp);
        overlay_outlet = findViewById(R.id.overlay_outlet);
        bar_ktp = findViewById(R.id.bar_ktp);
        bar_outlet = findViewById(R.id.bar_outlet);
        Button btn_simpan = findViewById(R.id.btn_simpan);
        TextView lbl_area = findViewById(R.id.lbl_area);
        img_upload_ktp = findViewById(R.id.img_upload_ktp);
        img_upload_outlet = findViewById(R.id.img_upload_outlet);
        layout_overlay = findViewById(R.id.layout_overlay);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        layout_galeri_selected = findViewById(R.id.layout_galeri_selected);
        img_galeri_selected = findViewById(R.id.img_galeri_selected);
        layout_zoom = findViewById(R.id.layout_zoom);
        pb_map = findViewById(R.id.pb_map);

        txt_negara.setText(R.string.indonesia);

        //inisialisasi collapsing toolbar
        initToolbar();
        //inisialisasi popup galeri
        initGaleri();

        //button upload ktp
        img_upload_ktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(CustomerDetail.this, UPLOAD_KTP, 1);
            }
        });

        //button upload outlet
        img_upload_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFotoOutlet.size() < 5){
                    Pix.start(CustomerDetail.this, UPLOAD_OUTLET, 5 - listFotoOutlet.size());
                }
            }
        });

        //tampilkan foto KTP di popup
        img_ktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tampilkan foto ktp
                initView(listFotoKtp);
            }
        });

        //tampilkan foto Outlet di popup
        img_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tampilkan foto outlet
                initView(listFotoOutlet);
            }
        });

        //Update lokasi
        findViewById(R.id.txt_update_lokasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null && !loading_location){
                    loading_location = true;
                    pb_map.setVisibility(View.VISIBLE);

                    manager = new GoogleLocationManager(CustomerDetail.this, new GoogleLocationManager.LocationUpdateListener() {
                        @Override
                        public void onChange(Location location) {
                            if(marker != null){
                                mMap.clear();
                            }

                            lokasi = new LatLng(location.getLatitude(), location.getLongitude());

                            //Update lokasi outlet & TextView
                            //outlet.setLokasi(lokasi.latitude, lokasi.longitude);
                            //txt_lokasi.setText(outlet.getStringLokasi());

                            //Update marker
                            marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Outlet").draggable(true));
                            marker.setPosition(lokasi);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
                            manager.stopLocationUpdates();

                            loading_location = false;
                            pb_map.setVisibility(View.INVISIBLE);
                        }
                    });
                    manager.startLocationUpdates();
                }
                else{
                    Toast.makeText(CustomerDetail.this, "Map tidak dapat menampilkan lokasi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button simpan data Customer
        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek validasi input
                if(fotoKtp == null){
                    Toast.makeText(CustomerDetail.this, "Foto KTP belum diisi", Toast.LENGTH_SHORT).show();
                }
                else if(pendingFotoOutlet.size() == 0){
                    Toast.makeText(CustomerDetail.this, "Upload minimal 1 foto Outlet", Toast.LENGTH_SHORT).show();
                }
                else if(txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") ||
                        txt_no_hp.getText().toString().equals("") || txt_no_ktp.getText().toString().equals("") ||
                        txt_nama_pemilik.getText().toString().equals("")){
                    Toast.makeText(CustomerDetail.this, "Pastikan semua input sudah terisi", Toast.LENGTH_SHORT).show();
                }
                else if(provinsiSelected.equals("")){
                    Toast.makeText(CustomerDetail.this, "Isi informasi Provinsi terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(kotaSelected.equals("")){
                    Toast.makeText(CustomerDetail.this, "Isi informasi Kota terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(!fotoKtp.isUploaded()){
                    Toast.makeText(CustomerDetail.this, "Foto KTP belum ter-upload", Toast.LENGTH_SHORT).show();
                }
                else if(!isAllOutletUploaded()){
                    Toast.makeText(CustomerDetail.this, "Belum semua foto Outlet ter-upload", Toast.LENGTH_SHORT).show();
                }
                else if(lokasi == null){
                    Toast.makeText(CustomerDetail.this, "Lokasi outlet belum ditentukan", Toast.LENGTH_SHORT).show();
                }
                else{
                    simpanDataCustomer();
                }
            }
        });

        //Jika ada data Customer di Intent, maka mode read only Customer (tidak bisa menyimpan ke database)
        if(getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            id_customer = getIntent().getStringExtra(Constant.EXTRA_ID_CUSTOMER);
            btn_simpan.setVisibility(View.INVISIBLE);
            lbl_area.setVisibility(View.VISIBLE);
            txt_area.setVisibility(View.VISIBLE);
            initCustomer();
        }
        else{
            //jika mode input Customer, muat data provinsi untuk pilihan dropdown
            loadProvinsi(true, "");

            //Ketika kota diklik, tampilkan dialog SearchableSpinner kota
            txt_kota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!idProvinsi.equals("")){
                        if(dialogKota == null){
                            dialogKota = new SearchableSpinnerDialog(CustomerDetail.this, new SearchableSpinnerDialog.SearchableSpinnerListener() {
                                @Override
                                public void onSelected(String id, String value) {
                                    kotaSelected = value;
                                    txt_kota.setText(value);
                                }

                                @Override
                                public void onLoad(boolean init, String search) {
                                    loadKota(init, idProvinsi, search);
                                }
                            }, listKota);
                        }

                        dialogKota.showDialog();
                    }
                    else{
                        Toast.makeText(CustomerDetail.this, "Pilih provinsi terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Ketika provinsi diklik, tampilkan dialog SearchableSpinner provinsi
            txt_provinsi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogProvinsi == null){
                        dialogProvinsi = new SearchableSpinnerDialog(CustomerDetail.this,
                                new SearchableSpinnerDialog.SearchableSpinnerListener() {
                                    @Override
                                    public void onSelected(String id, String value) {
                                        idProvinsi = id;
                                        provinsiSelected = value;
                                        txt_provinsi.setText(value);

                                        //reset pilihan kota jika provinsi diganti
                                        kotaSelected = "";
                                        txt_kota.setText(R.string.customer_detail_pilih_kota);
                                        dialogKota = null;
                                        loadKota(true, idProvinsi, "");
                                    }

                                    @Override
                                    public void onLoad(boolean init, String search) {
                                        loadProvinsi(init, search);
                                    }
                                }, listProvinsi);
                    }

                    dialogProvinsi.showDialog();
                }
            });
        }
    }

    private void initToolbar(){
        AppBarLayout appbar = findViewById(R.id.appbar);
        collapsing = findViewById(R.id.collapsing_toolbar);
        collapsing.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0) {
                    collapsing.setTitle("Customer Detail");
                    isShow = true;
                } else if(isShow) {
                    collapsing.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void loadProvinsi(final boolean init, String search){
        if(init){
            loadedProvinsi = 0;
        }

        //System.out.println(loadedProvinsi);
        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s", loadedProvinsi,
                10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_PROVINSI + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {

                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listProvinsi.clear();
                        }

                        if(dialogProvinsi != null){
                            dialogProvinsi.getAdapter().notifyDataSetChanged();
                            dialogProvinsi.setLoading(false);
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listProvinsi.clear();
                            }

                            JSONArray provinsi_list = new JSONObject(result).getJSONArray("provinsi_list");
                            for(int i = 0; i < provinsi_list.length(); i++){
                                JSONObject provinsi = provinsi_list.getJSONObject(i);
                                listProvinsi.add(new SimpleObjectModel(provinsi.getString("id_provinsi"),
                                        provinsi.getString("nama")));
                                loadedProvinsi += 1;
                            }

                            if(dialogProvinsi != null){
                                dialogProvinsi.getAdapter().notifyDataSetChanged();
                                dialogProvinsi.setLoading(false);
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(CustomerDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            if(dialogProvinsi != null){
                                dialogProvinsi.setLoading(false);
                            }
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                        if(dialogProvinsi != null){
                            dialogProvinsi.setLoading(false);
                        }
                    }
                }));
    }

    private void loadKota(final boolean init, String id, String search){
        if(init){
            loadedKota = 0;
        }

        //System.out.println(loadedKota);
        String parameter = String.format(Locale.getDefault(), "?id_provinsi=%s&start=%d&limit=%d&search=%s",
                id, loadedKota, 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_KOTA + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {

                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listKota.clear();
                        }

                        if(dialogKota != null){
                            dialogKota.getAdapter().notifyDataSetChanged();
                            dialogKota.setLoading(false);
                        }
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listKota.clear();
                            }

                            JSONArray kota_list = new JSONObject(result).getJSONArray("kota_list");
                            for(int i = 0; i < kota_list.length(); i++){
                                JSONObject kota = kota_list.getJSONObject(i);
                                listKota.add(new SimpleObjectModel(kota.getString("id_kota"),
                                        kota.getString("nama")));
                                loadedKota += 1;
                            }

                            if(dialogKota != null){
                                dialogKota.getAdapter().notifyDataSetChanged();
                                dialogKota.setLoading(false);
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(CustomerDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            if(dialogKota != null){
                                dialogKota.setLoading(false);
                            }
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                        if(dialogKota != null){
                            dialogKota.setLoading(false);
                        }
                    }
                }));
    }

    private void initGaleri(){
        //Inisialisasi popup detail foto galeri
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imgWidth = displayMetrics.widthPixels - displayMetrics.widthPixels/7;
        imgHeight = displayMetrics.heightPixels - displayMetrics.heightPixels/5;

        //Inisialisasi animasi popup
        anim_popin = AnimationUtils.loadAnimation(this, R.anim.anim_pop_in);
        anim_popout = AnimationUtils.loadAnimation(this, R.anim.anim_pop_out);
        anim_popout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                detail=false;
                layout_overlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_galeri_selected.startAnimation(anim_popout);
                //img_galeri_selected.startAnimation(anim_popout);
            }
        });

        //Next foto dalam galeri
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage < listImage.size() - 1){
                    selectedImage++;
                }
                else{
                    selectedImage = 0;
                }

                Glide.with(CustomerDetail.this).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        //Previous foto dalam galeri
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage > 0){
                    selectedImage--;

                }
                else{
                    selectedImage = listImage.size() - 1;
                }

                Glide.with(CustomerDetail.this).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });
    }

    private void initView(List<String> images){
        //Fungsi untuk menampilkan foto secara popup
        if(images.size() > 0){
            if(images.size() == 1){
                btn_next.setVisibility(View.INVISIBLE);
                btn_previous.setVisibility(View.INVISIBLE);
            }
            else{
                btn_next.setVisibility(View.VISIBLE);
                btn_previous.setVisibility(View.VISIBLE);
            }

            listImage = images;
            Glide.with(this).load(listImage.get(0)).apply(new RequestOptions().
                    override(imgWidth, imgHeight)).into(img_galeri_selected);
            layout_zoom.zoomTo(1, false);
            layout_overlay.setVisibility(View.VISIBLE);
            detail = true;

            layout_galeri_selected.startAnimation(anim_popin);
            //img_galeri_selected.startAnimation(anim_popin);
        }
    }

    private void simpanDataCustomer(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("nama_pemilik", txt_nama_pemilik.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("no_ktp", txt_no_ktp.getText().toString());
        //body.add("area", AppSharedPreferences.getRegional(this));
        body.add("no_hp", txt_no_hp.getText().toString());
        body.add("latitude", lokasi.latitude);
        body.add("longitude", lokasi.longitude);
        body.add("kota", kotaSelected);
        body.add("provinsi", provinsiSelected);
        body.add("negara", txt_negara.getText().toString());
        body.add("id_gambar_ktp", fotoKtp.getId());

        ArrayList<String> listFoto = new ArrayList<>();
        for(UploadModel u : pendingFotoOutlet){
            listFoto.add(u.getId());
        }
        body.add("id_gambar_lokasi", new JSONArray(listFoto));

        ApiVolleyManager.getInstance().addRequest(CustomerDetail.this, Constant.URL_CUSTOMER_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();

                        Toast.makeText(CustomerDetail.this, result, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CustomerDetail.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private boolean isAllOutletUploaded(){
        boolean isAllUploaded = true;
        for(UploadModel u : pendingFotoOutlet){
            if(!u.isUploaded()){
                isAllUploaded = false;
                break;
            }
        }

        return isAllUploaded;
    }

    private void initCustomer(){
        if(!id_customer.equals("")){
            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_DETAIL + id_customer,
                    ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                    new AppRequestCallback(new AppRequestCallback.RequestListener() {
                @Override
                public void onEmpty(String message) {
                    Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject customer = new JSONObject(result).getJSONObject("customer");
                        JSONArray imageList = new JSONObject(result).getJSONArray("image_list");

                        txt_nama.setText(customer.getString("nama"));
                        txt_nama_pemilik.setText(customer.getString("nama_pemilik"));
                        txt_alamat.setText(customer.getString("alamat"));
                        txt_no_ktp.setText(customer.getString("no_ktp"));
                        txt_area.setText(customer.getString("area"));
                        txt_npwp.setText(customer.getString("npwp"));
                        txt_no_hp.setText(customer.getString("no_hp"));
                        txt_kota.setText(customer.getString("kota"));
                        txt_provinsi.setText(customer.getString("provinsi"));
                        txt_negara.setText(customer.getString("negara"));

                        for(int i = 0; i < imageList.length(); i++){
                            listFotoOutlet.add(imageList.getJSONObject(i).getString("image"));
                        }

                        if(!customer.getString("gambar_ktp").equals("null") && !customer.getString("gambar_ktp").equals("")){
                            img_ktp.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            listFotoKtp.add(customer.getString("gambar_ktp"));
                            Glide.with(CustomerDetail.this).load(customer.getString("gambar_ktp")).into(img_ktp);
                        }

                        if(listFotoOutlet.size() > 0){
                            img_outlet.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Glide.with(CustomerDetail.this).load(listFotoOutlet.get(0)).into(img_outlet);
                        }

                        if(!customer.getString("latitude").equals("null") && !customer.getString("longitude").equals("null") ){
                            lokasi = new LatLng(customer.getDouble("latitude"), customer.getDouble("longitude"));
                            marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Customer").draggable(true));
                            marker.setPosition(lokasi);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(CustomerDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final CollapsingToolbarLayout collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        final ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapView != null){
            mapView.setListener(new ScrollableMapView.OnTouchListener() {
                @Override
                public void onTouch() {
                    collapsing_toolbar.requestDisallowInterceptTouchEvent(true);
                }
            });
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                lokasi = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });

        if(!getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            manager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
                @Override
                public void onChange(Location location) {
                    if(marker == null){
                        lokasi = new LatLng(location.getLatitude(), location.getLongitude());

                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Customer").draggable(true));
                        marker.setPosition(lokasi);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
                        manager.stopLocationUpdates();
                    }
                }
            });
            manager.startLocationUpdates();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case AppLocationManager.ACTIVATE_LOCATION:{
                if(manager != null){
                    manager.startLocationUpdates();
                }
                break;
            }
            case UPLOAD_KTP:{
                if(data != null){
                    try{
                        listFotoKtp.clear();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));
                        bitmap = Converter.resizeBitmap(bitmap, 1200);

                        //UploadModel uploadModel = new UploadModel(resized);
                        img_ktp.setImageBitmap(bitmap);
                        img_ktp.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        overlay_ktp.setVisibility(View.VISIBLE);
                        bar_ktp.setVisibility(View.VISIBLE);

                        fotoKtp = new UploadModel(bitmap);
                        fotoKtp.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
                        upload(fotoKtp, UPLOAD_KTP);
                    }
                    catch (IOException e){
                        Log.e(Constant.TAG, e.getMessage());
                    }
                }
                break;
            }
            case UPLOAD_OUTLET:{
                if(data != null){
                    try{
                        pendingFotoOutlet.clear();

                        ArrayList<String> listPath = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                        for(int i = 0; i < listPath.size(); i++){
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    Uri.fromFile(new File(listPath.get(i))));
                            bitmap = Converter.resizeBitmap(bitmap, 1200);

                            UploadModel uploadModel = new UploadModel(bitmap);
                            uploadModel.setUrl(Uri.fromFile(new File(listPath.get(i))).toString());
                            pendingFotoOutlet.add(uploadModel);

                            if(i == 0){
                                img_outlet.setImageBitmap(pendingFotoOutlet.get(0).getBitmap());
                                img_outlet.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                overlay_outlet.setVisibility(View.VISIBLE);
                                bar_outlet.setVisibility(View.VISIBLE);
                            }

                            upload(uploadModel, UPLOAD_OUTLET);
                        }
                    }
                    catch (IOException e){
                        Log.e(Constant.TAG, e.getMessage());
                    }
                }
                break;
            }
            default:super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void upload(final UploadModel upload, final int uploadCode){
        if(uploadCode == UPLOAD_KTP){
            ApiVolleyManager.getInstance().addMultipartRequest(CustomerDetail.this, Constant.URL_UPLOAD_KTP,
                    Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                    Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try{
                                JSONObject jsonresult = new JSONObject(result);
                                int status = jsonresult.getJSONObject("metadata").getInt("status");
                                String message = jsonresult.getJSONObject("metadata").getString("message");

                                if(status == 200){
                                    upload.setUploaded(true);
                                    upload.setId(jsonresult.getJSONObject("response").getString("id"));
                                    listFotoKtp.add(upload.getUrl());

                                    overlay_ktp.setVisibility(View.INVISIBLE);
                                    bar_ktp.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (JSONException e){
                                Toast.makeText(CustomerDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                                Log.e(Constant.TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String result) {
                            Toast.makeText(CustomerDetail.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, result);
                        }
                    });
        }
        else if(uploadCode == UPLOAD_OUTLET){
            ApiVolleyManager.getInstance().addMultipartRequest(CustomerDetail.this,
                    Constant.URL_UPLOAD_OUTLET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                    Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject jsonresult = new JSONObject(result);
                        int status = jsonresult.getJSONObject("metadata").getInt("status");
                        String message = jsonresult.getJSONObject("metadata").getString("message");

                        if(status == 200){
                            upload.setUploaded(true);
                            upload.setId(jsonresult.getJSONObject("response").getString("id"));
                            listFotoOutlet.add(upload.getUrl());

                            if(isAllOutletUploaded()){
                                bar_outlet.setVisibility(View.INVISIBLE);
                                overlay_outlet.setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(CustomerDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(CustomerDetail.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, result);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        if(!detail){
            if(!getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Batal Tambah");
                builder.setMessage("Anda yakin ingin membatalkan tambah Customer?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomerDetail.super.onBackPressed();
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            layout_galeri_selected.startAnimation(anim_popout);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(manager != null){
            manager.stopLocationUpdates();
        }
    }
}