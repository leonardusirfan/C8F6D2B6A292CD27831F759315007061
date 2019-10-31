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
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.bluetoothprinter.BluetoothPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.DaftarPelunasanDetailAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.Model.PelunasanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.PalPrinter;

public class DaftarPelunasanDetail extends AppCompatActivity {

    private CustomerModel customer;
    private double total;

    private String no_nota = "";

    private List<NotaPenjualanModel> listNota = new ArrayList<>();
    private DaftarPelunasanDetailAdapter adapter;

    private PalPrinter printerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pelunasan_detail);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Detail Nota");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Gson gson = new Gson();
        if(getIntent().hasExtra(Constant.EXTRA_ID_NOTA)){
            no_nota = getIntent().getStringExtra(Constant.EXTRA_ID_NOTA);
        }
        if(getIntent().hasExtra(Constant.EXTRA_CUSTOMER)){
            customer = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_CUSTOMER), CustomerModel.class);
        }

        RecyclerView rv_nota = findViewById(R.id.rv_nota);
        rv_nota.setItemAnimator(new DefaultItemAnimator());
        rv_nota.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DaftarPelunasanDetailAdapter(this, listNota);
        rv_nota.setAdapter(adapter);

        loadDetail();
    }

    private void loadDetail(){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format(Locale.getDefault(), "/%s?search=%s",no_nota, "");
        //Log.d(Constant.TAG, Constant.URL_DAFTAR_PELUNASAN_NOTA_DETAIL + parameter);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_PELUNASAN_NOTA_DETAIL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listNota.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject header = new JSONObject(result).getJSONObject("header");
                            total = header.getDouble("total");

                            JSONArray detail = new JSONObject(result).getJSONArray("detail");
                            for(int i = 0; i < detail.length(); i++){
                                JSONObject nota = detail.getJSONObject(i);
                                listNota.add(new NotaPenjualanModel(nota.getString("no_bukti"),
                                        new CustomerModel("", "")
                                        ,Constant.PENJUALAN_SO, nota.getDouble("jumlah_bayar"),
                                        nota.getDouble("total_piutang")));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarPelunasanDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarPelunasanDetail.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nota_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_print) {
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_pal_nota);
            printerManager = new PalPrinter(DaftarPelunasanDetail.this, Bitmap.createScaledBitmap(
                    logo, 170, 170, false));
            printerManager.startService();
            printerManager.setListener(new BluetoothPrinter.BluetoothListener() {
                @Override
                public void onBluetoothConnected() {
                    PelunasanModel pelunasan = new PelunasanModel(customer.getNama(),
                            AppSharedPreferences.getNamaRegional(DaftarPelunasanDetail.this),
                            AppSharedPreferences.getNama(DaftarPelunasanDetail.this),
                            no_nota, new Date(), total, listNota);

                    printerManager.printPelunasan(pelunasan);
                }

                @Override
                public void onBluetoothFailed(String message) {
                    Toast.makeText(DaftarPelunasanDetail.this, message, Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if(printerManager != null){
            printerManager.stopService();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
