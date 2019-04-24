package id.net.gmedia.pal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import id.net.gmedia.pal.Adapter.DaftarPelunasanAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaPelunasanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class DaftarPelunasan extends AppCompatActivity {

    private CustomerModel customer;

    private List<NotaPelunasanModel> listPelunasan = new ArrayList<>();
    private DaftarPelunasanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pelunasan);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Piutang Terlunasi");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_CUSTOMER)){
            Gson gson = new Gson();
            customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        }

        RecyclerView rv_pelunasan = findViewById(R.id.rv_pelunasan);
        rv_pelunasan.setItemAnimator(new DefaultItemAnimator());
        rv_pelunasan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DaftarPelunasanAdapter(this, listPelunasan);
        rv_pelunasan.setAdapter(adapter);

        loadPelunasan();
    }

    private void loadPelunasan(){
        AppLoading.getInstance().showLoading(this);

        String parameter = String.format(Locale.getDefault(),
                "/%s?search=%s", customer.getId(), Converter.encodeURL(""));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_PELUNASAN_NOTA + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listPelunasan.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listPelunasan.clear();
                            JSONArray piutang_list = new JSONObject(result).getJSONArray("bayar_piutang_list");
                            for(int i = 0; i < piutang_list.length(); i++){
                                JSONObject bayar_piutang = piutang_list.getJSONObject(i);
                                listPelunasan.add(new NotaPelunasanModel("", bayar_piutang.getString("no_bukti"),
                                        bayar_piutang.getString("tgl"), bayar_piutang.getDouble("total"),
                                        bayar_piutang.getString("keterangan"), bayar_piutang.getString("cara_bayar"),
                                        customer));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarPelunasan.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarPelunasan.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
