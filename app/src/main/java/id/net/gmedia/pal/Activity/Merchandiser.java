package id.net.gmedia.pal.Activity;

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
import android.view.View;
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

import id.net.gmedia.pal.Adapter.MerchandiserAdapter;
import id.net.gmedia.pal.Model.MerchandiserModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class Merchandiser extends AppCompatActivity {

    //Variabel filter & loadmore
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel data
    List<MerchandiserModel> listMerchandiser = new ArrayList<>();
    private MerchandiserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandiser);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Promosi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi RecyclerVIew & Adapter
        RecyclerView rv_merchandiser = findViewById(R.id.rv_merchandiser);
        adapter = new MerchandiserAdapter(this, listMerchandiser);
        rv_merchandiser.setLayoutManager(new LinearLayoutManager(this));
        rv_merchandiser.setItemAnimator(new DefaultItemAnimator());
        rv_merchandiser.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadMerchandiser(false);
            }
        };
        rv_merchandiser.addOnScrollListener(loadMoreScrollListener);

        //Button tambah Merchandiser
        findViewById(R.id.fab_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Merchandiser.this, Customer.class);
                i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_MERCHANDISER);
                startActivity(i);
            }
        });

        //muat data Merchandiser
        loadMerchandiser(true);
    }

    private void loadMerchandiser(final boolean init){
        //Membaca data merchandiser dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISER_LIST + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listMerchandiser.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listMerchandiser.clear();
                            }

                            JSONArray merchandiser_list = new JSONObject(result).getJSONArray("merchandiser_list");
                            for(int i = 0; i < merchandiser_list.length(); i++){
                                JSONObject merchandiser = merchandiser_list.getJSONObject(i);
                                listMerchandiser.add(new MerchandiserModel("", merchandiser.getString("nama"),
                                        merchandiser.getString("alamat"), merchandiser.getString("no_telp"),
                                        merchandiser.getString("gambar"), merchandiser.getString("keterangan")));
                            }
                            loadMoreScrollListener.finishLoad(merchandiser_list.length());

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(Merchandiser.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(Merchandiser.this, message, Toast.LENGTH_SHORT).show();
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
                loadMerchandiser(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadMerchandiser(true);
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
