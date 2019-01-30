package id.net.gmedia.pal.ApprovalSoAct;

import android.app.Dialog;
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
import android.widget.Button;
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
import id.net.gmedia.pal.RiwayatAct.RiwayatAdapter;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.DialogFactory;
import id.net.gmedia.pal.Util.JSONBuilder;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class ApprovalSo extends AppCompatActivity {

    private int loaded = 0;
    private String search = "";

    private RiwayatAdapter adapter;
    private LoadMoreScrollListener loadMoreScrollListener;

    private List<NotaModel> listNota = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_so);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Persetujuan SO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_so = findViewById(R.id.rv_so);
        rv_so.setItemAnimator(new DefaultItemAnimator());
        rv_so.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RiwayatAdapter(this, listNota);
        rv_so.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadNota(false);
            }
        });
        rv_so.addOnScrollListener(loadMoreScrollListener);

        loadNota(true);
    }

    private void loadNota(final boolean init){
        AppLoading.getInstance().showLoading(this);
        if(init){
            loaded = 0;
        }

        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s", loaded, 10, search);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SO_PENDING + parameter, ApiVolleyManager.METHOD_GET ,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNota.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.cantLoad();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNota.clear();
                                loadMoreScrollListener.canLoad();
                            }

                            JSONArray so_list = new JSONObject(result).getJSONArray("so_list");
                            for(int i = 0; i < so_list.length(); i++){
                                JSONObject nota = so_list.getJSONObject(i);
                                listNota.add(new NotaModel(nota.getString("nomor_nota"),
                                        new CustomerModel(nota.getString("kode_pelanggan"), nota.getString("nama_pelanggan")),
                                        Constant.PENJUALAN_SO, nota.getString("tanggal"), nota.getDouble("total")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalSo.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void showApproval(final String id){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_approval_so, 80, 30);

        Button btn_ya, btn_batal;
        btn_ya = dialog.findViewById(R.id.btn_ya);
        btn_batal = dialog.findViewById(R.id.btn_batal);

        btn_ya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLoading.getInstance().showLoading(ApprovalSo.this);
                JSONBuilder body = new JSONBuilder();
                body.add("nomor_nota", id);

                ApiVolleyManager.getInstance().addRequest(ApprovalSo.this, Constant.URL_APPROVAL_SO,
                        ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(ApprovalSo.this)),
                        body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                            @Override
                            public void onSuccess(String result) {
                                AppLoading.getInstance().stopLoading();
                                dialog.dismiss();

                                loadNota(true);
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(ApprovalSo.this, message, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                                AppLoading.getInstance().stopLoading();
                            }
                        }));
            }
        });

        btn_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
