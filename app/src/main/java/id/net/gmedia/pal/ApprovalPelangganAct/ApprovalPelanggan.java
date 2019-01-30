package id.net.gmedia.pal.ApprovalPelangganAct;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.CustomerAct.CustomerAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.DialogFactory;
import id.net.gmedia.pal.Util.JSONBuilder;

public class ApprovalPelanggan extends AppCompatActivity {

    private CustomerAdapter adapter;
    private List<CustomerModel> listCustomer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pelanggan);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Persetujuan Pelanggan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_customer = findViewById(R.id.rv_customer);
        rv_customer.setItemAnimator(new DefaultItemAnimator());
        rv_customer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomerAdapter(this, listCustomer);
        rv_customer.setAdapter(adapter);

        loadCustomer();
    }

    public void showApproval(final String id){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_approval_pelanggan, 80, 30);

        Button btn_ya, btn_batal;
        btn_ya = dialog.findViewById(R.id.btn_ya);
        btn_batal = dialog.findViewById(R.id.btn_batal);

        btn_ya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLoading.getInstance().showLoading(ApprovalPelanggan.this);
                JSONBuilder body = new JSONBuilder();
                body.add("kode_pelanggan", id);

                ApiVolleyManager.getInstance().addRequest(ApprovalPelanggan.this, Constant.URL_APPROVAL_PELANGGAN,
                        ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(ApprovalPelanggan.this)),
                        body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                            @Override
                            public void onSuccess(String result) {
                                AppLoading.getInstance().stopLoading();
                                dialog.dismiss();

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
        AppLoading.getInstance().showLoading(this);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PELANGGAN_PENDING, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listCustomer.clear();
                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {

                        try{
                            listCustomer.clear();
                            JSONArray customers = new JSONObject(result).getJSONArray("customers");
                            for(int i = 0; i < customers.length(); i++){
                                JSONObject customer = customers.getJSONObject(i);
                                listCustomer.add(new CustomerModel(customer.getString("kode_pelanggan"),
                                        customer.getString("nama"),
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
