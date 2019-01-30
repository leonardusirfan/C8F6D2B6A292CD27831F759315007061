package id.net.gmedia.pal.CustomerAct;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.LoadMoreScrollListener;

public class Customer extends AppCompatActivity {

    private String search = "";
    private int loaded = 0;
    private LoadMoreScrollListener loadMoreScrollListener;

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

        //Inisialisasi Recycler View
        RecyclerView rv_customer = findViewById(R.id.rv_customer);
        rv_customer.setItemAnimator(new DefaultItemAnimator());
        rv_customer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomerAdapter(this, listCustomer);
        rv_customer.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener(new LoadMoreScrollListener.LoadListener() {
            @Override
            public void onLoad() {
                loadCustomer(false);
            }
        });
        rv_customer.addOnScrollListener(loadMoreScrollListener);

        findViewById(R.id.fab_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Customer.this, CustomerDetail.class));
            }
        });

        loadCustomer(true);
    }

    private void loadCustomer(final boolean init){
        //Inisialisasi data customer
        AppLoading.getInstance().showLoading(this);
        if(init){
            loaded = 0;
        }

        String parameter = String.format(Locale.getDefault(), "sales?regional=%s&nip=%s&start=%d&limit=%d&search=%s",
                AppSharedPreferences.getRegional(this), AppSharedPreferences.getId(this), loaded, 10, search);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_REGIONAL + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
            @Override
            public void onEmpty(String message) {
                if(init){
                    listCustomer.clear();
                    loaded = 0;
                }
                else{
                    loadMoreScrollListener.cantLoad();
                }

                adapter.notifyDataSetChanged();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
            }

            @Override
            public void onSuccess(String result) {
                try{
                    if(init){
                        listCustomer.clear();
                        loadMoreScrollListener.canLoad();
                        loaded = 0;
                    }

                    JSONArray customer = new JSONObject(result).getJSONArray("customers");
                    for(int i = 0; i < customer.length(); i++){
                        listCustomer.add(new CustomerModel(customer.getJSONObject(i).getString("kode_pelanggan"),
                                customer.getJSONObject(i).getString("nama"),
                                customer.getJSONObject(i).getString("alamat"),
                                customer.getJSONObject(i).getString("kota"),
                                customer.getJSONObject(i).getString("status")));
                        loaded += 1;
                    }

                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    Toast.makeText(Customer.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                    e.printStackTrace();
                }

                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(Customer.this, message, Toast.LENGTH_SHORT).show();
                AppLoading.getInstance().stopLoading();
                loadMoreScrollListener.finishLoad();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
