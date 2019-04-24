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
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.ApprovalDispensasiPiutangAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.DispensasiPiutangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalDispensasiPiutang extends AppCompatActivity {

    private String search = "";

    private ApprovalDispensasiPiutangAdapter adapter;
    private List<DispensasiPiutangModel> listDispensasi = new ArrayList<>();
    private List<SimpleObjectModel> listApproval = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_dispensasi_piutang);

        //Inisialiasasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval Dispensasi Piutang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_dispensasi = findViewById(R.id.rv_dispensasi);
        rv_dispensasi.setItemAnimator(new DefaultItemAnimator());
        rv_dispensasi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalDispensasiPiutangAdapter(this, listDispensasi);
        rv_dispensasi.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadPengajuan(false);
            }
        };
        rv_dispensasi.addOnScrollListener(loadManager);

        loadPengajuan(true);
        loadApproval();
    }

    private void loadPengajuan(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
            loadManager.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadManager.getLoaded(), 10, Converter.encodeURL( search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DISPENSASI_LIST + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listDispensasi.clear();
                            adapter.notifyDataSetChanged();
                        }
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listDispensasi.clear();
                            }

                            JSONArray dispensasi_list = new JSONObject(result).getJSONArray("dispensasi_list");
                            for(int i = 0; i < dispensasi_list.length(); i++){
                                JSONObject dispensasi = dispensasi_list.getJSONObject(i);
                                listDispensasi.add(new DispensasiPiutangModel(dispensasi.getString("id"),new CustomerModel
                                        (dispensasi.getString("kode_pelanggan"), dispensasi.getString("nama_pelanggan")),
                                        dispensasi.getDouble("total_pengajuan"), dispensasi.getString("keterangan_dispensasi")));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(dispensasi_list.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalDispensasiPiutang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalDispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }
                }));
    }

    public void loadApproval(){
        //Memuat data respon approval dari Web Service
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_APPROVAL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            listApproval.clear();

                            JSONArray approval = new JSONObject(result).getJSONArray("approval");
                            for(int i = 0; i < approval.length(); i++){
                                listApproval.add(new SimpleObjectModel(approval.getJSONObject(i).getString("kode"),
                                        approval.getJSONObject(i).getString("nama")));
                            }

                            adapter.setListApproval(listApproval);
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalDispensasiPiutang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalDispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void responApproval(String id_pengajuan, String id_approval){
        //Kirim respon approval
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id_pengajuan);
        body.add("approval", id_approval);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DISPENSASI_APPROVE, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalDispensasiPiutang.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        //Muat ulang data PO
                        loadPengajuan(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalDispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
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
                loadPengajuan(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadPengajuan(true);
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
