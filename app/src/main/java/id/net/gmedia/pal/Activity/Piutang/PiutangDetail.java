package id.net.gmedia.pal.Activity.Piutang;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.PiutangModel;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.PetaOutlet;
import id.net.gmedia.pal.Adapter.PiutangDetailAdapter;
import id.net.gmedia.pal.R;

import id.net.gmedia.pal.Util.AppDialogKonfirmasi;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import com.leonardus.irfan.Haversine;

public class PiutangDetail extends AppCompatActivity {

    //Variabel global id upload
    private final int UPLOAD_BUKTI = 999;
    //Variabel global jenis pembayaran
    private String type = "tunai";
    //Variabel global Customer
    public CustomerModel customer;

    //Variabel filter & loadmore
    private boolean tempo = false;
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel lokasi
    private GoogleLocationManager manager;
    private Location current_location;
    private LatLng outlet_location;

    //Variabel upload
    private UploadModel upload;

    //Variabel flag detail pelunasan
    private boolean mulai_lunasi = false;
    private boolean lunasi = false;
    private double sisa_pelunasan = 0;
    private double jumlah = 0;
    private boolean retur = false;

    private double jum_pembayaran;
    private double jum_retur;

    //Variabel UI
    private LinearLayout layout_tunai, layout_bank, layout_giro, layout_pembayaran;
    private TextView txt_nama, txt_alamat, txt_piutang, txt_jumlah,
            txt_total_bayar, txt_jarak, txt_keterangan;
    private SlidingUpPanelLayout slidingpanel;
    private ImageView overlay_bukti, img_bukti;
    private ProgressBar bar_bukti, pb_map;
    private Spinner spn_akun_tunai, spn_akun_bank, spn_nomor_giro;
    private View img_refresh;

    //Variabel data akun tunai & bank
    private List<SimpleObjectModel> listAkunTunai;
    private List<SimpleObjectModel> listAkunBank;
    private List<String> spinner_item_tunai, spinner_item_bank;
    private ArrayAdapter<String> spinner_adapter_tunai, spinner_adapter_bank;
    //Variabel data giro
    private List<SimpleObjectModel> listNomorGiro;
    private List<String> spinner_item_giro;
    private ArrayAdapter<String> spinner_adapter_giro;

    //Variabel data piutang
    private List<PiutangModel> listPiutang = new ArrayList<>();
    private PiutangDetailAdapter adapter;
    private List<JSONObject> listBayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Piutang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi data customer
        if(getIntent().hasExtra(Constant.EXTRA_CUSTOMER)){
            Gson gson = new Gson();
            customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        }

        //Inisialisasi UI
        final RadioGroup rb_parent = findViewById(R.id.rb_parent);
        layout_tunai = findViewById(R.id.layout_tunai);
        layout_giro = findViewById(R.id.layout_giro);
        layout_bank = findViewById(R.id.layout_bank);
        layout_pembayaran = findViewById(R.id.layout_pembayaran);
        slidingpanel = findViewById(R.id.layout_parent);
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_total_bayar = findViewById(R.id.txt_total_bayar);
        txt_jarak = findViewById(R.id.txt_jarak);
        spn_akun_tunai = findViewById(R.id.spn_akun_tunai);
        spn_akun_bank = findViewById(R.id.spn_akun_bank);
        spn_nomor_giro = findViewById(R.id.spn_nomor_giro);
        txt_keterangan = findViewById(R.id.txt_keterangan);
        overlay_bukti = findViewById(R.id.overlay_bukti);
        img_bukti = findViewById(R.id.img_bukti);
        bar_bukti = findViewById(R.id.bar_bukti);
        img_refresh = findViewById(R.id.img_refresh);
        pb_map = findViewById(R.id.pb_map);

