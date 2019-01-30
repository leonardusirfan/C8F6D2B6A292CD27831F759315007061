package id.net.gmedia.pal.MerchandiserAct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import id.net.gmedia.pal.MainActivity;
import id.net.gmedia.pal.Model.MerchandiserModel;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.JSONBuilder;

public class MerchandiserTambah extends AppCompatActivity {

    private final int UPLOAD_MERCHANDISER = 999;
    private String id_customer = "";

    private EditText txt_nama, txt_alamat, txt_no_telepon, txt_keterangan;
    private ImageView img_merchandiser, overlay_merchandiser;
    private ProgressBar bar_merchandiser;

    private UploadModel upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandiser_tambah);

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_no_telepon = findViewById(R.id.txt_no_telepon);
        txt_keterangan = findViewById(R.id.txt_keterangan);
        img_merchandiser = findViewById(R.id.img_merchandiser);
        overlay_merchandiser = findViewById(R.id.overlay_merchandiser);
        bar_merchandiser = findViewById(R.id.bar_merchandiser);
        Button btn_simpan = findViewById(R.id.btn_simpan);

        if(getIntent().hasExtra(Constant.EXTRA_MERCHANDISER)){
            Gson gson = new Gson();
            MerchandiserModel merchandiser = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_MERCHANDISER), MerchandiserModel.class);
            txt_nama.setText(merchandiser.getNama());
            txt_alamat.setText(merchandiser.getAlamat());
            txt_no_telepon.setText(merchandiser.getNo_telp());
            txt_keterangan.setText(merchandiser.getKeterangan());
            Glide.with(MerchandiserTambah.this).load(merchandiser.getFoto()).into(img_merchandiser);

            btn_simpan.setVisibility(View.INVISIBLE);

            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle("Detail Merchandiser");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        if(getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            id_customer = getIntent().getStringExtra(Constant.EXTRA_ID_CUSTOMER);
            //Inisialisasi Toolbar
            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle("Tambah Merchandiser");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        findViewById(R.id.img_upload_merchandiser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(MerchandiserTambah.this, UPLOAD_MERCHANDISER, 1);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!upload.isUploaded()){
                    Toast.makeText(MerchandiserTambah.this, "Upload gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    simpanMerchandiser();
                }
            }
        });
    }

    private void simpanMerchandiser(){
        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", id_customer);
        body.add("nama_pembeli", txt_nama.getText().toString());
        body.add("no_telp", txt_no_telepon.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("keterangan", txt_keterangan.getText().toString());
        body.add("id_gambar", upload.getId());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISER_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(MerchandiserTambah.this, "Tambah Merchandiser berhasil", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MerchandiserTambah.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiserTambah.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case UPLOAD_MERCHANDISER:{
                if(data != null){
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));

                        img_merchandiser.setImageBitmap(bitmap);
                        img_merchandiser.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        overlay_merchandiser.setVisibility(View.VISIBLE);
                        bar_merchandiser.setVisibility(View.VISIBLE);

                        upload = new UploadModel(bitmap);
                        upload.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
                        upload(upload);
                    }
                    catch (IOException e){
                        Log.e(Constant.TAG, e.getMessage());
                    }
                }
                break;
            }
            default:super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void upload(final UploadModel upload){
        ApiVolleyManager.getInstance().addMultipartRequest(MerchandiserTambah.this, Constant.URL_MERCHANDISER_UPLOAD,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                upload.setUploaded(true);
                                upload.setId(jsonresult.getJSONObject("response").getString("id"));

                                overlay_merchandiser.setVisibility(View.INVISIBLE);
                                bar_merchandiser.setVisibility(View.INVISIBLE);
                            }
                            else{
                                Toast.makeText(MerchandiserTambah.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchandiserTambah.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(MerchandiserTambah.this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);
                    }
                });
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
