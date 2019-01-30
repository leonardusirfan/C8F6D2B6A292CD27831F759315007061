package id.net.gmedia.pal.PenjualanAct;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class PenjualanBarang extends AppCompatActivity {

    public String tempo = "";
    public int JENIS_PENJUALAN;
    public String no_bukti = "";
    public CustomerModel customer;
    private String search = "";

    private int loaded = 0;
    private LoadMoreScrollListener loadMoreScrollListener;

    private List<BarangModel> listBarang = new ArrayList<>();
    private PenjualanBarangAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_barang);

        Gson gson = new Gson();
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        if(getIntent().hasExtra(Constant.EXTRA_NO_BUKTI)){
            no_bukti = getIntent().getStringExtra(Constant.EXTRA_NO_BUKTI);
        }

        if(getIntent().hasExtra(Constant.EXTRA_TANGGAL_TEMPO)){
            tempo = getIntent().getStringExtra(Constant.EXTRA_TANGGAL_TEMPO);
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pilih Barang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if(JENIS_PENJUALAN == Constant.PENJUALAN_CANVAS){
            adapter = new PenjualanBarangAdapter(this, listBarang, true);
        }
        else{
            adapter = new PenjualanBarangAdapter(this, listBarang);
        }

        rv_barang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                if(JENIS_PENJUALAN == Constant.PENJUALAN_SO){
                    loadSO(false);
                }
                else{
                    loadCanvas(false);
                }
            }
        });
        rv_barang.addOnScrollListener(loadMoreScrollListener);

        if(JENIS_PENJUALAN == Constant.PENJUALAN_SO){
            loadSO(true);
        }
        else{
            loadCanvas(true);
        }

    }

    private void loadSO(final boolean init){
        AppLoading.getInstance().showLoading(this);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_SO +
                        String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s", loaded, 10, search),
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            loaded = 0;
                        }

                        loadMoreScrollListener.cantLoad();
                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listBarang.clear();
                                loaded = 0;
                                loadMoreScrollListener.canLoad();
                            }

                            JSONArray barang = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < barang.length(); i++){
                                List<SatuanModel> satuan = new ArrayList<>();
                                JSONArray array_satuan = barang.getJSONObject(i).getJSONArray("satuan");
                                for(int j = 0; j < array_satuan.length(); j++){
                                    satuan.add(new SatuanModel(array_satuan.getString(j)));
                                }

                                /*satuan.get(0).setJumlah(barang.getJSONObject(i).getInt("stok"));
                                satuan.get(1).setJumlah(barang.getJSONObject(i).getInt("stok_besar"));*/

                                BarangModel b = new BarangModel(barang.getJSONObject(i).getString("kode_barang"),
                                        barang.getJSONObject(i).getString("nama_barang"),
                                        barang.getJSONObject(i).getDouble("harga"));
                                b.setListSatuan(satuan);

                                listBarang.add(b);
                                loaded += 1;
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Penjualan Barang", e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }
                }));
    }

    private void loadCanvas(final boolean init){
        AppLoading.getInstance().showLoading(this);

        String parameter = String.format(Locale.getDefault(), "?search=%s&start=%d&limit=%d", search, loaded, 10);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_CANVAS + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String message) {
                if(init){
                    listBarang.clear();
                    loaded = 0;
                }

                loadMoreScrollListener.cantLoad();
                adapter.notifyDataSetChanged();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
            }

            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listBarang.clear();
                        loaded = 0;
                        loadMoreScrollListener.canLoad();
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
                                barang.getJSONObject(i).getInt("stok"));
                        b.setListSatuan(satuan);
                        listBarang.add(b);

                        loaded += 1;
                    }

                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e("Penjualan Barang", e.getMessage());
                    e.printStackTrace();
                }

                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
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
                if(JENIS_PENJUALAN == Constant.PENJUALAN_SO){
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
                    if(JENIS_PENJUALAN == Constant.PENJUALAN_SO){
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
        String parameter = String.format(Locale.getDefault(), "?barcode=%s", kode);
        String url = JENIS_PENJUALAN == Constant.PENJUALAN_SO?Constant.URL_SO_CARI_BARCODE:Constant.URL_CANVAS_CARI_BARCODE;
        ApiVolleyManager.getInstance().addRequest(this, url + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PenjualanBarang.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject obj = new JSONObject(result).getJSONObject("barang");

                            List<SatuanModel> satuan = new ArrayList<>();
                            JSONArray array_satuan = obj.getJSONArray("satuan");

                            for(int i = 0; i < array_satuan.length(); i++){
                                satuan.add(new SatuanModel(array_satuan.getString(i)));
                            }

                            if(JENIS_PENJUALAN == Constant.PENJUALAN_CANVAS){
                                satuan.get(0).setJumlah(obj.getInt("stok"));
                                satuan.get(1).setJumlah(obj.getInt("stok_besar"));
                            }

                            BarangModel barang = new BarangModel(obj.getString("kode_barang"),
                                    obj.getString("nama_barang"), obj.getDouble("harga"));
                            barang.setListSatuan(satuan);

                            if(!AppKeranjangPenjualan.getInstance().isBarangAda(barang.getId())){
                                Gson gson = new Gson();
                                Intent i = new Intent(PenjualanBarang.this, PenjualanDetail.class);
                                i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                                i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, JENIS_PENJUALAN);
                                if(!no_bukti.equals("")){
                                    i.putExtra(Constant.EXTRA_NO_BUKTI, no_bukti);
                                }
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(PenjualanBarang.this, "Barang sudah ada di penjualan anda", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PenjualanBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                cariBarcode(result.getContents());
                //Toast.makeText(PenjualanBarang.this, "Barcode : " + result.getContents(), Toast.LENGTH_SHORT).show();
                System.out.println("Barcode : " + result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
