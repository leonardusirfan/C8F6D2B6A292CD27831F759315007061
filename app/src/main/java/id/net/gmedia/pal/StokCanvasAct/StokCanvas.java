package id.net.gmedia.pal.StokCanvasAct;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class StokCanvas extends AppCompatActivity {

    private String search = "";
    private int loaded = 0;
    private LoadMoreScrollListener loadMoreScrollListener;

    private List<BarangModel> listBarang = new ArrayList<>();
    private StokCanvasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_canvas);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Stok Canvas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new StokCanvasAdapter(this, listBarang);

        rv_barang.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadCanvas(false);
            }
        });
        rv_barang.addOnScrollListener(loadMoreScrollListener);
        loadCanvas(true);
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
                            Toast.makeText(StokCanvas.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Penjualan Barang", e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(StokCanvas.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
