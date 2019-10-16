package id.net.gmedia.pal.Activity.Approval;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.AdapterApprovalPelanggan;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPelanggan extends AppCompatActivity {

    //Variabel data Customer
    private AdapterApprovalPelanggan adapter;
    private List<CustomerModel> listCustomer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_customer);

        //Inisialisasi Toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Approval Customer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi Recycler View & Adapter
        RecyclerView rv_customer = findViewById(R.id.rv_customer);
        rv_customer.setItemAnimator(new DefaultItemAnimator());
        rv_customer.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterApprovalPelanggan(this, listCustomer);
        rv_customer.setAdapter(adapter);

        //muat data Customer
        loadCustomer();
    }

    public void showApproval(final String id){
        //menampilkan dialog approval
        final Dialog dialog = DialogFactory.getInstance().
                createDialog(this, R.layout.popup_approval, 80, 30);

        ((TextView)dialog.findViewById(R.id.txt_pesan)).setText(R.string.approval_customer);
        Button btn_ya, btn_batal;
        btn_ya = dialog.findViewById(R.id.btn_ya);
        btn_batal = dialog.findViewById(R.id.btn_batal);

        btn_ya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Proses approval pelanggan
                AppLoading.getInstance().showLoading(ApprovalPelanggan.this);
                JSONBuilder body = new JSONBuilder();
                body.add("kode_pelanggan", id);

                ApiVolleyManager.getInstance().addRequest(ApprovalPelanggan.this,
                        Constant.URL_APPROVAL_PELANGGAN,
                        ApiVolleyManager.METHOD_POST,
                        Constant.getTokenHeader(AppSharedPreferences.getId(ApprovalPelanggan.this)),
                        body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                            @Override
                            public void onSuccess(String result) {
                                AppLoading.getInstance().stopLoading();
                                dialog.dismiss();

                                //muat ulang data Customer
                                loadCustomer();
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(ApprovalPelanggan.this, message, Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                                AppLoading.getInstance().stopLoading();
                            }
                        }));
            }
        });

        btn_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void loadCustomer(){
        //Memuat data Customer dari Web Service
        AppLoading.getInstance().showLoading(this);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PELANGGAN_PENDING,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listCustomer.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(ApprovalPelanggan.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {

                        try{
                            listCustomer.clear();
                            JSONArray customers = new JSONObject(result).getJSONArray("customers");
                            for(int i = 0; i < customers.length(); i++){
                                JSONObject customer = customers.getJSONObject(i);
                                listCustomer.add(new CustomerModel(customer.getString("kode_pelanggan"),
                                        customer.getString("nama"), customer.getString("kodearea"),
                                        customer.getString("alamat"), customer.getString("kota"),
                                        "belum terverifikasi"));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(ApprovalPelanggan.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ApprovalPelanggan.this, message, Toast.LENGTH_SHORT).show();
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
