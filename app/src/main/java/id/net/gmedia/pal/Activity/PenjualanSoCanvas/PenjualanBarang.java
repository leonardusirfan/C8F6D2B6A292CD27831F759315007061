package id.net.gmedia.pal.Activity.PenjualanSoCanvas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.Adapter.PenjualanBarangAdapter;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class PenjualanBarang extends AppCompatActivity {

    //Variable filter & loadmore
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel data
    private List<BarangModel> listBarang = new ArrayList<>();
    private PenjualanBarangAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_barang);

        //inisialisasi Jenis penjualan dan Customer dari activity sebelumnya
        /*Gson gson = new Gson();
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);*/

        //nomor bukti
       /* if(getIntent().hasExtra(Constant.EXTRA_NO_NOTA)){
            no_bukti = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);
        }*/

        //Inisialisasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pilih Barang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_CANVAS){
            adapter = new PenjualanBarangAdapter(this, listBarang, true);
        }
        else{
            adapter = new PenjualanBarangAdapter(this, listBarang);
        }
        rv_barang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                    loadSO(false);
                }
                else{
                    loadCanvas(false);
                }
            }
        };
        rv_barang.addOnScrollListener(loadMoreScrollListener);

        //muat data berdasarkan jenis penjualan
        if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
            loadSO(true);
        }
        else{
            loadCanvas(true);
        }

    }

    private void loadSO(final boolean init){
        //Membaca data SO dari Web Service
        if(init){
            loadMoreScrollListener.initLoad();
        }
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_SO +
                        String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search)),
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
                            if(init){
                                listBarang.clear();
                            }

                            JSONArray barang = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < barang.length(); i++){
                                List<SatuanModel> satuan = new ArrayList<>();
                                List<SatuanModel> satuanCanvas = new ArrayList<>();

                                //jika satuan barang kosong, lewati barang dan lanjutkan looping
                                JSONArray array_satuan = barang.getJSONObject(i).getJSONArray("satuan");
                                if(array_satuan.length() == 0){
                                    continue;
                                }

                                for(int j = 0; j < array_satuan.length(); j++){
                                    satuan.add(new SatuanModel(array_satuan.getString(j)));
                                    satuanCanvas.add(new SatuanModel(array_satuan.getString(j)));
                                }

                                satuan.get(0).setJumlah(barang.getJSONObject(i).getInt("stok"));
                                satuan.get(1).setJumlah(barang.getJSONObject(i).getInt("stok_besar"));

                                satuanCanvas.get(0).setJumlah(barang.getJSONObject(i).getInt("stok_canvas"));
                                satuanCanvas.get(1).setJumlah(barang.getJSONObject(i).getInt("stok_besar_canvas"));

                                BarangModel b = new BarangModel(barang.getJSONObject(i).getString("kode_barang"),
                                        barang.getJSONObject(i).getString("nama_barang"),
                                        barang.getJSONObject(i).getDouble("harga"),
                                        barang.getJSONObject(i).getString("tipe"),
                                        barang.getJSONObject(i).getString("no_batch"));
                                b.setListSatuan(satuan);
                                b.setListSatuanCanvas(satuanCanvas);

                                listBarang.add(b);
                            }

                            loadMoreScrollListener.finishLoad(barang.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    private void loadCanvas(final boolean init){
        if(init){
            loadMoreScrollListener.initLoad();
        }

        //Membaca data Canvas dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);

        String parameter = String.format(Locale.getDefault(), "?search=%s&start=%d&limit=%d",
                Converter.encodeURL(search), loadMoreScrollListener.getLoaded(), 10);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_CANVAS + parameter,
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
                    if(init){
                        listBarang.clear();
                    }

                    JSONArray barang = new JSONObject(result).getJSONArray("barang_list");
                    for(int i = 0; i < barang.length(); i++){
                        List<SatuanModel> satuan = new ArrayList<>();
                        JSONArray array_satuan = barang.getJSONObject(i).getJSONArray("satuan");
                        for(int j = 0; j < array_satuan.length(); j++){
                            satuan.add(new SatuanModel(array_satuan.getString(j)));
                        }

                        satuan.get(0).setJumlah(barang.getJSONObject(i).getInt("stok"));
                        satuan.get(1).setJumlah(barang.getJSONObject(i).getInt("stok_besar"));

                        BarangModel b = new BarangModel(barang.getJSONObject(i).getString("kode_barang"),
                                barang.getJSONObject(i).getString("nama_barang"),
                                barang.getJSONObject(i).getDouble("harga"),
                                barang.getJSONObject(i).getString("tipe"),
                                barang.getJSONObject(i).getString("no_batch"));
                        b.setListSatuan(satuan);
                        listBarang.add(b);
                    }

                    loadMoreScrollListener.finishLoad(barang.length());
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    loadMoreScrollListener.finishLoad(0);
                    Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                    e.printStackTrace();
                }

                AppLoading.getInstance().stopLoading();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad(0);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_penjualan_barang, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                    loadSO(true);
                }
                else{
                    loadCanvas(true);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                        loadSO(true);
                    }
                    else{
                        loadCanvas(true);
                    }
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
            case R.id.action_barcode:
                //Buka Activity scan barcode
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setOrientationLocked(true);
                //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Baca barcode");
                //integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                //integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cariBarcode(String kode){
        System.out.println(kode);
        //Membaca data barang berdasarkan barcode

        String parameter = String.format(Locale.getDefault(), "?barcode=%s", kode);
        String url = AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN()
                == Constant.PENJUALAN_SO?Constant.URL_SO_CARI_BARCODE:Constant.URL_CANVAS_CARI_BARCODE;

        ApiVolleyManager.getInstance().addRequest(this, url + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject obj = new JSONObject(result).getJSONObject("barang");

                            List<SatuanModel> satuan = new ArrayList<>();
                            List<SatuanModel> satuanCanvas = new ArrayList<>();

                            JSONArray array_satuan = obj.getJSONArray("satuan");

                            for(int i = 0; i < array_satuan.length(); i++){
                                satuan.add(new SatuanModel(array_satuan.getString(i)));
                                if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                                    satuanCanvas.add(new SatuanModel(array_satuan.getString(i)));
                                }
                            }

                            if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_CANVAS){
                                satuan.get(0).setJumlah(obj.getInt("stok"));
                                satuan.get(1).setJumlah(obj.getInt("stok_besar"));
                            }
                            else if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                                satuan.get(0).setJumlah(obj.getInt("stok"));
                                satuan.get(1).setJumlah(obj.getInt("stok_besar"));
                                satuanCanvas.get(0).setJumlah(obj.getInt("stok_canvas"));
                                satuanCanvas.get(1).setJumlah(obj.getInt("stok_besar_canvas"));
                            }

                            BarangModel barang = new BarangModel(obj.getString("kode_barang"),
                                    obj.getString("nama_barang"), obj.getDouble("harga"));
                            barang.setListSatuan(satuan);
                            if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                                barang.setListSatuanCanvas(satuanCanvas);
                            }

                            if(AppKeranjangPenjualan.getInstance().isBarangBelumAda(barang.getId())){
                                Gson gson = new Gson();
                                Intent i = new Intent(PenjualanBarang.this, PenjualanDetail.class);
                                i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                                /*if(!no_bukti.equals("")){
                                    i.putExtra(Constant.EXTRA_NO_NOTA, no_bukti);
                                }*/
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PenjualanBarang.this,
                                        "Barang sudah ada di penjualan anda", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Ambil data pembacaan barcode
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                cariBarcode(result.getContents());
                //System.out.println("Barcode : " + result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
