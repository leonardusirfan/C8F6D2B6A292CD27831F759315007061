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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.ApprovalReturDetailAdapter;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.Model.ReturModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalReturDetail extends AppCompatActivity {

    //Variabel global nota
    private NotaPenjualanModel nota;

    //Variabel UI
    private TextView txt_nama, txt_total, txt_nota;

    //Variabel data retur
    private List<ReturModel> listRetur = new ArrayList<>();
    private ApprovalReturDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Retur");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi nota dari Intent
        if(getIntent().hasExtra(Constant.EXTRA_NOTA)){
            Gson gson = new Gson();
            nota = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_NOTA), NotaPenjualanModel.class);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_total = findViewById(R.id.txt_piutang);
        txt_nota = findViewById(R.id.txt_nota);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ApprovalReturDetailAdapter(this, listRetur);
        rv_riwayat.setAdapter(adapter);

        //Muat data detail retur
        loadRetur();
    }

    private void loadRetur(){
        //Membaca data detail retur dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "/%s", nota.getId());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RETUR_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listRetur.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listRetur.clear();

                            txt_nama.setText(nota.getCustomer().getNama());
                            txt_total.setText(Converter.doubleToRupiah(nota.getTotal()));
                            txt_nota.setText(nota.getId());

                            JSONArray detail = new JSONObject(result).getJSONArray("detail");
                            for(int i = 0; i < detail.length(); i++){
                                JSONObject retur = detail.getJSONObject(i);
                                listRetur.add(new ReturModel(retur.getString("kode_barang"),
                                        retur.getString("nama_barang"), retur.getInt("jumlah"),
                                        retur.getString("satuan"), retur.getString("alasan"),
                                        retur.getString("image")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalReturDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalReturDetail.this, message, Toast.LENGTH_SHORT).show();
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
