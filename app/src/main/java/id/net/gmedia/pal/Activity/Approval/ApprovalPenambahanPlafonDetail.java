package id.net.gmedia.pal.Activity.Approval;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPenambahanPlafonDetail extends AppCompatActivity {

    private String id_pengajuan;

    private List<SimpleObjectModel> listApproval;

    //Variabel UI
    private TextView txt_nominal_sebelumnya, txt_tanggal_sebelumnya, txt_durasi_sebelumnya,
            txt_minimal_penjualan, txt_maksimal_penjualan, txt_rata_penjualan, txt_minimal_pembayaran,
            txt_maksimal_pembayaran, txt_rata_pembayaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_penambahan_plafon_detail);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Pengajuan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_ID_PENGAJUAN_PLAFON)){
            id_pengajuan = getIntent().getStringExtra(Constant.EXTRA_ID_PENGAJUAN_PLAFON);
        }

        //Inisialisasi UI
        txt_nominal_sebelumnya = findViewById(R.id.txt_nominal_sebelumnya);
        txt_tanggal_sebelumnya = findViewById(R.id.txt_tanggal_sebelumnya);
        txt_durasi_sebelumnya = findViewById(R.id.txt_durasi_sebelumnya);
        txt_minimal_penjualan = findViewById(R.id.txt_minimal_penjualan);
        txt_maksimal_penjualan = findViewById(R.id.txt_maksimal_penjualan);
        txt_rata_penjualan = findViewById(R.id.txt_rata_penjualan);
        txt_minimal_pembayaran = findViewById(R.id.txt_minimal_pembayaran);
        txt_maksimal_pembayaran = findViewById(R.id.txt_maksimal_pembayaran);
        txt_rata_pembayaran = findViewById(R.id.txt_rata_pembayaran);

        findViewById(R.id.btn_approval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listApproval == null){
                    Toast.makeText(ApprovalPenambahanPlafonDetail.this,
                            "Data Approval belum termuat", Toast.LENGTH_SHORT).show();
                }
                else{
                    showApproval();
                }
            }
        });

        loadPengajuan();
        loadApproval();
    }

    private void showApproval(){
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.btn_approval), Gravity.END);

        for(int i = 0; i < listApproval.size(); i++){
            popup.getMenu().add(0, i, i, listApproval.get(i).getValue());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                responApproval(listApproval.get(menuItem.getItemId()).getId());
                return false;
            }
        });

        popup.show();
    }

    private void responApproval(String id_approval){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id_pengajuan);
        body.add("approval", id_approval);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PLAFON_APPROVE,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ApprovalPenambahanPlafonDetail.this, result, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();

                        onBackPressed();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPenambahanPlafonDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadPengajuan(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "/%s", id_pengajuan);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PLAFON_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ApprovalPenambahanPlafonDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().showLoading(ApprovalPenambahanPlafonDetail.this, R.layout.popup_loading);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject request_plafon = new JSONObject(result);

                            JSONObject prev_plafon = request_plafon.getJSONObject("prev_plafon");
                            txt_nominal_sebelumnya.setText(Converter.doubleToRupiah(prev_plafon.getDouble("nominal")));
                            txt_tanggal_sebelumnya.setText(prev_plafon.getString("tanggal"));
                            txt_durasi_sebelumnya.setText(prev_plafon.getString("durasi_bulan"));

                            JSONObject penjualan = request_plafon.getJSONObject("penjualan");
                            txt_minimal_penjualan.setText(Converter.doubleToRupiah(penjualan.getDouble("minimal")));
                            txt_maksimal_penjualan.setText(Converter.doubleToRupiah(penjualan.getDouble("maksimal")));
                            txt_rata_penjualan.setText(Converter.doubleToRupiah(penjualan.getDouble("rata_rata")));

                            JSONObject pembayaran = request_plafon.getJSONObject("pembayaran");
                            txt_minimal_pembayaran.setText(Converter.doubleToRupiah(pembayaran.getDouble("minimal")));
                            txt_maksimal_pembayaran.setText(Converter.doubleToRupiah(pembayaran.getDouble("maksimal")));
                            txt_rata_pembayaran.setText(Converter.doubleToRupiah(pembayaran.getDouble("rata_rata")));
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPenambahanPlafonDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPenambahanPlafonDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().showLoading(ApprovalPenambahanPlafonDetail.this, R.layout.popup_loading);
                    }
                }));
    }

    public void loadApproval(){
        //Memuat data respon showApproval dari Web Service
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MASTER_APPROVAL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            listApproval = new ArrayList<>();

                            JSONArray approval = new JSONObject(result).getJSONArray("approval");
                            for(int i = 0; i < approval.length(); i++){
                                listApproval.add(new SimpleObjectModel(approval.getJSONObject(i).getString("kode"),
                                        approval.getJSONObject(i).getString("nama")));
                            }

                            //adapter.setListApproval(listApproval);
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPenambahanPlafonDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPenambahanPlafonDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
