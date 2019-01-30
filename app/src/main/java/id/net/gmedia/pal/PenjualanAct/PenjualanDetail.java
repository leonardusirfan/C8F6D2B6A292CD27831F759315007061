package id.net.gmedia.pal.PenjualanAct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.JSONBuilder;

public class PenjualanDetail extends AppCompatActivity {

    private int JENIS_PENJUALAN;
    private BarangModel barang;
    private CustomerModel customer;
    private int edit;

    private double budget_diskon = 0;

    //private double total = 0;
    private double diskon = 0;

    private Spinner spn_satuan;
    private EditText txt_jumlah, txt_diskon;

    TextView txt_nama_pelanggan, txt_nama_barang, txt_stok, txt_harga_satuan, txt_budget;
    //TextView txt_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        spn_satuan = findViewById(R.id.spn_satuan);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_nama_pelanggan = findViewById(R.id.txt_nama_pelanggan);
        txt_nama_barang = findViewById(R.id.txt_nama_barang);
        /*txt_total = findViewById(R.id.txt_total);
        txt_total.setText(Converter.doubleToRupiah(total));*/
        txt_stok = findViewById(R.id.txt_stok);
        txt_harga_satuan = findViewById(R.id.txt_harga_satuan);
        txt_diskon = findViewById(R.id.txt_diskon);
        txt_budget = findViewById(R.id.txt_budget);

        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);
        Gson gson = new Gson();
        customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        barang = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_BARANG), BarangModel.class);
        txt_nama_pelanggan.setText(customer.getNama());
        txt_nama_barang.setText(barang.getNama());
        txt_harga_satuan.setText(Converter.doubleToRupiah(barang.getHarga()));

        edit = getIntent().getIntExtra(Constant.EXTRA_EDIT, -1);

        if(JENIS_PENJUALAN == Constant.PENJUALAN_CANVAS){
            txt_stok.setVisibility(View.VISIBLE);
        }

       /* txt_jumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    total = 0;
                }
                else{
                    total = Integer.parseInt(s.toString()) * barang.getHarga() - diskon;
                }
                txt_total.setText(Converter.doubleToRupiah(total));
            }
        });*/

        txt_diskon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    diskon = 0;
                }
                else{
                    diskon = Double.parseDouble(txt_diskon.getText().toString());
                }
                //txt_total.setText(Converter.doubleToRupiah(total - diskon));
            }
        });

        findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_jumlah.getText().toString().equals("")){
                    Toast.makeText(PenjualanDetail.this, "Jumlah barang tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(spn_satuan.getSelectedItem() == null || spn_satuan.getSelectedItem().toString().equals("")){
                    Toast.makeText(PenjualanDetail.this, "Satuan barang belum dipilih", Toast.LENGTH_SHORT).show();
                }
                /*else if(total < diskon){
                    Toast.makeText(PenjualanDetail.this, "Diskon tidak boleh melebihi jumlah total", Toast.LENGTH_SHORT).show();
                }*/
                else if(diskon > budget_diskon){
                    Toast.makeText(PenjualanDetail.this, "Diskon tidak boleh melebihi budget diskon", Toast.LENGTH_SHORT).show();
                }
                else if(JENIS_PENJUALAN == Constant.PENJUALAN_CANVAS &&
                        Integer.parseInt(txt_jumlah.getText().toString()) > barang.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getJumlah()){
                    txt_jumlah.setText(String.valueOf(barang.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getJumlah()));
                    //txt_total.setText(Converter.doubleToRupiah(Integer.parseInt(txt_jumlah.getText().toString()) * barang.getHarga()));
                    Toast.makeText(PenjualanDetail.this, "Jumlah barang tidak boleh melebihi stok barang", Toast.LENGTH_SHORT).show();
                }
                else{
                    cekTotal();
                    //tambahBarang();
                }
            }
        });

        initBudget();
        initSatuan();
    }

    private void cekTotal(){
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", customer.getId());
        body.add("kode_barang", edit == -1?barang.getId():AppKeranjangPenjualan.getInstance().getBarang(edit).getId());
        body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        body.add("satuan", spn_satuan.getSelectedItem().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TOTAL_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PenjualanDetail.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            double total = new JSONObject(result).getDouble("total_harga");
                            if(total < diskon){
                                Toast.makeText(PenjualanDetail.this,
                                        "Diskon tidak boleh melebihi total harga", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                tambahBarang(total);
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PenjualanDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initBudget(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_BUDGET_DISKON,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        String budget = "Budget diskon : " + Converter.doubleToRupiah(budget_diskon);
                        txt_budget.setText(budget);
                    }

                    @Override
                    public void onSuccess(String result) {
                        System.out.println(result);
                        try{
                            budget_diskon = new JSONObject(result).getJSONObject("budget_diskon").getDouble("sisa");
                            if(edit == -1){
                                String budget = "Budget diskon : " + Converter.doubleToRupiah
                                        (budget_diskon - AppKeranjangPenjualan.getInstance().getBudget_terpakai());
                                txt_budget.setText(budget);
                            }
                            else {
                                String budget = "Budget diskon : " + Converter.doubleToRupiah
                                        (budget_diskon - AppKeranjangPenjualan.getInstance().getBudget_terpakai()
                                                + AppKeranjangPenjualan.getInstance().getBarang(edit).getDiskon());
                                txt_budget.setText(budget);
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(PenjualanDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initSatuan(){
        List<String> spinnerItem = new ArrayList<>();
        for(SatuanModel s : barang.getListSatuan()){
            spinnerItem.add(s.getSatuan());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                PenjualanDetail.this, android.R.layout.simple_spinner_item, spinnerItem);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_satuan.setAdapter(adapter);

        spn_satuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stok = barang.getListSatuan().get(position).getJumlah() + " " + spn_satuan.getItemAtPosition(position).toString();
                txt_stok.setText(stok);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void tambahBarang(double subtotal){
        if(edit == -1){
            barang.setJumlah(Integer.parseInt(txt_jumlah.getText().toString()));
            barang.setDiskon(diskon);
            barang.setSatuan(spn_satuan.getSelectedItem().toString());
            barang.setSubtotal(subtotal);
            System.out.println("SATUAN : " + barang.getSatuan());
            AppKeranjangPenjualan.getInstance().pakai_budget(diskon);

            AppKeranjangPenjualan.getInstance().addBarang(barang);
        }
        else{
            AppKeranjangPenjualan.getInstance().edit_pakai_budget(
                    AppKeranjangPenjualan.getInstance().getBarang(edit).getDiskon(), diskon);

            AppKeranjangPenjualan.getInstance().getBarang(edit).setJumlah(Integer.parseInt(txt_jumlah.getText().toString()));
            AppKeranjangPenjualan.getInstance().getBarang(edit).setDiskon(diskon);
            AppKeranjangPenjualan.getInstance().getBarang(edit).setSatuan(spn_satuan.getSelectedItem().toString());
            AppKeranjangPenjualan.getInstance().getBarang(edit).setSubtotal(subtotal);
            System.out.println("SATUAN : " + AppKeranjangPenjualan.getInstance().getBarang(edit).getSatuan());
        }

        Gson gson = new Gson();
        Intent i = new Intent(PenjualanDetail.this, PenjualanNota.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
        i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, JENIS_PENJUALAN);
        if(getIntent().hasExtra(Constant.EXTRA_TANGGAL_TEMPO)){
            i.putExtra(Constant.EXTRA_TANGGAL_TEMPO, getIntent().getStringExtra(Constant.EXTRA_TANGGAL_TEMPO));
        }
        startActivity(i);
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
