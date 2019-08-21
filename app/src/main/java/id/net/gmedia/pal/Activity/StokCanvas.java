package id.net.gmedia.pal.Activity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import id.net.gmedia.pal.Model.SalesModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Adapter.StokCanvasAdapter;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class StokCanvas extends AppCompatActivity {

    //Variabel global filter & loadmore
    private String nip = "";
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel data barang
    private List<BarangModel> listBarang = new ArrayList<>();
    private StokCanvasAdapter adapter;

    //Variabel spinner sales
    private AppCompatSpinner spn_sales;
    private List<SalesModel> listSales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_canvas);

        //Inisialisasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Stok Canvas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new StokCanvasAdapter(this, listBarang);
        rv_barang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadCanvas(false);
            }
        };
        rv_barang.addOnScrollListener(loadMoreScrollListener);

        //Inisialiasi UI berdasarkan jabatan user (helper bisa cek stok sales)
        spn_sales = findViewById(R.id.spn_sales);
        if(AppSharedPreferences.getJabatan(this).equals("Helper")){
            findViewById(R.id.layout_sales).setVisibility(View.VISIBLE);
            loadSales();
        }
        else{
            loadCanvas(true);
        }
    }

    private void loadSales(){
        //Membaca data Sales dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_LIST_SALES_BY_HELPER,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            List<String> spinnerItem = new ArrayList<>();
                            JSONArray sales_list = new JSONObject(result).getJSONArray("sales_list");

                            for(int i = 0; i < sales_list.length(); i++){
                                JSONObject sales = sales_list.getJSONObject(i);
                                listSales.add(new SalesModel(sales.getString("nip"), sales.getString("nama"),
                                        sales.getString("jabatan"), sales.getString("posisi")));
                                spinnerItem.add(sales.getString("nama"));
                            }

                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(
                                    StokCanvas.this, android.R.layout.simple_spinner_item, spinnerItem);
                            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_sales.setAdapter(spinner_adapter);
                            spn_sales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    nip = listSales.get(spn_sales.getSelectedItemPosition()).getNip();
                                    loadCanvas(true);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    nip = "";
                                }
                            });
                        }
                        catch (JSONException e){
                            Toast.makeText(StokCanvas.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(StokCanvas.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadCanvas(final boolean init){
        //Membaca data barang dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);

        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "/%s?search=%s&start=%d&limit=%d", nip,
                Converter.encodeURL(search), loadMoreScrollListener.getLoaded(), 10);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_CANVAS + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                        }

                        adapter.notifyDataSetChanged();
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
                                        barang.getJSONObject(i).getInt("stok"));
                                b.setListSatuan(satuan);
                                listBarang.add(b);
                            }

                            loadMoreScrollListener.finishLoad(barang.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(StokCanvas.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Penjualan Barang", e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(StokCanvas.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadCanvas(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadCanvas(true);
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
