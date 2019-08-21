package id.net.gmedia.pal.Activity.Approval;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.ApprovalPenambahanPlafonAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.PengajuanPlafonModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPenambahanPlafon extends AppCompatActivity {

    private List<PengajuanPlafonModel> listPengajuan = new ArrayList<>();
    private ApprovalPenambahanPlafonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penambahan_plafon);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval Penambahan Plafon");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_plafon = findViewById(R.id.rv_plafon);
        rv_plafon.setItemAnimator(new DefaultItemAnimator());
        rv_plafon.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalPenambahanPlafonAdapter(this, listPengajuan);
        rv_plafon.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        loadPengajuan();
        super.onResume();
    }

    private void loadPengajuan(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s", 0, 10, "");
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PLAFON_LIST + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listPengajuan.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(ApprovalPenambahanPlafon.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listPengajuan.clear();
                            JSONArray plafon_list = new JSONObject(result).getJSONArray("plafon_list");
                            for(int i = 0; i < plafon_list.length(); i++){
                                JSONObject plafon = plafon_list.getJSONObject(i);
                                listPengajuan.add(new PengajuanPlafonModel(plafon.getString("id"),
                                        new CustomerModel(plafon.getString("kode_pelanggan"),
                                                plafon.getString("nama_pelanggan")),
                                        plafon.getInt("plafon_nota"), plafon.getDouble("plafon_nominal"),
                                        plafon.getString("alasan_penambahan")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPenambahanPlafon.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPenambahanPlafon.this, message, Toast.LENGTH_SHORT).show();
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
