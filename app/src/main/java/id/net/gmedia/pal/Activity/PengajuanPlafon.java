package id.net.gmedia.pal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import java.util.Locale;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.PengajuanPlafonModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class PengajuanPlafon extends AppCompatActivity {

    private String id = "";
    private boolean edit = false;

    private EditText txt_plafon_nota, txt_plafon_nominal, txt_alasan_pengajuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_plafon);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Penambahan plafon");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_plafon_nota = findViewById(R.id.txt_plafon_nota);
        txt_plafon_nominal = findViewById(R.id.txt_plafon_nominal);
        txt_alasan_pengajuan = findViewById(R.id.txt_alasan_pengajuan);

        //Inisialisasi Customer
        Gson gson = new Gson();
        if(getIntent().hasExtra(Constant.EXTRA_CUSTOMER)){
            CustomerModel customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);

            id = customer.getId();
            edit = false;

            ((TextView)findViewById(R.id.txt_nama_customer)).
                    setText(customer.getNama());
        }
        else if(getIntent().hasExtra(Constant.EXTRA_PENGAJUAN_PLAFON)){
            PengajuanPlafonModel plafon = gson.fromJson(getIntent().getStringExtra
                    (Constant.EXTRA_PENGAJUAN_PLAFON), PengajuanPlafonModel.class);

            id = plafon.getId();
            edit = true;

            ((TextView)findViewById(R.id.txt_nama_customer)).
                    setText(plafon.getCustomer().getNama());
            txt_plafon_nota.setText(String.valueOf(plafon.getPlafon_nota()));
            txt_plafon_nominal.setText(String.format(Locale.getDefault(), "%.0f", plafon.getPlafon_nominal()));
            txt_alasan_pengajuan.setText(plafon.getAlasan());
        }

        //button ajukan plafon
        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_plafon_nota.getText().toString().equals("")){
                    Toast.makeText(PengajuanPlafon.this, "Plafon nota tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(txt_plafon_nominal.getText().toString().equals("")){
                    Toast.makeText(PengajuanPlafon.this, "Plafon nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(txt_alasan_pengajuan.getText().toString().equals("")){
                    Toast.makeText(PengajuanPlafon.this, "Alasan pengajuan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(edit){
                        editPlafon();
                    }
                    else{
                        ajukanPlafon();
                    }
                }
            }
        });
    }

    private void ajukanPlafon(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", id);
        body.add("plafon_nota", txt_plafon_nota.getText().toString());
        body.add("plafon_nominal", txt_plafon_nominal.getText().toString());
        body.add("alasan_penambahan", txt_alasan_pengajuan.getText().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PLAFON_REQUEST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(PengajuanPlafon.this, result, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(PengajuanPlafon.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(PengajuanPlafon.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void editPlafon(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);
        body.add("nota", txt_plafon_nota.getText().toString());
        body.add("nominal", txt_plafon_nominal.getText().toString());
        body.add("keterangan", txt_alasan_pengajuan.getText().toString());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PLAFON_EDIT, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(PengajuanPlafon.this, result, Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(PengajuanPlafon.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