        //Inisialisasi Spinner akun tunai
        spinner_item_tunai = new ArrayList<>();
        spinner_adapter_tunai = new ArrayAdapter<>(
                PiutangDetail.this, android.R.layout.simple_spinner_item, spinner_item_tunai);
        spinner_adapter_tunai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_akun_tunai.setAdapter(spinner_adapter_tunai);

        //Inisialisasi Spinner akun bank
        spinner_item_bank = new ArrayList<>();
        spinner_adapter_bank = new ArrayAdapter<>(
                PiutangDetail.this, android.R.layout.simple_spinner_item, spinner_item_bank);
        spinner_adapter_bank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_akun_bank.setAdapter(spinner_adapter_bank);

        //Inisialisasi Spinner Giro
        spinner_item_giro = new ArrayList<>();
        spinner_adapter_giro = new ArrayAdapter<>(
                PiutangDetail.this, android.R.layout.simple_spinner_item, spinner_item_giro);
        spinner_adapter_giro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_nomor_giro.setAdapter(spinner_adapter_giro);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_piutang = findViewById(R.id.rv_piutang);
        rv_piutang.setItemAnimator(new DefaultItemAnimator());
        rv_piutang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PiutangDetailAdapter(this, listPiutang);
        rv_piutang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadPiutang(false);
            }
        };

        //button upload bukti transfer piutang
        findViewById(R.id.img_upload_bukti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(PiutangDetail.this, UPLOAD_BUKTI, 1);
            }
        });

        //Action listener radio button Jenis pembayaran
        rb_parent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               updateLayoutPembayaran(checkedId);
            }
        });

        //buka activity peta antara sales dan customer/outlet
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

        //Button untuk menambah data giro baru pelanggan
        findViewById(R.id.txt_tambah_giro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(PiutangDetail.this, ListGiroDetail.class);
                i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                startActivity(i);*/
                showDialogGiro();
            }
        });

        //button start pelunasan piutang,
        //masukkan nominal pembayaran, dan memilih nota2 yang dilunasi
        findViewById(R.id.btn_start_lunasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(PiutangDetail.this,
                        R.layout.popup_piutang, 85, 40);

                //Inisialisasi UI
                final TextView txt_nominal, btn_batal, btn_bayar;
                txt_nominal = dialog.findViewById(R.id.txt_nominal);
                RadioGroup rb_jenis_pelunasan = dialog.findViewById(R.id.rb_jenis_pelunasan);
                btn_bayar = dialog.findViewById(R.id.btn_bayar);
                btn_batal = dialog.findViewById(R.id.btn_batal);

                initPembayaranPiutang();
                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f",jum_pembayaran));
                initReturPiutang();

                retur = false;
                rb_jenis_pelunasan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.rb_pembayaran:
                                retur = false;
                                //txt_nominal.setEnabled(true);
                                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f",jum_pembayaran));
                                break;
                            case R.id.rb_retur:
                                retur = true;
                                //txt_nominal.setEnabled(false);
                                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f", jum_retur));
                                break;
                        }
                    }});


                btn_batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                //Button Pembayaran piutang
                btn_bayar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //cek validasi
                        if(txt_nominal.getText().toString().equals("")){
                            Toast.makeText(PiutangDetail.this, "Nominal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                        else if(Double.parseDouble(txt_nominal.getText().toString()) <= 0){
                            if(retur){
                                Toast.makeText(PiutangDetail.this, "Anda tidak mempunyai kredit retur", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(PiutangDetail.this, "Nominal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(retur && Double.parseDouble(txt_nominal.getText().toString())  > jum_retur ||
                                !retur && Double.parseDouble(txt_nominal.getText().toString())  > jum_pembayaran){

                            if(retur){
                                Toast.makeText(PiutangDetail.this, "Nominal pembayaran tidak boleh melebihi kredit", Toast.LENGTH_SHORT).show();
                                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f", jum_retur));
                            }
                            else{
                                Toast.makeText(PiutangDetail.this, "Nominal pembayaran tidak boleh melebihi seluruh nota", Toast.LENGTH_SHORT).show();
                                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f", jum_pembayaran));
                            }
                        }
                        else{
                            //inisialisasi pemilihan nota yang dilunasi
                            jumlah = Double.parseDouble(txt_nominal.getText().toString());
                            sisa_pelunasan = jumlah;
                            String jum = "Sisa : " + Converter.doubleToRupiah(sisa_pelunasan);
                            txt_jumlah.setText(jum);

                            txt_jumlah.setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_lunasi).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_start_lunasi).setVisibility(View.GONE);

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

        //button lunasi piutang
        findViewById(R.id.btn_lunasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek apakah nominal pembayaran sudah habis
                if(sisa_pelunasan > 0){
                    Toast.makeText(PiutangDetail.this, "Sisa pelunasan harus dihabiskan", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(retur){
                        layout_pembayaran.setVisibility(View.GONE);
                        layout_tunai.setVisibility(View.GONE);
                        layout_bank.setVisibility(View.GONE);
                        layout_giro.setVisibility(View.GONE);
                    }
                    else{
                        layout_pembayaran.setVisibility(View.VISIBLE);
                        updateLayoutPembayaran(rb_parent.getCheckedRadioButtonId());
                    }

                    if(manager == null){
                        //Inisialisasi manager lokasi
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
                                pb_map.setVisibility(View.INVISIBLE);
                            }
                        });
                        manager.startLocationUpdates();
                        pb_map.setVisibility(View.VISIBLE);
                    }

                    img_refresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(manager != null){
                                manager.stopLocationUpdates();
                            }

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
                                    pb_map.setVisibility(View.INVISIBLE);
                                }
                            });
                            manager.startLocationUpdates();
                            pb_map.setVisibility(View.VISIBLE);
                        }
                    });

                    //inisialisasi UI total pembayaran
                    txt_total_bayar.setText(Converter.doubleToRupiah(jumlah));

                    //tampilkan panel pelunasan piutang
                    slidingpanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    lunasi = true;
                }
            }
        });

        //button pembayaran piutang
        findViewById(R.id.btn_bayar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBayar = new ArrayList<>();
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

                //Validasi input
                if(current_location == null){
                    Toast.makeText(PiutangDetail.this, "Lokasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                }
                else if(!retur && type.equals("tunai") && spn_akun_tunai.getSelectedItemPosition() == -1){
                    Toast.makeText(PiutangDetail.this, "Akun belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(!retur && type.equals("bank") && spn_akun_bank.getSelectedItemPosition() == -1){
                    Toast.makeText(PiutangDetail.this, "Akun belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(!retur && type.equals("giro") && spn_nomor_giro.getSelectedItemPosition() == -1){
                    Toast.makeText(PiutangDetail.this, "Nomor giro kosong", Toast.LENGTH_SHORT).show();
                }
                else if(!retur && type.equals("bank") && upload == null){
                    Toast.makeText(PiutangDetail.this, "Bukti transfer belum di-upload", Toast.LENGTH_SHORT).show();
                }
                else if(!retur && type.equals("bank") && !upload.isUploaded()){
                    Toast.makeText(PiutangDetail.this, "Bukti transfer belum selesai ter-upload", Toast.LENGTH_SHORT).show();
                }
                else{
                    //lakukan pelunasan piutang
                    AppDialogKonfirmasi.showKonfirmasi(PiutangDetail.this, "Konfirmasi Pelunasan",
                            "Yakin ingin melakukan pelunasan piutang ini?", new AppDialogKonfirmasi.KonfirmasiListener() {
                                @Override
                                public void onLanjut() {
                                    lunasiPiutang(listBayar);
                                }
                            });
                }
            }
        });

        //muat data piutang
        loadPiutang(true);
    }

    /*private void cetakPiutang(){
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_pal_nota);
        printerManager = new NotaPrinter(this, Bitmap.createScaledBitmap(
                logo, 170, 170, false));
        printerManager.startService();
        printerManager.setListener(new BluetoothPrinter.BluetoothListener() {
            @Override
            public void onBluetoothConnected() {
                List<Item> listItem = new ArrayList<>();
                double total_diskon = 0;

                for(BarangModel b : listBarang){
                    listItem.add(new Item(b.getNama(), b.getJumlah(), b.getHarga()));
                    total_diskon += b.getDiskon();
                }

                Transaksi transaksi = new Transaksi(txt_nama.getText().toString(),
                        AppSharedPreferences.getNama(DaftarSODetail.this),
                        txt_nota.getText().toString(), new Date(), new Date(), listItem);
                transaksi.setTunai(nota.getTotal());
                transaksi.setDiskon(total_diskon);

                printerManager.print(transaksi);
            }

            @Override
            public void onBluetoothFailed(String message) {
                Toast.makeText(DaftarSODetail.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void loadPiutang(final boolean init){
        //membaca data piutang dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());
        body.add("filter", tempo?"tempo":"");
        body.add("status", "");
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("limit", 10);
        body.add("search", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PIUTANG_CUSTOMER,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listPiutang.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

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

                            double latitude = obj.getJSONObject("pelanggan").getString("latitude").equals("null")?0:obj.getJSONObject("pelanggan").getDouble("latitude");
                            double longitude = obj.getJSONObject("pelanggan").getString("longitude").equals("null")?0:obj.getJSONObject("pelanggan").getDouble("longitude");
                            outlet_location = new LatLng(latitude, longitude);

                            JSONArray array = obj.getJSONArray("nota_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject nota = array.getJSONObject(i);

                                int type = nota.getString("tipe").equals("canvas")?Constant.PENJUALAN_CANVAS:Constant.PENJUALAN_SO;
                                listPiutang.add(new PiutangModel(nota.getString("nomor_nota"),
                                        nota.getString("nomor_nota"), nota.getDouble("piutang"), nota.getDouble("bayar"),
                                        nota.getString("tanggal"), nota.getString("tanggal_tempo"), type));
                            }

                            loadMoreScrollListener.finishLoad(array.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(PiutangDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    private void showDialogGiro(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this,
                R.layout.popup_tambah_giro, 85);

        final EditText txt_nomor, txt_bank, txt_nominal;
        final TextView txt_tgl_kadaluarsa;
        //final TextView txt_tgl_terbit;

        txt_nomor = dialog.findViewById(R.id.txt_nomor);
        txt_bank = dialog.findViewById(R.id.txt_bank);
        txt_nominal = dialog.findViewById(R.id.txt_nominal);
        //txt_tgl_terbit = dialog.findViewById(R.id.txt_tgl_terbit);
        txt_tgl_kadaluarsa = dialog.findViewById(R.id.txt_tgl_kadaluarsa);

        txt_nominal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    dialog.findViewById(R.id.layout_tgl_kadaluarsa).performClick();
                    return true;
                }
                return false;
            }
        });

        /*dialog.findViewById(R.id.layout_tgl_terbit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(PiutangDetail.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_terbit.setText(dateString);
                    }
                });
            }
        });*/

        dialog.findViewById(R.id.layout_tgl_kadaluarsa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(PiutangDetail.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_kadaluarsa.setText(dateString);
                    }
                });
            }
        });

        dialog.findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomor = txt_nomor.getText().toString();
                String bank = txt_bank.getText().toString();
                String nominal = txt_nominal.getText().toString();
                //String tgl_terbit = txt_tgl_terbit.getText().toString();
                String tgl_kadaluarsa = txt_tgl_kadaluarsa.getText().toString();

                if(nomor.equals("") || bank.equals("") || nominal.equals("")||
                        //tgl_terbit.equals("") ||
                        tgl_kadaluarsa.equals("")){
                    Toast.makeText(PiutangDetail.this, "Pastikan semua input terisi", Toast.LENGTH_SHORT).show();
                }
                else{
                    JSONBuilder body = new JSONBuilder();
                    body.add("kode_pelanggan", customer.getId());
                    body.add("nomor_giro", nomor);
                    body.add("bank", bank);
                    //body.add("tanggal_cair", tgl_terbit);
                    body.add("tanggal_expired", tgl_kadaluarsa);
                    body.add("total", nominal);

                    tambahGiro(dialog, body.create());
                }
            }
        });

        dialog.show();
    }

    private void tambahGiro(final Dialog dialog, JSONObject body){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GIRO_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body,
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(PiutangDetail.this, "Berhasil menambah giro", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        listNomorGiro.clear();
                        spinner_item_giro.clear();

                        loadGiro();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void updateLayoutPembayaran(int id){
        switch (id){
            case R.id.rb_tunai:
                layout_tunai.setVisibility(View.VISIBLE);
                layout_bank.setVisibility(View.GONE);
                layout_giro.setVisibility(View.GONE);
                type = "tunai";

                if(listAkunTunai == null){
                    loadAkun();
                }
                break;
            case R.id.rb_bank:
                layout_tunai.setVisibility(View.GONE);
                layout_bank.setVisibility(View.VISIBLE);
                layout_giro.setVisibility(View.GONE);
                type = "bank";

                if(listAkunBank == null){
                    loadAkun();
                }
                break;
            case R.id.rb_giro:
                type = "giro";
                layout_tunai.setVisibility(View.GONE);
                layout_bank.setVisibility(View.GONE);
                layout_giro.setVisibility(View.VISIBLE);

                if(listNomorGiro == null){
                    loadGiro();
                }
                break;
            case R.id.rb_retur:
                type = "retur";
                layout_tunai.setVisibility(View.GONE);
                layout_bank.setVisibility(View.GONE);
                layout_giro.setVisibility(View.GONE);
                break;
            default:
                layout_tunai.setVisibility(View.VISIBLE);
                layout_bank.setVisibility(View.GONE);
                layout_giro.setVisibility(View.GONE);
                type = "";
                break;
        }
    }

    private void initPembayaranPiutang(){
        //Inisialisasi jumlah piutang
        jum_pembayaran = 0;
        for(PiutangModel p : listPiutang){
            jum_pembayaran += p.getPiutangSisa();
        }
    }

    private void initReturPiutang(){
        //Inisialisasi jumlah piutang
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RETUR_CUSTOMER_CREDIT,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        jum_retur = 0;
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            jum_retur = new JSONObject(result).getDouble("sisa_cn");
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

    private void lunasiPiutang(List<JSONObject> listBayar){
        AppLoading.getInstance().showLoading(this);
        //Kirim data pelunasan piutang ke Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("nota_jual", new JSONArray(listBayar));
        body.add("kode_pelanggan", customer.getId());

        if(!retur){
            body.add("cara_bayar", type.equals("bank")?"transfer":type);

            switch (type) {
                case "giro":
                    body.add("nomor_giro", listNomorGiro.get(spn_nomor_giro.
                            getSelectedItemPosition()).getId());
                    break;
                case "tunai":
                    body.add("kode_akun", listAkunTunai.get(spn_akun_tunai.
                            getSelectedItemPosition()).getId());
                    break;
                case "bank":
                    body.add("kode_akun", listAkunBank.get(spn_akun_bank.
                            getSelectedItemPosition()).getId());
                    body.add("id_gambar_bukti", upload.getId());
                    break;
            }
        }
        else{
            body.add("cara_bayar", "credit_note");
        }

        body.add("keterangan", txt_keterangan.getText().toString());
        body.add("user_latitude", current_location.getLatitude());
        body.add("user_longitude", current_location.getLongitude());
        //System.out.println(body.create());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PIUTANG_PELUNASAN,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().showLoading(PiutangDetail.this);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(PiutangDetail.this, "Pelunasan berhasil", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(PiutangDetail.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                        AppLoading.getInstance().showLoading(PiutangDetail.this);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().showLoading(PiutangDetail.this);
                    }
                }));
    }

    public void updateJumlah(double update){
        //Update sisa pelunasan setiap nota dipilih
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
        //Membaca data akun dari Web Service
        String parameter = String.format(Locale.getDefault(), "?tipe=%s", type);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_AKUN + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(type.equals("tunai")){
                                listAkunTunai = new ArrayList<>();
                                JSONArray array = new JSONObject(result).getJSONArray("akun_list");
                                for(int i = 0; i < array.length(); i++){
                                    listAkunTunai.add(new SimpleObjectModel(array.getJSONObject(i).getString("kode_akun"),
                                            array.getJSONObject(i).getString("nama_akun")));
                                    spinner_item_tunai.add(array.getJSONObject(i).getString("nama_akun"));
                                }

                                spinner_adapter_tunai.notifyDataSetChanged();
                            }
                            else if(type.equals("bank")){
                                listAkunBank = new ArrayList<>();
                                JSONArray array = new JSONObject(result).getJSONArray("akun_list");
                                for(int i = 0; i < array.length(); i++){
                                    listAkunBank.add(new SimpleObjectModel(array.getJSONObject(i).getString("kode_akun"),
                                            array.getJSONObject(i).getString("nama_akun")));
                                    spinner_item_bank.add(array.getJSONObject(i).getString("nama_akun"));
                                }

                                spinner_adapter_bank.notifyDataSetChanged();
                            }
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

    private void loadGiro(){
        //Membaca data nomor giro dari Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GIRO_LIST_CAIR,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listNomorGiro = new ArrayList<>();
                        spinner_adapter_giro.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listNomorGiro = new ArrayList<>();

                            JSONArray giro_list = new JSONObject(result).getJSONArray("giro_list");
                            for(int i = 0; i < giro_list.length(); i++){
                                listNomorGiro.add(new SimpleObjectModel(giro_list.getJSONObject(i).getString("nomor_giro"),
                                        giro_list.getJSONObject(i).getString("nomor_giro")));
                                spinner_item_giro.add(giro_list.getJSONObject(i).getString("nomor_giro"));
                            }

                            spinner_adapter_giro.notifyDataSetChanged();
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

    private void uploadBukti(){
        //Upload foto bukti ke Web Service
        ApiVolleyManager.getInstance().addMultipartRequest(this, Constant.URL_UPLOAD_FOTO_PELUNASAN,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), "pic",
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

                                overlay_bukti.setVisibility(View.INVISIBLE);
                                bar_bukti.setVisibility(View.INVISIBLE);
                            }
                            else{
                                Toast.makeText(PiutangDetail.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PiutangDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(PiutangDetail.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GoogleLocationManager.ACTIVATE_LOCATION: {
                if (manager != null) {
                    manager.startLocationUpdates();
                }
                break;
            }
            case UPLOAD_BUKTI:{
                if(data != null){
                    //Inisialisasi upload gambar bukti
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));
                        bitmap = Converter.resizeBitmap(bitmap, 750);

                        img_bukti.setImageBitmap(bitmap);
                        overlay_bukti.setVisibility(View.VISIBLE);
                        bar_bukti.setVisibility(View.VISIBLE);

                        upload = new UploadModel(bitmap);
                        upload.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
                        uploadBukti();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_piutang_detail, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GoogleLocationManager.PERMISSION_LOCATION){
            if(manager != null){
                manager.startLocationUpdates();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_filter:
                //filter piutang
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
            findViewById(R.id.btn_start_lunasi).setVisibility(View.VISIBLE);
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

    @Override
    protected void onResume() {
        super.onResume();
        //loadGiro();
    }
}
