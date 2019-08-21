package id.net.gmedia.pal.Activity.ReturCanvas;

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

import id.net.gmedia.pal.Adapter.AdapterMutasiBarang;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ReturCanvasBarang extends AppCompatActivity {

    private String search = "";
    private LoadMoreScrollListener loadManager;
    private AdapterMutasiBarang adapter;
    private List<BarangModel> listBarang = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_canvas_barang);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Tambah Barang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this));
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBarang(false);
            }
        };
        adapter = new AdapterMutasiBarang(this, listBarang, true);
        rv_barang.setAdapter(adapter);
        rv_barang.addOnScrollListener(loadManager);

        loadBarang(true);
    }

    private void loadBarang(boolean init){
        AppLoading.getInstance().showLoading(this);
        if(init){
            loadManager.initLoad();
            listBarang.clear();
        }

        String parameter = String.format(Locale.getDefault(), "?search=%s&start=%d&limit=%d",
                Converter.encodeURL(search), loadManager.getLoaded(), 10);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENJUALAN_BARANG_CANVAS + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ReturCanvasBarang.this, message, Toast.LENGTH_SHORT).show();

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray list = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < list.length(); i++){
                                JSONObject barang = list.getJSONObject(i);
                                BarangModel b = new BarangModel(barang.getString("kode_barang"),
                                        barang.getString("nama_barang"), barang.getDouble("harga"));
                                b.setNo_batch(barang.getString("no_batch"));

                                JSONArray arr_satuan = barang.getJSONArray("satuan");
                                b.getListSatuan().add(new SatuanModel(arr_satuan.getString(0), barang.getInt("stok")));
                                b.getListSatuan().add(new SatuanModel(arr_satuan.getString(1), barang.getInt("stok_besar")));

                                listBarang.add(b);
                            }

                            loadManager.finishLoad(list.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ReturCanvasBarang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ReturCanvasBarang.this, message, Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, message);

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
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
                loadBarang(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadBarang(true);
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
