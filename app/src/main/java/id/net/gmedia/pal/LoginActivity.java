package id.net.gmedia.pal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Util.AppImeiManager;

import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class LoginActivity extends AppCompatActivity {

    //Variabel UI
    private EditText txt_username, txt_password;
    private ProgressBar bar;

    //Variabel manager IMEI
    private AppImeiManager manager;

    //Variabel penampung IMEI dan FCM
    private String imei = "";
    private String fcm_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inisialisasi UI
        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        bar = findViewById(R.id.bar);

        //login
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imei.equals("")){
                    Toast.makeText(LoginActivity.this, "Gagal membaca informasi IMEI", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*else if(fcm_id.equals("")){
                    Toast.makeText(LoginActivity.this, "Gagal mendapat id notifikasi", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                bar.setVisibility(View.VISIBLE);
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                List<String> listImei = new ArrayList<>();
                listImei.add(imei);
                JSONBuilder body = new JSONBuilder();
                body.add("username", username);
                body.add("password", password);
                body.add("imei", new JSONArray(listImei));
                body.add("fcm_id", fcm_id);
                //System.out.println(body.create());

                ApiVolleyManager.getInstance().addRequest(LoginActivity.this, Constant.URL_LOGIN,
                        ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(),
                        new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            bar.setVisibility(View.INVISIBLE);

                            //Menyimpan informasi user
                            AppSharedPreferences.Login(LoginActivity.this, jsonresult.getString("nip"),
                                    jsonresult.getString("nama"), jsonresult.getString("kode_gudang"),
                                    jsonresult.getString("kode_regional"), jsonresult.getString("nama_regional"),
                                    jsonresult.getString("jabatan"), jsonresult.getString("posisi"));
                        }
                        catch (JSONException e){
                            Toast.makeText(LoginActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.INVISIBLE);
                    }
                }));
            }
        });

        //Inisialisasi IMEI
        manager = new AppImeiManager(this, new AppImeiManager.IMEIListener() {
            @Override
            public void onGet(String result) {
                if(result == null){
                    Toast.makeText(LoginActivity.this, "gagal mendapatkan IMEI", Toast.LENGTH_SHORT).show();
                }
                else{
                    imei = result;
                }
            }
        });
        manager.getImei();

        //Inisialisasi FCM_ID
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().
                addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcm_id = instanceIdResult.getToken();
                        AppSharedPreferences.setFcmId(LoginActivity.this, fcm_id);
                        //System.out.println("FCM " + fcm_id);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == AppImeiManager.PERMISSION_PHONE){
            manager.getImei();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
