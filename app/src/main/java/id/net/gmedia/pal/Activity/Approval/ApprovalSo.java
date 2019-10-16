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

import id.net.gmedia.pal.Adapter.ApprovalSOAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalSo extends AppCompatActivity {

    //Variabel data nota SO
    private ApprovalSOAdapter adapter;
    private List<NotaPenjualanModel> listNota = new ArrayList<>();
    private List<SimpleObjectModel> listApproval = new ArrayList<>();

    //Variabel load more
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_so);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval SO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_so = findViewById(R.id.rv_so);
        rv_so.setItemAnimator(new DefaultItemAnimator());
        rv_so.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ApprovalSOAdapter(this, listNota);
        rv_so.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadNota(false);
            }
        };
        rv_so.addOnScrollListener(loadMoreScrollListener);

        //Muat data Nota SO
        loadNota(true);
        loadApproval();
    }

    private void loadNota(final boolean init){
        //Membaca data nota SO dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SO_PENDING + parameter,
                ApiVolleyManager.METHOD_GET , Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNota.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                        Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNota.clear();
                            }

                            JSONArray so_list = new JSONObject(result).getJSONArray("nota_list");
                            for(int i = 0; i < so_list.length(); i++){
                                JSONObject nota = so_list.getJSONObject(i);
                                listNota.add(new NotaPenjualanModel(nota.getString("nomor_nota"),
                                        new CustomerModel("", nota.getString("nama_pelanggan"), "",
                                                nota.getString("kodearea")), Constant.PENJUALAN_SO,
                                        nota.getString("tanggal"), nota.getDouble("total")));
                            }

                            loadMoreScrollListener.finishLoad(so_list.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(ApprovalSo.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void loadApproval(){
        //Memuat data respon approval dari Web Service
        ApiVolleyManager.getInstance().addRequest(this,
                Constant.URL_MASTER_APPROVAL + "/sales_order", ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listApproval.clear();
                        adapter.setListApproval(listApproval);
                    }

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
                            Toast.makeText(ApprovalSo.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void responApproval(String nota, String approval){
        //Tampilkan menu approval SO
        JSONBuilder body = new JSONBuilder();
        body.add("nota_so", nota);
        body.add("approval", approval);

        AppLoading.getInstance().showLoading(this, new AppLoading.CancelListener() {
            @Override
            public void onCancel() {
                ApiVolleyManager.getInstance().cancelRequest();
            }
        });

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_APPROVAL_SO, ApiVolleyManager.METHOD_POST,
        Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalSo.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        //Muat ulang data PO
                        loadNota(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
