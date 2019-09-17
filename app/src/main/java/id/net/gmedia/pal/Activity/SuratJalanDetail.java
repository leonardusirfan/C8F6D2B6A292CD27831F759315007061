package id.net.gmedia.pal.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.FileDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.SuratJalanDetailAdapter;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class SuratJalanDetail extends AppCompatActivity {

    private String id_nota = "";
    private String no_nota = "";
    private final int DOWNLOAD_REQUEST = 999;

    //Variabel UI
    private TextView txt_nota, txt_total;

    private SuratJalanDetailAdapter adapter;
    private List<BarangModel> listBarang = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_jalan_detail);

        //Inisialisasi variabel global nomor nota
        if(getIntent().hasExtra(Constant.EXTRA_NO_NOTA)){
            no_nota = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);
        }
        if(getIntent().hasExtra(Constant.EXTRA_ID_NOTA)){
            id_nota = getIntent().getStringExtra(Constant.EXTRA_ID_NOTA);
        }

        //Inisialisasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Surat Jalan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_nota = findViewById(R.id.txt_nota);
        txt_total = findViewById(R.id.txt_total);

        RecyclerView rv_detail = findViewById(R.id.rv_detail);
        rv_detail.setItemAnimator(new DefaultItemAnimator());
        rv_detail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuratJalanDetailAdapter(listBarang);
        rv_detail.setAdapter(adapter);

        loadDetail();
    }

    private void loadDetail(){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format(Locale.getDefault(), "/%s", no_nota);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SURAT_JALAN_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(SuratJalanDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listBarang.clear();

                            JSONObject response = new JSONObject(result);
                            txt_nota.setText(response.getJSONObject("header").getString("no_bukti"));
                            txt_total.setText(Converter.doubleToRupiah(response.getJSONObject("header").getDouble("total")));

                            JSONArray details = response.getJSONArray("details");
                            for(int i = 0; i < details.length(); i++){
                                JSONObject barang = details.getJSONObject(i);
                                listBarang.add(new BarangModel("", barang.getString("nama_barang"),
                                        barang.getDouble("harga"), barang.getInt("jumlah"),
                                        barang.getString("satuan"), 0, barang.getDouble("total")));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(SuratJalanDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SuratJalanDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_surat_jalan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_download) {
            if (writeStorageCheckPermission()) {
                downloadFiles();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean writeStorageCheckPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOWNLOAD_REQUEST);
                return false;
            }
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == DOWNLOAD_REQUEST){
            if(writeStorageCheckPermission()){
                downloadFiles();
            }
        }
    }

    public void downloadFiles(){
        String parameter = String.format(Locale.getDefault(), "?id=%s", id_nota);
        FileDownloadManager downloadManager = new FileDownloadManager(this, FileDownloadManager.TYPE_PDF);
        downloadManager.download(Constant.URL_SURAT_JALAN_DOWNLOAD + parameter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
