package id.net.gmedia.pal.Activity.Approval;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import id.net.gmedia.pal.Adapter.ApprovalLoginAdapter;
import id.net.gmedia.pal.Model.LoginPenggantiModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalLoginPengganti extends AppCompatActivity {

    private String search = "";

    private LoadMoreScrollListener loadManager;
    private List<LoginPenggantiModel> listPengganti = new ArrayList<>();
    private List<SimpleObjectModel> listApproval = new ArrayList<>();
    private ApprovalLoginAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_login_pengganti);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval Pengganti");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_login = findViewById(R.id.rv_login);
        rv_login.setItemAnimator(new DefaultItemAnimator());
        rv_login.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalLoginAdapter(this, listPengganti);
        rv_login.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadSalesPengganti(false);
            }
        };
        rv_login.addOnScrollListener(loadManager);

        loadApproval();
        loadSalesPengganti(true);
    }

    private void loadSalesPengganti(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
            loadManager.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadManager.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SALES_PENGGANTI_LIST + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listPengganti.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listPengganti.clear();
                            }

                            JSONArray pengganti_list = new JSONObject(result).getJSONArray("pengganti_list");
                            for(int i = 0; i < pengganti_list.length(); i++){
                                JSONObject pengganti = pengganti_list.getJSONObject(i);
                                listPengganti.add(new LoginPenggantiModel(pengganti.getString("id"), pengganti.getString("nama_diganti"),
                                        pengganti.getString("nama_pengganti"), pengganti.getString("tanggal_mulai"),
                                        pengganti.getString("tanggal_selesai"), pengganti.getString("waktu_request")));
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(pengganti_list.length());
                        }
                        catch (JSONException e){
                            loadManager.finishLoad(0);
                            Toast.makeText(ApprovalLoginPengganti.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalLoginPengganti.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.failedLoad();
                        AppLoading.getInstance().stopLoading();
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

                            //adapter.setListApproval(listApproval);
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalLoginPengganti.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalLoginPengganti.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void approval(final String id, View res_view){
        if(listApproval.size() == 0){
            Toast.makeText(this, "Data Approval belum termuat", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu menu = new PopupMenu(this, res_view, Gravity.END);
        for(int i = 0; i < listApproval.size(); i++){
            menu.getMenu().add(0, i, i, listApproval.get(i).getValue());
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                responApproval(id, listApproval.get(menuItem.getItemId()).getId());
                return false;
            }
        });

        menu.show();
    }

    private void responApproval(String id, String approval){
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);
        body.add("kode_approval", approval);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SALES_PENGGANTI_APPROVAL, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalLoginPengganti.this, result, Toast.LENGTH_SHORT).show();
                        loadSalesPengganti(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalLoginPengganti.this, message, Toast.LENGTH_SHORT).show();
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
                loadSalesPengganti(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadSalesPengganti(true);
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
