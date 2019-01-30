package id.net.gmedia.pal.DaftarSoAct;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

import id.net.gmedia.pal.BarangDetailAdapter;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class DaftarSODetail extends AppCompatActivity {

    private String search = "";

    public CustomerModel customer;
    private TextView txt_nama, txt_piutang, txt_nota;
    public NotaModel nota;
    private int loaded = 0;

    private List<BarangModel> listBarang = new ArrayList<>();
    private LoadMoreScrollListener loadMoreScrollListener;
    private RecyclerView.Adapter adapter;

    private NotaPrinter printerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_sodetail);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail SO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_nama = findViewById(R.id.txt_nama);
        txt_piutang = findViewById(R.id.txt_piutang);
        txt_nota = findViewById(R.id.txt_nota);

        if(getIntent().hasExtra(Constant.EXTRA_NOTA)){
            Gson gson = new Gson();
            nota = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_NOTA), NotaModel.class);
        }

        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if(getIntent().hasExtra(Constant.EXTRA_EDIT)){
            adapter = new DaftarSODetailAdapter(this, listBarang, Constant.PENJUALAN_SO);
        }
        else{
            adapter = new BarangDetailAdapter(this, listBarang, Constant.PENJUALAN_SO);
        }

        rv_riwayat.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadBarang(false);
            }
        });
        rv_riwayat.addOnScrollListener(loadMoreScrollListener);

        loadBarang(true);
    }

    private void loadBarang(final boolean init){
        AppLoading.getInstance().showLoading(this);
        if(init){
            loaded = 0;
        }

        String parameter = String.format(Locale.getDefault(), "%s?start=%d&limit=%d&search=%s", nota.getId(), loaded, 10, search);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_SO_DETAIL + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                        loadMoreScrollListener.cantLoad();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            //Inisialisasi Header
                            JSONObject obj = new JSONObject(result).getJSONObject("nota");
                            customer = new CustomerModel(obj.getString("kode_pelanggan"), obj.getString("nama_pelanggan"));
                            txt_nama.setText(customer.getNama());
                            txt_piutang.setText(Converter.doubleToRupiah(obj.getDouble("total")));
                            txt_nota.setText(nota.getId());

                            if(init){
                                listBarang.clear();
                                loadMoreScrollListener.canLoad();
                            }

                            JSONArray array = new JSONObject(result).getJSONArray("barang_list");
                            for(int i = 0; i < array.length(); i++){
                                JSONObject item = array.getJSONObject(i);
                                List<SatuanModel> satuan = new ArrayList<>();

                                JSONArray array_satuan = item.getJSONArray("satuan_list");

                                for(int j = 0; j < array_satuan.length(); j++){
                                    satuan.add(new SatuanModel(array_satuan.getString(j)));
                                }

                                BarangModel barang = new BarangModel(item.getString("id"), item.getString("kode_barang"),
                                        item.getString("nama_barang"), item.getDouble("harga_satuan"),
                                        item.getInt("jumlah"), item.getString("satuan"),
                                        item.getDouble("diskon_rupiah"), item.getDouble("harga_total"));
                                barang.setListSatuan(satuan);

                                listBarang.add(barang);

                                loaded += 1;
                            }

                            if(array.length() == 0){
                                loadMoreScrollListener.cantLoad();
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarSODetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarSODetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nota_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_print:
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
                                AppSharedPreferences.getNama(DaftarSODetail.this),
                                txt_nota.getText().toString(), new Date(), listItem);
                        transaksi.setTunai(nota.getTotal());
                        transaksi.setDiskon(total_diskon);

                        printerManager.print(transaksi);
                    }

                    @Override
                    public void onBluetoothFailed(String message) {
                        Toast.makeText(DaftarSODetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(printerManager != null){
            printerManager.stopService();
        }
        super.onDestroy();
    }
}
