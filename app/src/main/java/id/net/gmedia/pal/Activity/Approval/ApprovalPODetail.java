package id.net.gmedia.pal.Activity.Approval;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.ApprovalPODetailAdapter;
import id.net.gmedia.pal.Model.BarangPOModel;
import id.net.gmedia.pal.Model.PurchaseOrderModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPODetail extends AppCompatActivity {

    //Variabel global nomor nota
    public PurchaseOrderModel nota;

    //Variabel UI
    private TextView txt_nota, txt_total;

    //Variabel data PO
    private List<BarangPOModel> listPo = new ArrayList<>();
    private ApprovalPODetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_podetail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail PO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi data global nota
        if(getIntent().hasExtra(Constant.EXTRA_NOTA)){
            Gson gson = new Gson();
            nota = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_NOTA), PurchaseOrderModel.class);
        }

        //Inisialisasi UI
        txt_nota = findViewById(R.id.txt_nota);
        txt_total = findViewById(R.id.txt_total);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_po = findViewById(R.id.rv_po);
        rv_po.setItemAnimator(new DefaultItemAnimator());
        rv_po.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalPODetailAdapter(this, listPo);
        rv_po.setAdapter(adapter);

        //muat data detail
        loadDetail();
    }

    private void loadDetail(){
        //Membaca detail PO dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "/%s", nota.getId());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PO_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listPo.clear();
                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            txt_nota.setText(nota.getId());
                            txt_total.setText(Converter.doubleToRupiah(nota.getTotal()));

                            listPo.clear();

                            JSONArray detail = new JSONObject(result).getJSONArray("detail");
                            for(int i = 0; i < detail.length(); i++){
                                JSONObject barang = detail.getJSONObject(i);

                                List<SatuanModel> listSatuan = new ArrayList<>();
                                JSONArray jsonsatuan = barang.getJSONArray("satuan_list");

                                for(int j = 0; j < jsonsatuan.length(); j++){
                                    listSatuan.add(new SatuanModel(jsonsatuan.getString(j)));
                                }

                                BarangPOModel barangpo = new BarangPOModel(barang.getString("id"), barang.getString("kode_barang"),
                                        barang.getString("nama_barang"), barang.getDouble("harga"),
                                        barang.getInt("jumlah"), barang.getString("satuan"),
                                        barang.getDouble("total_ppn"), barang.getDouble("total"));
                                barangpo.setListSatuan(listSatuan);

                                listPo.add(barangpo);
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPODetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPODetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void hapusBarangPO(String id){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id_detail", id);
        body.add("nomor_nota", nota.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PO_HAPUS, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ApprovalPODetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalPODetail.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        //Muat ulang data PO
                        loadDetail();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPODetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
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
