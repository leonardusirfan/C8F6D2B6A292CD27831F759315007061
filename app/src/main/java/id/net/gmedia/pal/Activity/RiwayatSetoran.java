package id.net.gmedia.pal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.AdapterRiwayatSetoran;
import id.net.gmedia.pal.Model.SetoranModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class RiwayatSetoran extends AppCompatActivity {

    private String start_date = "";
    private String end_date = "";
    private String search = "";

    private List<SetoranModel> listRiwayat = new ArrayList<>();
    private AdapterRiwayatSetoran adapter;
    private LoadMoreScrollListener loadMoreScrollListener;

    private TextView txt_tgl_mulai, txt_tgl_selesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_setoran);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Setoran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_tgl_mulai = findViewById(R.id.txt_tgl_mulai);
        txt_tgl_selesai = findViewById(R.id.txt_tgl_selesai);
        txt_tgl_mulai.setText(start_date);
        txt_tgl_selesai.setText(end_date);

        RecyclerView rv_setoran = findViewById(R.id.rv_setoran);
        rv_setoran.setItemAnimator(new DefaultItemAnimator());
        rv_setoran.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterRiwayatSetoran(this, listRiwayat);
        rv_setoran.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadRiwayat(false);
            }
        };
        rv_setoran.addOnScrollListener(loadMoreScrollListener);

        findViewById(R.id.img_tgl_mulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(RiwayatSetoran.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        start_date = dateString;
                        txt_tgl_mulai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.img_tgl_selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(RiwayatSetoran.this, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        end_date = dateString;
                        txt_tgl_selesai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRiwayat(true);
            }
        });

        //muat data riwayat
        loadRiwayat(true);
    }

    private void loadRiwayat(final boolean init){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        final int LOAD_SIZE = 20;
        JSONBuilder body = new JSONBuilder();
        body.add("start_date", start_date);
        body.add("end_date", end_date);
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("limit", LOAD_SIZE);
        body.add("search", search);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SETORAN_HISTORY,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listRiwayat.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listRiwayat.clear();
                            }

                            JSONArray array = new JSONArray(result);
                            for(int i = 0; i < array.length(); i++){
                                JSONObject riwayat = array.getJSONObject(i);
                                listRiwayat.add(new SetoranModel(riwayat.getString("tgl"),
                                        riwayat.getString("nama_pelanggan"), riwayat.getString("nobukti"),
                                        riwayat.getDouble("total"), riwayat.getString("cara_bayar"),
                                        riwayat.getString("keterangan")));
                            }

                            adapter.notifyDataSetChanged();

                            AppLoading.getInstance().stopLoading();
                            loadMoreScrollListener.finishLoad(array.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(RiwayatSetoran.this, R.string.error_json, Toast.LENGTH_SHORT).show();

                            AppLoading.getInstance().stopLoading();
                            loadMoreScrollListener.finishLoad(0);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RiwayatSetoran.this, message, Toast.LENGTH_SHORT).show();
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
                loadRiwayat(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadRiwayat(true);
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
