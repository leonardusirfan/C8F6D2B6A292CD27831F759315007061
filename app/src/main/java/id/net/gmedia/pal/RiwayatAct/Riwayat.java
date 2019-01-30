package id.net.gmedia.pal.RiwayatAct;

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
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class Riwayat extends AppCompatActivity {

    private int loaded = 0;
    private String search = "";
    private String filter = "all";

    private List<NotaModel> listNota = new ArrayList<>();
    private LoadMoreScrollListener loadMoreScrollListener;
    private RiwayatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RiwayatAdapter(this, listNota);
        rv_riwayat.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadNota(false);
            }
        });
        rv_riwayat.addOnScrollListener(loadMoreScrollListener);

        loadNota(true);
    }

    private void loadNota(final boolean init){
        AppLoading.getInstance().showLoading(this);
        if(init){
            loaded = 0;
        }

        String parameter = String.format(Locale.getDefault(), "?tipe=%s&start=%d&limit=%d&search=%s",filter, loaded, 10, search);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RIWAYAT + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNota.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                        loadMoreScrollListener.cantLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNota.clear();
                                loadMoreScrollListener.canLoad();
                            }

                            JSONArray array = new JSONObject(result).getJSONArray("riwayat_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject nota = array.getJSONObject(i);
                                int type = nota.getString("tipe").equals("canvas")?Constant.PENJUALAN_CANVAS:Constant.PENJUALAN_SO;

                                listNota.add(new NotaModel(nota.getString("no_bukti"),
                                        new CustomerModel("", nota.getString("nama_pelanggan")),
                                        type, nota.getString("tanggal"),
                                        nota.getDouble("total")));
                                loaded += 1;
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(Riwayat.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e("Riwayat", e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(Riwayat.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_riwayat, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadNota(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadNota(true);
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
                PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_filter));
                popup.getMenuInflater().inflate(R.menu.submenu_riwayat, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_so){
                            filter = "so";
                            loadNota(true);
                        }
                        else if(item.getItemId() == R.id.action_canvas){
                            filter = "canvas";
                            loadNota(true);
                        }
                        else{
                            filter = "all";
                            loadNota(true);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
