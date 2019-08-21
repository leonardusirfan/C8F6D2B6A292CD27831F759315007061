package id.net.gmedia.pal.Activity.Approval;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.AdapterApprovalMutasiBarang;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPengajuanMutasiDetail extends AppCompatActivity {

    private String no_nota = "";
    private String search = "";

    private AppCompatSpinner spn_approval;
    private ArrayAdapter<SimpleObjectModel> adapterApproval;
    private List<SimpleObjectModel> listApproval = new ArrayList<>();

    private List<BarangModel> listBarang = new ArrayList<>();
    private LoadMoreScrollListener loadManager;
    private AdapterApprovalMutasiBarang adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pengajuan_mutasi_detail);

        if(getIntent().hasExtra(Constant.EXTRA_NO_NOTA)){
            no_nota = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Pengajuan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spn_approval = findViewById(R.id.spn_approval);
        adapterApproval = new ArrayAdapter<SimpleObjectModel>(this, R.layout.custom_spinner_item, listApproval){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_custom_spinner,parent, false);
                }

                String rowItem = getItem(position).toString();
                TextView txtTitle = convertView.findViewById(R.id.txt_text);
                txtTitle.setText(rowItem);

                return convertView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_custom_spinner_dropdown,parent, false);
                }

                String rowItem = getItem(position).toString();
                TextView txtTitle = convertView.findViewById(R.id.txt_text);
                txtTitle.setText(rowItem);

                return convertView;
            }
        };
        adapterApproval.setDropDownViewResource(R.layout.custom_spinner_item);
        spn_approval.setAdapter(adapterApproval);

        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterApprovalMutasiBarang(this, listBarang);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBarang(false);
            }
        };
        rv_barang.setAdapter(adapter);
        rv_barang.addOnScrollListener(loadManager);

        findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spn_approval.getSelectedItemPosition() == -1){
                    Toast.makeText(ApprovalPengajuanMutasiDetail.this, "Approval belum termuat", Toast.LENGTH_SHORT).show();
                }
                else{
                    kirimApproval();
                }
            }
        });

        loadApproval();
        loadBarang(true);
    }

    private void loadBarang(final boolean init){
        AppLoading.getInstance().showLoading(this);

        if(init){
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("nomor_bukti", no_nota);
        body.add("start", loadManager.getLoaded());
        body.add("limit", 20);
        body.add("search", search);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENGAJUAN_MUTASI_APPROVAL_LIST_BARANG,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.failedLoad();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listBarang.clear();
                            }

                            JSONArray details = new JSONObject(result).getJSONArray("details");
                            for(int i = 0; i < details.length(); i++){
                                JSONObject detail = details.getJSONObject(i);
                                BarangModel b = new BarangModel(detail.getString("id"),
                                        detail.getString("kode_barang"), detail.getString("nama_barang"));
                                b.setJumlah(detail.getInt("jumlah"));
                                b.setSatuan(detail.getString("satuan"));
                                listBarang.add(b);
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(details.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPengajuanMutasiDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadManager.failedLoad();
                            AppLoading.getInstance().stopLoading();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();

                        loadManager.failedLoad();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void loadApproval(){
        //Memuat data respon approval dari Web Service
        ApiVolleyManager.getInstance().addRequest(this,
                Constant.URL_MASTER_APPROVAL + "/request_canvas", ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listApproval.clear();
                        adapterApproval.notifyDataSetChanged();
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

                            adapterApproval.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPengajuanMutasiDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void hapusItem(String id){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENGAJUAN_MUTASI_APPROVAL_HAPUS_BARANG,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        loadBarang(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void kirimApproval(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("nomor_bukti", no_nota);
        body.add("kode_approval", listApproval.get(spn_approval.getSelectedItemPosition()).getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PENGAJUAN_MUTASI_APPROVAL_UPDATE,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, "Approval berhasil di-update", Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        onBackPressed();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPengajuanMutasiDetail.this, message, Toast.LENGTH_SHORT).show();
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
                loadBarang(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadBarang(true);
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
