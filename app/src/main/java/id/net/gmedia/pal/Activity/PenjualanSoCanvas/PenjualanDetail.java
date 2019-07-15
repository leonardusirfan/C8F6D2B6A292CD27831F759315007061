package id.net.gmedia.pal.Activity.PenjualanSoCanvas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class PenjualanDetail extends AppCompatActivity {

    //Variabel global flag apakah mode edit
    private int edit;
    //Variabel global data barang
    private BarangModel barang;

    //Variabel global budget diskon
    private double budget_diskon = 0;

    //Variabel Ui
    private Spinner spn_satuan;
    private EditText txt_jumlah, txt_diskon, txt_jumlah_canvas;
    private TextView txt_stok, txt_budget, txt_stok_canvas;
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
        TextView txt_nama_pelanggan, txt_nama_barang, txt_harga_satuan;
        spn_satuan = findViewById(R.id.spn_satuan);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_nama_pelanggan = findViewById(R.id.txt_nama_pelanggan);
        txt_nama_barang = findViewById(R.id.txt_nama_barang);
        txt_stok = findViewById(R.id.txt_stok);
        txt_harga_satuan = findViewById(R.id.txt_harga_satuan);
        txt_diskon = findViewById(R.id.txt_diskon);
        txt_budget = findViewById(R.id.txt_budget);
        txt_jumlah_canvas = findViewById(R.id.txt_jumlah_canvas);
        txt_stok_canvas = findViewById(R.id.txt_stok_canvas);
        /*txt_total = findViewById(R.id.txt_total);
        txt_total.setText(Converter.doubleToRupiah(total));*/

        //Inisialisasi data global jenis penjualan, customer, barang, mode edit
        Gson gson = new Gson();

        barang = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_BARANG), BarangModel.class);
        edit = getIntent().getIntExtra(Constant.EXTRA_EDIT, -1);
        if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
            findViewById(R.id.layout_potong_stok_canvas).setVisibility(View.VISIBLE);
        }

        //Inisialisasi nilai UI
        txt_nama_pelanggan.setText(AppKeranjangPenjualan.getInstance().getCustomer().getNama());
        txt_nama_barang.setText(barang.getNama());
        txt_harga_satuan.setText(Converter.doubleToRupiah(barang.getHarga()));

        //button tambah barang ke nota penjualan
        findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek validasi input
                double diskon = txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString());
                int jumlah_canvas = txt_jumlah_canvas.getText().toString().equals("")?0:Integer.parseInt(txt_jumlah_canvas.getText().toString());

                if(txt_jumlah.getText().toString().equals("")){
                    Toast.makeText(PenjualanDetail.this, "Jumlah barang tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(spn_satuan.getSelectedItem() == null || spn_satuan.getSelectedItem().toString().equals("")){
                    Toast.makeText(PenjualanDetail.this, "Satuan barang belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(diskon > budget_diskon){
                    Toast.makeText(PenjualanDetail.this, "Diskon tidak boleh melebihi budget diskon", Toast.LENGTH_SHORT).show();
                }
                else if(!barang.getTipe().equals("move") && diskon > 0){
                    Toast.makeText(PenjualanDetail.this, "Maaf, barang ini tidak bisa di-diskon", Toast.LENGTH_SHORT).show();
                    txt_diskon.setText("0");
                }
                else if(Integer.parseInt(txt_jumlah.getText().toString()) > barang.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getJumlah()){
                    txt_jumlah.setText(String.valueOf(barang.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getJumlah()));
                    //txt_total.setText(Converter.doubleToRupiah(Integer.parseInt(txt_jumlah.getText().toString()) * barang.getHarga()));
                    Toast.makeText(PenjualanDetail.this, "Jumlah barang tidak boleh melebihi stok barang", Toast.LENGTH_SHORT).show();
                }
                else if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO &&
                        jumlah_canvas > barang.getListSatuanCanvas().get(spn_satuan.getSelectedItemPosition()).getJumlah()){
                    txt_jumlah_canvas.setText(String.valueOf(barang.getListSatuanCanvas().get(spn_satuan.getSelectedItemPosition()).getJumlah()));
                    //txt_total.setText(Converter.doubleToRupiah(Integer.parseInt(txt_jumlah.getText().toString()) * barang.getHarga()));
                    Toast.makeText(PenjualanDetail.this, "Jumlah barang tidak boleh melebihi stok canvas", Toast.LENGTH_SHORT).show();
                }
                else if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO &&
                        diskon > 0 && AppKeranjangPenjualan.getInstance().getTotalBarangDiskon(edit)
                        + Integer.parseInt(txt_jumlah.getText().toString()) + jumlah_canvas > 20){
                    //txt_total.setText(Converter.doubleToRupiah(Integer.parseInt(txt_jumlah.getText().toString()) * barang.getHarga()));
                    Toast.makeText(PenjualanDetail.this, "Jumlah seluruh barang diskon tidak boleh lebih dari 20", Toast.LENGTH_SHORT).show();
                }
                else if(diskon > 0 && AppKeranjangPenjualan.getInstance().getTotalBarangDiskon(edit)
                        + Integer.parseInt(txt_jumlah.getText().toString()) > 20){
                    Toast.makeText(PenjualanDetail.this, "Jumlah seluruh barang diskon tidak boleh lebih dari 20", Toast.LENGTH_SHORT).show();
                }
                else{
                    //cek harga total barang
                    cekTotal();
                }
            }
        });

        //muat data budget
        initBudget();
        //muat data satuan barang
        initSatuan();
    }

    private void cekTotal(){
        //cek harga total barang yang akan ditambahkan
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", AppKeranjangPenjualan.getInstance().getCustomer().getId());
        body.add("kode_barang", edit == -1?barang.getId():AppKeranjangPenjualan.getInstance().getBarang(edit).getId());
        if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
            int jumlah_potong = txt_jumlah_canvas.getText().toString().equals("")?0:Integer.parseInt(txt_jumlah_canvas.getText().toString());
            body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()) + jumlah_potong);
        }
        else{
            body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        }

        body.add("satuan", spn_satuan.getSelectedItem().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_TOTAL_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(PenjualanDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            double total = new JSONObject(result).getDouble("total_harga");
                            double diskon = txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString());
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
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PenjualanDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void initBudget(){
        //Membaca data budget diskon dari Web Service
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_BUDGET_DISKON,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        String budget = "Budget diskon : " + Converter.doubleToRupiah(budget_diskon);
                        txt_budget.setText(budget);
                    }

                    @Override
                    public void onSuccess(String result) {
                        //System.out.println(result);
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
        //Inisialisasi satuan barang
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

                if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                    String stok_canvas = barang.getListSatuanCanvas().get(position).getJumlah() + " " + spn_satuan.getItemAtPosition(position).toString();
                    txt_stok_canvas.setText(stok_canvas);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void tambahBarang(double subtotal){
        //tambah barang ke nota penjualan
        if(edit == -1){
            //jika bukan edit, tambah ke keranjang penjualan
            if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                int jumlah_potong = txt_jumlah_canvas.getText().toString().equals("")?0:
                        Integer.parseInt(txt_jumlah_canvas.getText().toString());
                barang.setJumlah_potong(jumlah_potong);
                barang.setJumlah(Integer.parseInt(txt_jumlah.getText().toString()) + jumlah_potong);
            }
            else if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_CANVAS){
                barang.setJumlah(Integer.parseInt(txt_jumlah.getText().toString()));
            }

            barang.setDiskon(txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString()));
            barang.setSatuan(spn_satuan.getSelectedItem().toString());
            barang.setSubtotal(subtotal);
            AppKeranjangPenjualan.getInstance().pakai_budget(txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString()));

            AppKeranjangPenjualan.getInstance().addBarang(barang);
        }
        else{
            if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_SO){
                int jumlah_potong = txt_jumlah_canvas.getText().toString().equals("")?0:
                        Integer.parseInt(txt_jumlah_canvas.getText().toString());
                AppKeranjangPenjualan.getInstance().getBarang(edit).setJumlah_potong(jumlah_potong);
                AppKeranjangPenjualan.getInstance().getBarang(edit).setJumlah(Integer.parseInt(txt_jumlah.getText().toString()) + jumlah_potong);
            }
            else if(AppKeranjangPenjualan.getInstance().getJENIS_PENJUALAN() == Constant.PENJUALAN_CANVAS){
                AppKeranjangPenjualan.getInstance().getBarang(edit).setJumlah(Integer.parseInt(txt_jumlah.getText().toString()));
            }

            AppKeranjangPenjualan.getInstance().getBarang(edit).setDiskon(txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString()));
            AppKeranjangPenjualan.getInstance().getBarang(edit).setSatuan(spn_satuan.getSelectedItem().toString());
            AppKeranjangPenjualan.getInstance().getBarang(edit).setSubtotal(subtotal);
            AppKeranjangPenjualan.getInstance().edit_pakai_budget(AppKeranjangPenjualan.getInstance().getBarang(edit).getDiskon(), txt_diskon.getText().toString().equals("")?0:Double.parseDouble(txt_diskon.getText().toString()));
        }

        Intent i = new Intent(PenjualanDetail.this, PenjualanNota.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
