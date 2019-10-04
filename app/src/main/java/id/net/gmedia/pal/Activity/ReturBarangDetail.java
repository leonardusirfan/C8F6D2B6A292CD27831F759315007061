package id.net.gmedia.pal.Activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.Model.ReturModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Adapter.ReturDetailAdapter;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ReturBarangDetail extends AppCompatActivity {

    //Variabel global nota
    private NotaPenjualanModel nota;

    //Variabel global list barang yang diretur
    private List<ReturModel> barangRetur = new ArrayList<>();

    //Variabel filter & loadmore
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel UI
    private TextView txt_nama, txt_piutang, txt_nota;

    //Variabel data barang
    private List<BarangModel> listBarang = new ArrayList<>();
    private ReturDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_barang_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Retur Barang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi data global tipe penjualan, nota
        int type = getIntent().getIntExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
        if(getIntent().hasExtra(Constant.EXTRA_NOTA)){
            Gson gson = new Gson();
            nota = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_NOTA), NotaPenjualanModel.class);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_nota = findViewById(R.id.txt_nota);

        //button retur barang
        findViewById(R.id.btn_retur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validasi input
                if(barangRetur.size() < 1){
                    Toast.makeText(ReturBarangDetail.this, "Pilih minimal 1 barang retur", Toast.LENGTH_SHORT).show();
                }
                else{
                    //lakukan retur barang
                    returBarang();
                }
            }
        });

        //Inisialisasi RecyclerView dan Adapter
        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ReturDetailAdapter(this, listBarang, type);
        rv_riwayat.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBarang(false);
            }
        };
        rv_riwayat.addOnScrollListener(loadMoreScrollListener);

        //muat data barang
        loadBarang(true);
    }

    private void loadBarang(final boolean init){
        //Membaca data barang dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "/%s?start=%d&limit=%d&search=%s",
                nota.getId(), loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RIWAYAT + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            //Inisialisasi Header
                            JSONObject obj = new JSONObject(result).getJSONObject("detail");
                            txt_nama.setText(obj.getString("nama_pelanggan"));
                            txt_piutang.setText(Converter.doubleToRupiah(obj.getDouble("total")));
                            txt_nota.setText(nota.getId());

                            if(init){
                                listBarang.clear();
                            }

                            JSONArray array = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject item = array.getJSONObject(i);
                                BarangModel barang = new BarangModel(item.getString("id"), item.getString("kode_barang"), item.getString("nama_barang"),
                                        item.getDouble("harga_satuan"), item.getInt("jumlah"), item.getString("satuan"),
                                        item.getDouble("diskon_rupiah"), item.getDouble("harga_total"));
                                barang.setNo_batch(item.getString("no_batch"));
                                listBarang.add(barang);
                            }

                            loadMoreScrollListener.finishLoad(array.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(ReturBarangDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ReturBarangDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    private void returBarang(){
        //kirim retur barang ke Web service
        AppLoading.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        body.add("nota_jual", nota.getId());
        List<JSONObject> listRetur = new ArrayList<>();
        for(ReturModel r : barangRetur){
            JSONBuilder retur = new JSONBuilder();
            retur.add("no_batch", r.getNo_batch());
            retur.add("kode_barang", r.getKode_barang());
            retur.add("jumlah", r.getJumlah());
            retur.add("satuan", r.getSatuan());
            retur.add("kondisi", r.isBarang_baik()?"B":"R");
            retur.add("alasan", r.getAlasan());
            retur.add("id_gambar_barang", r.getGambar());
            listRetur.add(retur.create());
        }
        body.add("barang", new JSONArray(listRetur));
        Log.d(Constant.TAG, "body " + body.create());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_UPLOAD_RETUR, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ReturBarangDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ReturBarangDetail.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        onBackPressed();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ReturBarangDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void tambahBarangRetur(ReturModel retur){
        //Menambah barang dari list retur
        barangRetur.add(retur);
    }

    public void hapusBarangRetur(String id){
        //Menghapus barang dari list retur
        for(ReturModel b : barangRetur){
            if(b.getKode_barang().equals(id)){
                barangRetur.remove(b);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == adapter.UPLOAD_FOTO_RETUR){
            if (data != null) {
                adapter.initUpload(data);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
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
}
