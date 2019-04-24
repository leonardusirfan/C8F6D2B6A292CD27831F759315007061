package id.net.gmedia.pal.Activity.Approval;

import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.BarangPOModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPOEdit extends AppCompatActivity {

    //Variabel global barang PO, no nota
    private String no_nota = "";
    private BarangPOModel barang_po;

    //Variabel UI
    private EditText txt_jumlah;
    private AppCompatSpinner spn_satuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_poedit);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Edit PO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi data global no nota, barang
        Gson gson = new Gson();
        no_nota = getIntent().getStringExtra(Constant.EXTRA_NO_NOTA);
        if(getIntent().hasExtra(Constant.EXTRA_BARANG_PO)){
            barang_po = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_BARANG_PO), BarangPOModel.class);
        }

        //Inisialisasi UI
        TextView txt_nama_barang, txt_harga_satuan;
        txt_nama_barang = findViewById(R.id.txt_nama_barang);
        txt_harga_satuan = findViewById(R.id.txt_harga_satuan);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        spn_satuan = findViewById(R.id.spn_satuan);
        txt_nama_barang.setText(barang_po.getNama());
        txt_harga_satuan.setText(Converter.doubleToRupiah(barang_po.getHarga()));

        //button simpan hasil edit barang
        findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cek validasi
                if(txt_jumlah.getText().toString().equals("")){
                    Toast.makeText(ApprovalPOEdit.this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    //kirim hasil edit PO
                    editPO();
                }
            }
        });

        initSatuan();
    }

    private void initSatuan(){
        //Inisialisasi satuan barang
        List<String> spinnerItem = new ArrayList<>();
        for(SatuanModel s : barang_po.getListSatuan()){
            spinnerItem.add(s.getSatuan());
        }

        ArrayAdapter<String> satuan_adapter = new ArrayAdapter<>(
                ApprovalPOEdit.this, android.R.layout.simple_spinner_item, spinnerItem);

        satuan_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_satuan.setAdapter(satuan_adapter);
    }

    private void editPO(){
        //Kirim hasil edit PO Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        JSONBuilder body = new JSONBuilder();
        body.add("id_detail", barang_po.getId());
        body.add("nomor_nota", no_nota);
        body.add("kode_barang", barang_po.getKode());
        body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        body.add("satuan", spn_satuan.getSelectedItem().toString());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PO_EDIT, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ApprovalPOEdit.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(ApprovalPOEdit.this, result, Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent(ApprovalPOEdit.this, ApprovalPO.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ApprovalPOEdit.this);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPOEdit.this, message, Toast.LENGTH_SHORT).show();
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
