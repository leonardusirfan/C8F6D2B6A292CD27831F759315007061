package id.net.gmedia.pal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.CustomerAdapter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;

public class Customer extends AppCompatActivity {

    //Variabel load more
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;
    private int act_code = 0;

    //Variabel data
    private List<CustomerModel> listCustomer = new ArrayList<>();
    private CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Customer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //inisialisasi intent jika ada
        if(getIntent().hasExtra(Constant.EXTRA_ACT_CODE)){
            act_code = getIntent().getIntExtra(Constant.EXTRA_ACT_CODE, 0);
        }
        else{
            FloatingMenuButton fab_tambah = findViewById(R.id.fab_tambah);
            fab_tambah.setVisibility(View.VISIBLE);
            //on Click tambah Customer
            fab_tambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Customer.this, CustomerDetail.class));
                }
            });
        }

        //Inisialisasi Recycler View & Adapter
        RecyclerView rv_customer = findViewById(R.id.rv_customer);
        rv_customer.setItemAnimator(new DefaultItemAnimator());
        rv_customer.setLayoutManager(new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomerAdapter(this, listCustomer, act_code);
        rv_customer.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                if(act_code != Constant.ACT_DAFTAR_PELUNASAN){
                    loadCustomer(false);
                }
            }
        };
        rv_customer.addOnScrollListener(loadMoreScrollListener);

        //Muat data Customer
        if(act_code == Constant.ACT_DAFTAR_PELUNASAN){
            loadCustomerPiutang();
        }
        else{
            loadCustomer(true);
        }
    }

    private void loadCustomer(final boolean init){
        //Inisialisasi data customer dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "sales?regional=%s&nip=%s&start=%d&limit=%d&search=%s",
                AppSharedPreferences.getRegional(this), AppSharedPreferences.getId(this),
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_REGIONAL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onEmpty(String message) {
                if(init){
                    listCustomer.clear();
                    adapter.notifyDataSetChanged();
                }

                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad(0);
            }

            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listCustomer.clear();
                    }

                    JSONArray customer = new JSONObject(result).getJSONArray("customers");
                    for(int i = 0; i < customer.length(); i++){
                        listCustomer.add(new CustomerModel(customer.getJSONObject(i).getString("kode_pelanggan"),
                                customer.getJSONObject(i).getString("nama"),
                                customer.getJSONObject(i).getString("alamat"),
                                customer.getJSONObject(i).getString("kota"),
                                customer.getJSONObject(i).getString("status")));
                    }

                    loadMoreScrollListener.finishLoad(customer.length());
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    loadMoreScrollListener.finishLoad(0);
                    Toast.makeText(Customer.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                    e.printStackTrace();
                }

                AppLoading.getInstance().stopLoading();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(Customer.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad(0);
            }
        }));
    }

    private void loadCustomerPiutang(){
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        loadMoreScrollListener.initLoad();

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_PELUNASAN_PELANGGAN,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listCustomer.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listCustomer.clear();

                            JSONArray customer = new JSONObject(result).getJSONArray("pelanggan_list");
                            for(int i = 0; i < customer.length(); i++){
                                listCustomer.add(new CustomerModel(customer.
                                        getJSONObject(i).getString("kode_pelanggan"),
                                        customer.getJSONObject(i).getString("nama_pelanggan"),
                                        customer.getJSONObject(i).getString("alamat")));
                            }

                            loadMoreScrollListener.finishLoad(customer.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(Customer.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadMoreScrollListener.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(Customer.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadCustomer(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadCustomer(true);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
