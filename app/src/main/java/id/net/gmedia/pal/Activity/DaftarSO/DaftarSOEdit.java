package id.net.gmedia.pal.Activity.DaftarSO;

import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class DaftarSOEdit extends AppCompatActivity {

    //Variabel global customer, barang, dan nota
    private BarangModel barang;
    private CustomerModel customer;
    private String no_nota = "";

    //Variabel global budget diskon dan diskon terpakai
    private double budget_diskon = 0;

    //Variabel UI
    private Spinner spn_satuan;
    private EditText txt_jumlah, txt_diskon;
    private TextView txt_budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_soedit);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Edit Barang SO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        spn_satuan = findViewById(R.id.spn_satuan);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        TextView txt_nama_pelanggan, txt_nama_barang, txt_harga_satuan;
        txt_nama_pelanggan = findViewById(R.id.txt_nama_pelanggan);
        txt_nama_barang = findViewById(R.id.txt_nama_barang);
        txt_harga_satuan = findViewById(R.id.txt_harga_satuan);
        txt_diskon = findViewById(R.id.txt_diskon);
        txt_budget = findViewById(R.id.txt_budget);

        //Inisialisasi data Customer dan Barang
        Gson gson = new Gson();
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        barang = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_BARANG), BarangModel.class);
        no_nota = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);

        txt_nama_pelanggan.setText(customer.getNama());
        txt_nama_barang.setText(barang.getNama());
        txt_harga_satuan.setText(Converter.doubleToRupiah(barang.getHarga()));

        //simpan data edit SO
        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek validasi edit SO
                double diskon = txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString());
                if(txt_jumlah.getText().toString().equals("")){
                    Toast.makeText(DaftarSOEdit.this, "Jumlah barang tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(spn_satuan.getSelectedItem() == null || spn_satuan.getSelectedItem().toString().equals("")){
                    Toast.makeText(DaftarSOEdit.this, "Satuan barang belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(diskon > budget_diskon){
                    Toast.makeText(DaftarSOEdit.this, "Diskon tidak boleh melebihi budget diskon", Toast.LENGTH_SHORT).show();
                }
                else{
                    //cek validasi total
                    cekTotal();
                }
            }
        });

        //muat budget diskon
        initBudget();
        //inisialisasi satuan barang
        initSatuan();
    }

    private void cekTotal(){
        //cek total dari web service
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());
        body.add("kode_barang", barang.getKode());
        body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        body.add("satuan", spn_satuan.getSelectedItem().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TOTAL_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(DaftarSOEdit.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            double total = new JSONObject(result).getDouble("total_harga");
                            double diskon = txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString());
                            //jika diskon yang dimasukkan melebihi total, penjualan tidak valid
                            if(total < diskon){
                                Toast.makeText(DaftarSOEdit.this,
                                        "Diskon tidak boleh melebihi total harga", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                AppLoading.getInstance().showLoading(DaftarSOEdit.this, R.layout.popup_loading);
                                //Jika valid, simpan barang
                                simpanBarang();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarSOEdit.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarSOEdit.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initBudget(){
        //Inisialisasi budget diskon dari web service
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_BUDGET_DISKON,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        //String budget = "Budget diskon : " + Converter.doubleToRupiah(budget_diskon + barang.getDiskon());
                        String budget = "Budget diskon : " + Converter.doubleToRupiah(budget_diskon);
                        txt_budget.setText(budget);
                    }

                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        try{
                            budget_diskon = new JSONObject(result).getJSONObject("budget_diskon").getDouble("sisa");
                            /*String budget = "Budget diskon : " + Converter.doubleToRupiah
                                    (budget_diskon + barang.getDiskon());*/
                            String budget = "Budget diskon : " + Converter.doubleToRupiah(budget_diskon);
                            txt_budget.setText(budget);
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarSOEdit.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarSOEdit.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initSatuan(){
        List<String> spinnerItem = new ArrayList<>();
        for(SatuanModel s : barang.getListSatuan()){
            spinnerItem.add(s.getSatuan());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                DaftarSOEdit.this, android.R.layout.simple_spinner_item, spinnerItem);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_satuan.setAdapter(adapter);
    }

    private void simpanBarang(){
        //menyimpan data penjualan SO ke Web Service
        JSONBuilder body = new JSONBuilder();
        body.add("nomor_nota", no_nota);
        body.add("kode_pelanggan", customer.getId());
        body.add("id", barang.getId());
        body.add("kode_barang", barang.getKode());
        body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        body.add("satuan", spn_satuan.getSelectedItem().toString());
        body.add("diskon", txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString()));

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SO_EDIT, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(DaftarSOEdit.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(DaftarSOEdit.this, result, Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent(DaftarSOEdit.this, DaftarSO.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(DaftarSOEdit.this);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(DaftarSOEdit.this, message, Toast.LENGTH_SHORT).show();
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
