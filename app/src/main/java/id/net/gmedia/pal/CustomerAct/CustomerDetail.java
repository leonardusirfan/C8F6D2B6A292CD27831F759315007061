package id.net.gmedia.pal.CustomerAct;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
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
import com.otaliastudios.zoom.ZoomLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLocationManager;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import id.net.gmedia.pal.Util.JSONBuilder;
import id.net.gmedia.pal.Util.ScrollableMapView;

public class CustomerDetail extends AppCompatActivity implements OnMapReadyCallback {

    private String id_customer = "";

    private List<String> listImage = new ArrayList<>();

    private final int UPLOAD_KTP = 999;
    private final int UPLOAD_OUTLET = 998;

    private GoogleMap mMap;
    private GoogleLocationManager manager;
    private LatLng lokasi;
    private Marker marker;

    private EditText txt_nama, txt_alamat, txt_no_ktp, txt_area, txt_npwp, txt_no_hp,
            txt_nama_pemilik, txt_kota, txt_provinsi, txt_negara;
    private ImageView img_ktp, img_outlet, overlay_ktp, overlay_outlet;
    private ProgressBar bar_ktp, bar_outlet;
    private ImageView img_galeri_selected;
    private ConstraintLayout layout_overlay;
    private CardView layout_galeri_selected;
    private ZoomLayout layout_zoom;
    private Button btn_next, btn_previous;
    private CollapsingToolbarLayout collapsing;

    //Variabel UI galeri (animasi, foto tampil)
    private Animation anim_popin, anim_popout;
    private int selectedImage = 0;
    private int imgHeight = 0;
    private int imgWidth = 0;

    private UploadModel fotoKtp;
    private List<UploadModel> fotoOutlet = new ArrayList<>();

    private List<String> listFotoKtp = new ArrayList<>();
    private List<String> listFotoOutlet = new ArrayList<>();

    //flag apakah galeri sedang menampilkan foto detail secara popup atau tidak
    private boolean detail = false;

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

        txt_negara.setText("Indonesia");

        initToolbar();
        initGaleri();

        img_upload_ktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(CustomerDetail.this, UPLOAD_KTP, 1);
            }
        });

        img_upload_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(CustomerDetail.this, UPLOAD_OUTLET, 5);
            }
        });

        img_ktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tampilkan foto ktp
                initView(listFotoKtp);
            }
        });

        img_outlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tampilkan foto outlet
                initView(listFotoOutlet);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek validasi input
                if(fotoKtp == null){
                    Toast.makeText(CustomerDetail.this, "Foto KTP belum diisi", Toast.LENGTH_SHORT).show();
                }
                else if(fotoOutlet.size() == 0){
                    Toast.makeText(CustomerDetail.this, "Upload minimal 1 foto Outlet", Toast.LENGTH_SHORT).show();
                }
                else if(txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") ||
                        txt_no_hp.getText().toString().equals("") || txt_no_ktp.getText().toString().equals("") ||
                        txt_nama_pemilik.getText().toString().equals("")){
                    Toast.makeText(CustomerDetail.this, "Pastikan semua input sudah terisi", Toast.LENGTH_SHORT).show();
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

        if(getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            id_customer = getIntent().getStringExtra(Constant.EXTRA_ID_CUSTOMER);
            btn_simpan.setVisibility(View.INVISIBLE);
            lbl_area.setVisibility(View.VISIBLE);
            txt_area.setVisibility(View.VISIBLE);
            initCustomer();
        }
    }

    private void initToolbar(){
        AppBarLayout appbar = findViewById(R.id.appbar);
        collapsing = findViewById(R.id.collapsing_toolbar);
        collapsing.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

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
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("nama_pemilik", txt_nama_pemilik.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("no_ktp", txt_no_ktp.getText().toString());
        //body.add("area", AppSharedPreferences.getRegional(this));
        body.add("no_hp", txt_no_hp.getText().toString());
        body.add("latitude", lokasi.latitude);
        body.add("longitude", lokasi.longitude);
        System.out.println("LATITUDE " + marker.getPosition().latitude);
        System.out.println("LONGITUDE " + marker.getPosition().longitude);
        body.add("kota", txt_kota.getText().toString());
        body.add("provinsi", txt_provinsi.getText().toString());
        body.add("negara", txt_negara.getText().toString());
        body.add("id_gambar_ktp", fotoKtp.getId());

        ArrayList<String> listFoto = new ArrayList<>();
        for(UploadModel u : fotoOutlet){
            listFoto.add(u.getId());
        }
        body.add("id_gambar_lokasi", new JSONArray(listFoto));

        ApiVolleyManager.getInstance().addRequest(CustomerDetail.this, Constant.URL_CUSTOMER_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(CustomerDetail.this, result, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CustomerDetail.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(CustomerDetail.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private boolean isAllOutletUploaded(){
        boolean isAllUploaded = true;
        for(UploadModel u : fotoOutlet){
            if(!u.isUploaded()){
                isAllUploaded = false;
                break;
            }
        }

        return isAllUploaded;
    }

    private void initCustomer(){
        if(!id_customer.equals("")){
            ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_DETAIL + id_customer, ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
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

                        if(!customer.getString("gambar_ktp").equals("null")){
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
                        Log.e("Detail Customer", e.getMessage());
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));

                        img_ktp.setImageBitmap(bitmap);
                        img_ktp.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        overlay_ktp.setVisibility(View.VISIBLE);
                        bar_ktp.setVisibility(View.VISIBLE);

                        fotoKtp = new UploadModel(bitmap);
                        fotoKtp.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
                        upload(fotoKtp, UPLOAD_KTP);
                    }
                    catch (IOException e){
                        Log.e("Upload", e.getMessage());
                    }
                }
                break;
            }
            case UPLOAD_OUTLET:{
                if(data != null){
                    try{
                        ArrayList<String> listPath = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                        for(int i = 0; i < listPath.size(); i++){
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    Uri.fromFile(new File(listPath.get(i))));
                            UploadModel uploadModel = new UploadModel(bitmap);
                            uploadModel.setUrl(Uri.fromFile(new File(listPath.get(i))).toString());
                            fotoOutlet.add(uploadModel);

                            if(i == 0){
                                img_outlet.setImageBitmap(fotoOutlet.get(0).getBitmap());
                                img_outlet.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                overlay_outlet.setVisibility(View.VISIBLE);
                                bar_outlet.setVisibility(View.VISIBLE);
                            }

                            upload(uploadModel, UPLOAD_OUTLET);
                        }
                    }
                    catch (IOException e){
                        Log.e("Upload", e.getMessage());
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
                        Log.e("Upload KTP", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(CustomerDetail.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                    Log.e("Upload KTP", result);
                }
            });
        }
        else if(uploadCode == UPLOAD_OUTLET){
            ApiVolleyManager.getInstance().addMultipartRequest(CustomerDetail.this, Constant.URL_UPLOAD_OUTLET, Constant.getTokenHeader(AppSharedPreferences.getId(this)), Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
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
                        Log.e("Upload Outlet", e.getMessage());
                    }
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(CustomerDetail.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                    Log.e("Upload Outlet", result);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!detail){
            super.onBackPressed();
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
