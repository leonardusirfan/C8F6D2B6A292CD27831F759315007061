package id.net.gmedia.pal.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.SetoranModel;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Adapter.SetoranSalesAdapter;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class SetoranSales extends AppCompatActivity {

    //variabel global id upload bukti
    private final int UPLOAD_BUKTI = 999;

    //Variabel global dialog upload
    private Dialog dialog_upload;
    //Variabel global upload
    private UploadModel upload;

    //variabel global total setoran sales
    private double total = 0;
    private String pembayaran = "";
    private String date_start = "";
    private String date_end = "";
    private String search = "";
    //variabel global flag apakah filter muncul atau tidak
    private boolean panel_filter = false;

    //Variabel UI
    private TextView txt_total;
    private TextView txt_tgl_mulai, txt_tgl_selesai;
    private SlidingUpPanelLayout slidingPanel;
    private AppCompatSpinner spn_pembayaran;
    private EditText txt_nominal, txt_keterangan;
    private ProgressBar bar_bukti;
    private ImageView overlay_bukti, img_bukti;

    //Variabel data setoran
    private List<SetoranModel> listSetoran = new ArrayList<>();
    private SetoranSalesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setoran_sales);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Setoran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_total = findViewById(R.id.txt_total);
        slidingPanel = findViewById(R.id.layout_parent);
        txt_tgl_mulai = findViewById(R.id.txt_tgl_mulai);
        txt_tgl_selesai = findViewById(R.id.txt_tgl_selesai);
        spn_pembayaran = findViewById(R.id.spn_pembayaran);
        txt_tgl_mulai.setText(Converter.DToString(new Date()));
        txt_tgl_selesai.setText(Converter.DToString(new Date()));

        //button menampilkan datepicker untuk set filter tanggal mulai
        findViewById(R.id.img_tgl_mulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(SetoranSales.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_mulai.setText(dateString);
                    }
                });
            }
        });

        //button menampilkan datepicker untuk set filter tanggal selesai
        findViewById(R.id.img_tgl_selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(SetoranSales.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_selesai.setText(dateString);
                    }
                });
            }
        });

        //button memuat data berdasarkan filter yang sudah diisi
        findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_start = txt_tgl_mulai.getText().toString();
                date_end = txt_tgl_selesai.getText().toString();
                pembayaran = spn_pembayaran.getSelectedItemPosition() == 0 ? "" : spn_pembayaran.getSelectedItem().toString();

                //tutup panel filter
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                panel_filter = false;

                //muat ulang data setoran
                loadSetoran();
            }
        });

        //button untuk memulai upload data transfer
        findViewById(R.id.btn_transfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inisialisasi dialog upload
                dialog_upload = DialogFactory.getInstance().createDialog(SetoranSales.this, R.layout.popup_setoran_sales,
                        80, 70);

                txt_nominal = dialog_upload.findViewById(R.id.txt_nominal);
                txt_keterangan = dialog_upload.findViewById(R.id.txt_keterangan);
                overlay_bukti = dialog_upload.findViewById(R.id.overlay_bukti);
                img_bukti = dialog_upload.findViewById(R.id.img_bukti);
                bar_bukti = dialog_upload.findViewById(R.id.bar_bukti);

                txt_nominal.setText(String.format(Locale.getDefault(), "%.0f", total));

                //button upload gambar bukti
                dialog_upload.findViewById(R.id.img_upload_bukti).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pix.start(SetoranSales.this, UPLOAD_BUKTI, 1);
                    }
                });

                dialog_upload.findViewById(R.id.btn_batal).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_upload.cancel();
                    }
                });

                //button kirim data bukti
                dialog_upload.findViewById(R.id.btn_kirim).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Validasi input
                        if(upload == null){
                            Toast.makeText(SetoranSales.this, "Upload bukti transfer terlebih dahulu", Toast.LENGTH_SHORT).show();
                        }
                        else if(!upload.isUploaded()){
                            Toast.makeText(SetoranSales.this, "Foto bukti transfer belum selesai ter-upload", Toast.LENGTH_SHORT).show();
                        }
                        else if(txt_nominal.getText().toString().equals("")){
                            Toast.makeText(SetoranSales.this, "Nominal transfer tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                        else if(Double.parseDouble(txt_nominal.getText().toString()) <= 0){
                            Toast.makeText(SetoranSales.this, "Nominal transfer tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //kirim bukti setoran
                            uploadBukti();
                        }
                    }
                });

                dialog_upload.show();
            }
        });

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_setoran = findViewById(R.id.rv_setoran);
        rv_setoran.setLayoutManager(new LinearLayoutManager(this));
        rv_setoran.setItemAnimator(new DefaultItemAnimator());
        adapter = new SetoranSalesAdapter(listSetoran);
        rv_setoran.setAdapter(adapter);

        //muat data setoran
        loadSetoran();
    }

    private void loadSetoran(){
        //Membaca data setoran dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "?date_start=%s&date_end=%s&search=%s&cara=%s", date_start, date_end, Converter.encodeURL(search), pembayaran);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SETORAN_SALES + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listSetoran.clear();
                        adapter.notifyDataSetChanged();

                        total = 0;
                        String str_total = "Total : " + Converter.doubleToRupiah(total);
                        txt_total.setText(str_total);

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        listSetoran.clear();

                        try{
                            total = 0;
                            JSONArray list_bayar = new JSONObject(result).getJSONArray("bayar_list");
                            for(int i = 0; i < list_bayar.length(); i++){
                                JSONObject obj = list_bayar.getJSONObject(i);
                                SetoranModel setoran = new SetoranModel(obj.getString("tanggal"), obj.getString("nama_pelanggan"),
                                        obj.getString("nomor_nota"), obj.getDouble("bayar"), obj.getString("cara_bayar"));
                                total += setoran.getJumlah();
                                listSetoran.add(setoran);
                            }

                            String str_total = "Total : " + Converter.doubleToRupiah(total);
                            txt_total.setText(str_total);
                        }
                        catch (JSONException e){
                            Toast.makeText(SetoranSales.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case UPLOAD_BUKTI:{
                if(data != null){
                    //Inisialisasi data upload
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));
                        bitmap = Converter.resizeBitmap(bitmap, 1200);

                        img_bukti.setImageBitmap(bitmap);
                        overlay_bukti.setVisibility(View.VISIBLE);
                        bar_bukti.setVisibility(View.VISIBLE);

                        upload = new UploadModel(bitmap);
                        upload.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
                        uploadFotoBukti(upload);
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

    private void uploadFotoBukti(final UploadModel upload){
        //Upload gambar bukti transfer ke Web Service
        ApiVolleyManager.getInstance().addMultipartRequest(this, Constant.URL_UPLOAD_FOTO_BUKTI,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), Converter.getFileDataFromDrawable(upload.getBitmap()),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        bar_bukti.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            String id = new JSONObject(result).getString("id");

                            upload.setUploaded(true);
                            upload.setId(id);

                            overlay_bukti.setVisibility(View.GONE);
                            bar_bukti.setVisibility(View.GONE);
                        }
                        catch (JSONException e){
                            Toast.makeText(SetoranSales.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        bar_bukti.setVisibility(View.GONE);
                    }
                }));
    }

    private void uploadBukti(){
        //Kirim data bukti setoran ke Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id_gambar_bukti", upload.getId());
        body.add("nominal", Double.parseDouble(txt_nominal.getText().toString()));
        body.add("keterangan", txt_keterangan.getText().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_UPLOAD_BUKTI, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(SetoranSales.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        dialog_upload.dismiss();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_setoran_sales, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadSetoran();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadSetoran();
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_filter:
                //munculkan panel filter
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                panel_filter = true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(panel_filter){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            panel_filter = false;
        }
        else{
            super.onBackPressed();
        }
    }
}
