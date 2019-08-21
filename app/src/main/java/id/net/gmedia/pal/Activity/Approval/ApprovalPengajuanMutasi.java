package id.net.gmedia.pal.Activity.Approval;

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
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPengajuanMutasi extends AppCompatActivity {

    private String search = "";

    private List<NotaModel> listPengajuan = new ArrayList<>();
    private AdapterApprovalPengajuanMutasi adapter;
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pengajuan_mutasi);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval Mutasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_pengajuan = findViewById(R.id.rv_pengajuan);
        rv_pengajuan.setItemAnimator(new DefaultItemAnimator());
        rv_pengajuan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterApprovalPengajuanMutasi(this, listPengajuan);
        rv_pengajuan.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadApproval(false);
            }
        };
        rv_pengajuan.addOnScrollListener(loadManager);
    }

    @Override
    protected void onResume() {
        loadApproval(true);
        super.onResume();
    }

    private void loadApproval(final boolean init){
        AppLoading.getInstance().showLoading(this);

        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("limit", 20);
        body.add("search", search);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENGAJUAN_MUTASI_APPROVAL_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listPengajuan.clear();
                            adapter.notifyDataSetChanged();
                        }

                        Toast.makeText(ApprovalPengajuanMutasi.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.failedLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listPengajuan.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject pengajuan = response.getJSONObject(i);
                                listPengajuan.add(new NotaModel(pengajuan.getString("nomor_bukti"),
                                        pengajuan.getString("nama_sales"), pengajuan.getString("tanggal"),
                                        0, pengajuan.getString("keterangan")));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(response.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPengajuanMutasi.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.failedLoad();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPengajuanMutasi.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.failedLoad();
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
                loadApproval(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadApproval(true);
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
