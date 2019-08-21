package id.net.gmedia.pal.Activity.ReturCanvas;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.AdapterMutasiBarang;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ReturCanvas extends AppCompatActivity {

    private final int TAMBAH_BARANG = 902;

    private String keterangan = "";
    private List<BarangModel> listBarang = new ArrayList<>();
    private ImageView img_keterangan;
    private TextView lbl_keterangan;

    private AdapterMutasiBarang adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_canvas);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Retur Canvas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        img_keterangan = findViewById(R.id.img_keterangan);
        lbl_keterangan = findViewById(R.id.lbl_keterangan);

        //txt_keterangan = findViewById(R.id.txt_keterangan);
        RecyclerView rv_barang = findViewById(R.id.rv_barang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterMutasiBarang(this, listBarang, false);
        rv_barang.setAdapter(adapter);

        findViewById(R.id.btn_keterangan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(ReturCanvas.this,
                        R.layout.popup_keterangan, 90, 50);

                final EditText txt_keterangan = dialog.findViewById(R.id.txt_keterangan);
                txt_keterangan.setText(keterangan);
                dialog.findViewById(R.id.btn_selesai).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keterangan = txt_keterangan.getText().toString();
                        if(!keterangan.isEmpty()){
                            img_keterangan.setImageResource(R.drawable.noted);
                            lbl_keterangan.setVisibility(View.GONE);
                        }
                        else{
                            img_keterangan.setImageResource(R.drawable.pencil_green);
                            lbl_keterangan.setVisibility(View.VISIBLE);
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        findViewById(R.id.btn_pengajuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listBarang.size() < 1){
                    Toast.makeText(ReturCanvas.this,
                            "Input barang terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    retur();
                }
            }
        });
    }

    private void retur(){
        AppLoading.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        List<JSONObject> jsonListBarang = new ArrayList<>();
        for(BarangModel b : listBarang){
            JSONBuilder barang = new JSONBuilder();
            barang.add("kode_barang", b.getId());
            barang.add("no_batch", b.getNo_batch());
            barang.add("jumlah", b.getJumlah());
            barang.add("satuan", b.getSatuan());
            jsonListBarang.add(barang.create());
        }
        body.add("barang", new JSONArray(jsonListBarang));
        body.add("keterangan", keterangan);

        Log.d(Constant.TAG, body.create().toString());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_RETUR_CANVAS,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ReturCanvas.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ReturCanvas.this, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ReturCanvas.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void hapusBarang(int position){
        listBarang.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == TAMBAH_BARANG){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Gson gson = new Gson();
                    BarangModel b = gson.fromJson(data.getStringExtra
                            (Constant.EXTRA_BARANG), BarangModel.class);

                    for(BarangModel barang : listBarang){
                        if(barang.getId().equals(b.getId())){
                            listBarang.remove(barang);
                            break;
                        }
                    }

                    listBarang.add(b);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_tambah_barang, menu);

        MenuItem getItem = menu.findItem(R.id.action_tambah);
        if (getItem != null) {
            View layout_parent = getItem.getActionView();
            Button btn_tambah = layout_parent.findViewById(R.id.btn_tambah);
            btn_tambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(ReturCanvas.this,
                            ReturCanvasBarang.class), TAMBAH_BARANG);
                }
            });
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
