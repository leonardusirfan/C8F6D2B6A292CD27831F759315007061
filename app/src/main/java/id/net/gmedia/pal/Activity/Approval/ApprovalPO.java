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

import id.net.gmedia.pal.Adapter.ApprovalPOAdapter;
import id.net.gmedia.pal.Model.PurchaseOrderModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPO extends AppCompatActivity {

    //Variabel data
    private List<PurchaseOrderModel> listPO = new ArrayList<>();
    private ApprovalPOAdapter adapter;

    //Variabel filter & loadmore
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel approval
    private List<SimpleObjectModel> listApproval = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_po);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval PO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_po = findViewById(R.id.rv_po);
        rv_po.setItemAnimator(new DefaultItemAnimator());
        rv_po.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalPOAdapter(this, listPO);
        rv_po.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadPO(false);
            }
        };
        rv_po.addOnScrollListener(loadMoreScrollListener);

        //muat data PO
        loadPO(true);
        //muat menu approval
        loadApproval();
    }

    private void loadPO(final boolean init){
        //Membaca data PO dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PO_LIST + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listPO.clear();
                            adapter.notifyDataSetChanged();
                        }

                        Toast.makeText(ApprovalPO.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listPO.clear();
                            }

                            JSONArray purchase_orders = new JSONObject(result).getJSONArray("purchase_orders");
                            for(int i = 0; i < purchase_orders.length(); i++){
                                JSONObject po = purchase_orders.getJSONObject(i);
                                listPO.add(new PurchaseOrderModel(po.getString("nomor_nota"), po.getString("tanggal"),
                                        po.getDouble("total"), po.getDouble("ppn"), po.getDouble("biaya_lain"),
                                        po.getString("kode_supplier"), po.getString("tanggal_tempo"), po.getString("keterangan")));
                            }

                            loadMoreScrollListener.finishLoad(purchase_orders.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(ApprovalPO.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPO.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    public void loadApproval(){
        //Memuat data respon approval dari Web Service
        ApiVolleyManager.getInstance().addRequest(this,
                Constant.URL_MASTER_APPROVAL + "/purchase_order", ApiVolleyManager.METHOD_GET,
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
                            Toast.makeText(ApprovalPO.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPO.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void responApproval(String no_nota, String id_approval){
        //Kirim respon approval
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("nomor_nota", no_nota);
        body.add("kode_approval", id_approval);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PO_APPROVE, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalPO.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        //Muat ulang data PO
                        loadPO(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPO.this, message, Toast.LENGTH_SHORT).show();
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
                loadPO(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadPO(true);
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
