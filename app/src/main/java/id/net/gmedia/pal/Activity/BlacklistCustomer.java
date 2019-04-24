package id.net.gmedia.pal.Activity;

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

import id.net.gmedia.pal.Adapter.BlacklistCustomerAdapter;
import id.net.gmedia.pal.Model.BlacklistModel;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class BlacklistCustomer extends AppCompatActivity {

    //Variabel data blacklist
    private List<BlacklistModel> listBlacklist = new ArrayList<>();
    private BlacklistCustomerAdapter adapter;

    //Variabel load more
    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist_customer);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Blacklist Customer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Variabel RecyclerView & Adapter
        RecyclerView rv_blacklist = findViewById(R.id.rv_blacklist);
        rv_blacklist.setLayoutManager(new LinearLayoutManager(this));
        rv_blacklist.setItemAnimator(new DefaultItemAnimator());
        adapter = new BlacklistCustomerAdapter(this, listBlacklist);
        rv_blacklist.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBlacklist(false);
            }
        };

        //Muat data blacklist
        loadBlacklist(true);
    }

    private void loadBlacklist(final boolean init){
        //Membaca data blacklist dari Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        if(init){
            loadMoreScrollListener.initLoad();
        }

        String paramteter = String.format(Locale.getDefault(), "?start=%d&limit=%d&search=%s",
                loadMoreScrollListener.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_BLACKlIST + paramteter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBlacklist.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listBlacklist.clear();
                            }

                            JSONArray customers = new JSONObject(result).getJSONArray("customers");
                            for(int i = 0; i < customers.length(); i++){
                                JSONObject customer = customers.getJSONObject(i);
                                listBlacklist.add(new BlacklistModel(new CustomerModel(customer.getString("kode_pelanggan"),
                                        customer.getString("nama_pelanggan"), customer.getString("alamat"),
                                        customer.getString("kota"), customer.getDouble("total_piutang")),
                                        customer.getDouble("total_piutang"), (float)customer.getInt("tanggungan")/100));
                            }

                            loadMoreScrollListener.finishLoad(customers.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(BlacklistCustomer.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(BlacklistCustomer.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
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
                loadBlacklist(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadBlacklist(true);
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
