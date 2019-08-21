package id.net.gmedia.pal.Activity.PenjualanSoCanvas;

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

public class Penjualan extends AppCompatActivity {

    private String search = "";
    public int JENIS_PENJUALAN;

    private LoadMoreScrollListener loadMoreScrollListener;

    private List<CustomerModel> listCustomer = new ArrayList<>();
    private CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pilih Customer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        JENIS_PENJUALAN = getIntent().getIntExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);

        RecyclerView rv_customer = findViewById(R.id.rv_customer);
        rv_customer.setItemAnimator(new DefaultItemAnimator());
        rv_customer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CustomerAdapter(this, listCustomer);
        rv_customer.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadCustomer(false);
            }
        };
        rv_customer.addOnScrollListener(loadMoreScrollListener);

        loadCustomer(true);
    }

    private void loadCustomer(final boolean init){
        //Inisialisasi data customer
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "sales?regional=%s&nip=%s&start=%d&limit=%d&search=%s",
                AppSharedPreferences.getRegional(this),AppSharedPreferences.getId(this),
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_CUSTOMER_REGIONAL + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listCustomer.clear();
                        }

                        adapter.notifyDataSetChanged();
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
                                double latitude = customer.getJSONObject(i).getString("latitude").equals("null")?0:customer.getJSONObject(i).getDouble("latitude");
                                double longitude = customer.getJSONObject(i).getString("longitude").equals("null")?0:customer.getJSONObject(i).getDouble("longitude");
                                listCustomer.add(new CustomerModel(customer.getJSONObject(i).getString("kode_pelanggan"),
                                        customer.getJSONObject(i).getString("nama"), customer.getJSONObject(i).getString("alamat"),
                                        customer.getJSONObject(i).getString("kota"), latitude, longitude,
                                        customer.getJSONObject(i).getString("status")));
                            }

                            loadMoreScrollListener.finishLoad(customer.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(Penjualan.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(Penjualan.this, message, Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
