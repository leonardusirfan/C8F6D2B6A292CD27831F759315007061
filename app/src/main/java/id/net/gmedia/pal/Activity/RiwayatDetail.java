package id.net.gmedia.pal.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.bluetoothprinter.BluetoothPrinter;
import com.leonardus.irfan.bluetoothprinter.Model.Item;
import com.leonardus.irfan.bluetoothprinter.Model.Transaksi;
import com.leonardus.irfan.bluetoothprinter.NotaPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.BarangDetailAdapter;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class RiwayatDetail extends AppCompatActivity {

    //Variabel global nota
    private NotaPenjualanModel nota;

    //Variabel global printer
    private NotaPrinter printerManager;

    //Variabel filter & loadmore
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    //Variabel UI
    private TextView txt_nama, txt_piutang, txt_nota;

    //Variabel data barang
    private List<BarangModel> listBarang = new ArrayList<>();
    private BarangDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_detail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat Penjualan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi data global tipe penjualan, nota
        int type = getIntent().getIntExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
        if(getIntent().hasExtra(Constant.EXTRA_NOTA)){
            Gson gson = new Gson();
            nota = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_NOTA), NotaPenjualanModel.class);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_nota = findViewById(R.id.txt_nota);

        //Inisialisasi RecyclerView & Adapter
        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BarangDetailAdapter(this, listBarang, type);
        rv_riwayat.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBarang(false);
            }
        };
        rv_riwayat.addOnScrollListener(loadMoreScrollListener);

        //muat data barang
        loadBarang(true);
    }

    private void loadBarang(final boolean init){
        //Membaca data barang dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "/%s?start=%d&limit=%d&search=%s", nota.getId(),
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RIWAYAT + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            //Inisialisasi Header
                            JSONObject obj = new JSONObject(result).getJSONObject("detail");
                            txt_nama.setText(obj.getString("nama_pelanggan"));
                            txt_piutang.setText(Converter.doubleToRupiah(obj.getDouble("total")));
                            txt_nota.setText(nota.getId());

                            if(init){
                                listBarang.clear();
                            }

                            JSONArray array = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject item = array.getJSONObject(i);
                                BarangModel barang = new BarangModel(item.getString("id"), item.getString("nama_barang"),
                                        item.getDouble("harga_satuan"), item.getInt("jumlah"), item.getString("satuan"),
                                        item.getDouble("diskon_rupiah"), item.getDouble("harga_total"));

                                listBarang.add(barang);
                            }

                            loadMoreScrollListener.finishLoad(array.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(RiwayatDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RiwayatDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_nota_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_print:
                //print nota
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_pal_nota);
                printerManager = new NotaPrinter(this, Bitmap.createScaledBitmap(
                        logo, 170, 170, false));
                printerManager.startService();
                printerManager.setListener(new BluetoothPrinter.BluetoothListener() {
                    @Override
                    public void onBluetoothConnected() {
                        List<Item> listItem = new ArrayList<>();
                        double total_diskon = 0;

                        for(BarangModel b : listBarang){
                            listItem.add(new Item(b.getNama(), b.getJumlah(), b.getHarga()));
                            total_diskon += b.getDiskon();
                        }

                        Transaksi transaksi = new Transaksi(txt_nama.getText().toString(),
                                AppSharedPreferences.getNama(RiwayatDetail.this),
                                txt_nota.getText().toString(), new Date(), listItem);
                        transaksi.setTunai(nota.getTotal());
                        transaksi.setDiskon(total_diskon);

                        printerManager.print(transaksi);
                    }

                    @Override
                    public void onBluetoothFailed(String message) {
                        Toast.makeText(RiwayatDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}
