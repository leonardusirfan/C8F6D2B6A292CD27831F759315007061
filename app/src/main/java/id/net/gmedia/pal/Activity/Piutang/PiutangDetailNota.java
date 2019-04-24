package id.net.gmedia.pal.Activity.Piutang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.BarangDetailAdapter;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class PiutangDetailNota extends AppCompatActivity {

    //variabel global tipe nota
    public int type;

    //Variabel global nomor nota, dan nama customer
    private String nomor_nota = "";
    private String nama_customer = "";

    //Variabel UI
    private TextView txt_nama, txt_piutang, txt_nota;

    //Variabel data barang
    private List<BarangModel> listBarang = new ArrayList<>();
    private BarangDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang_detail_nota);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Nota Piutang");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //inisialisasi data global tipe nota, nomor nota, nama customer
        type = getIntent().getIntExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
        nomor_nota = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);
        nama_customer = getIntent().getStringExtra(Constant.EXTRA_NAMA_CUSTOMER);

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_nota = findViewById(R.id.txt_nota);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_piutang = findViewById(R.id.rv_piutang);
        rv_piutang.setItemAnimator(new DefaultItemAnimator());
        rv_piutang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BarangDetailAdapter(this, listBarang, type);
        rv_piutang.setAdapter(adapter);

        //muat data barang
        loadBarang();
    }

    private void loadBarang(){
        //Membaca data barang dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PIUTANG_NOTA + nomor_nota, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject obj = new JSONObject(result);

                            txt_nama.setText(nama_customer);
                            txt_piutang.setText(Converter.doubleToRupiah(obj.getJSONObject("piutang").getDouble("piutang")));
                            txt_nota.setText(nomor_nota);

                            JSONArray array = obj.getJSONArray("barang_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject item = array.getJSONObject(i);
                                BarangModel barang = new BarangModel(item.getString("kode_barang"),
                                        item.getString("nama_barang"),
                                        item.getDouble("harga_satuan"),
                                        item.getInt("jumlah"), item.getString("satuan"),
                                        item.getDouble("diskon_rupiah"), item.getDouble("harga_total"));
                                listBarang.add(barang);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(PiutangDetailNota.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(PiutangDetailNota.this, message, Toast.LENGTH_SHORT).show();

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
