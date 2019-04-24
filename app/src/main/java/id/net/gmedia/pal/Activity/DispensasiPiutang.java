package id.net.gmedia.pal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class DispensasiPiutang extends AppCompatActivity {

    //Variabel global id Customer
    private String id_customer;

    //Variabel UI
    private EditText txt_keterangan;
    private TextView txt_minimal_penjualan, txt_maksimal_penjualan, txt_rata_penjualan,
            txt_minimal_pembayaran, txt_maksimal_pembayaran, txt_rata_pembayaran, txt_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensasi_piutang);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Dispensasi Piutang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi variable global id Customer
        if(getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            id_customer = getIntent().getStringExtra(Constant.EXTRA_ID_CUSTOMER);
        }

        //Inisialisasi UI
        txt_keterangan = findViewById(R.id.txt_keterangan);
        txt_minimal_penjualan = findViewById(R.id.txt_minimal_penjualan);
        txt_maksimal_penjualan = findViewById(R.id.txt_maksimal_penjualan);
        txt_rata_penjualan = findViewById(R.id.txt_rata_penjualan);
        txt_minimal_pembayaran = findViewById(R.id.txt_minimal_pembayaran);
        txt_maksimal_pembayaran = findViewById(R.id.txt_maksimal_pembayaran);
        txt_rata_pembayaran = findViewById(R.id.txt_rata_pembayaran);
        txt_customer = findViewById(R.id.txt_customer);

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_keterangan.getText().toString().equals("")){
                    Toast.makeText(DispensasiPiutang.this,
                            "Keterangan dispensasi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    ajukanDispensasi();
                }
            }
        });

        loadRekap();
    }

    private void loadRekap(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        String parameter = String.format(Locale.getDefault(), "/%s", id_customer);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_REKAP + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(DispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);

                            txt_customer.setText(response.getJSONObject("pelanggan").getString("nama"));

                            JSONObject penjualan = response.getJSONObject("penjualan");
                            txt_minimal_penjualan.setText(penjualan.getString("minimal"));
                            txt_maksimal_penjualan.setText(penjualan.getString("maksimal"));
                            txt_rata_penjualan.setText(penjualan.getString("rata_rata"));

                            JSONObject pembayaran = response.getJSONObject("pembayaran");
                            txt_minimal_pembayaran.setText(pembayaran.getString("minimal"));
                            txt_maksimal_pembayaran.setText(pembayaran.getString("maksimal"));
                            txt_rata_pembayaran.setText(pembayaran.getString("rata_rata"));
                        }
                        catch (JSONException e){
                            Toast.makeText(DispensasiPiutang.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void ajukanDispensasi(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", id_customer);
        body.add("keterangan_dispensasi", txt_keterangan.getText().toString());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DISPENSASI_REQUEST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(DispensasiPiutang.this, result, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(DispensasiPiutang.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DispensasiPiutang.this, message, Toast.LENGTH_SHORT).show();
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