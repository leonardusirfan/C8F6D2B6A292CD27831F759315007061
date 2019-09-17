package id.net.gmedia.pal.Activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Adapter.ListGiroAdapter;
import id.net.gmedia.pal.Model.GiroModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ListGiroDetail extends AppCompatActivity{

    private String id_customer;
    private ListGiroAdapter adapter;

    private String search = "";
    private LoadMoreScrollListener loadMoreScrollListener;

    List<GiroModel> listGiro = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_giro);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Giro");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_ID_CUSTOMER)){
            id_customer = getIntent().getStringExtra(Constant.EXTRA_ID_CUSTOMER);
        }

        RecyclerView rv_giro = findViewById(R.id.rv_giro);
        rv_giro.setLayoutManager(new LinearLayoutManager(this));
        rv_giro.setItemAnimator(new DefaultItemAnimator());
        adapter = new ListGiroAdapter(listGiro);
        rv_giro.setAdapter(adapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadGiro(false);
            }
        };
        rv_giro.addOnScrollListener(loadMoreScrollListener);

        findViewById(R.id.fab_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(ListGiroDetail.this, R.layout.popup_tambah_giro,
                        85, 80);

                final EditText txt_nomor, txt_bank, txt_nominal;
                final TextView txt_tgl_kadaluarsa;
                //TextView txt_tgl_terbit;

                txt_nomor = dialog.findViewById(R.id.txt_nomor);
                txt_bank = dialog.findViewById(R.id.txt_bank);
                txt_nominal = dialog.findViewById(R.id.txt_nominal);
                //txt_tgl_terbit = dialog.findViewById(R.id.txt_tgl_terbit);
                txt_tgl_kadaluarsa = dialog.findViewById(R.id.txt_tgl_kadaluarsa);

                txt_nominal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            dialog.findViewById(R.id.layout_tgl_kadaluarsa).performClick();
                            return true;
                        }
                        return false;
                    }
                });

                /*dialog.findViewById(R.id.layout_tgl_terbit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeChooser.getInstance().selectDate(ListGiroDetail.this, new DateTimeChooser.DateTimeListener() {
                            @Override
                            public void onFinished(String dateString) {
                                txt_tgl_terbit.setText(dateString);
                            }
                        });
                    }
                });
*/
                dialog.findViewById(R.id.layout_tgl_kadaluarsa).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeChooser.getInstance().selectDate(ListGiroDetail.this, new DateTimeChooser.DateTimeListener() {
                            @Override
                            public void onFinished(String dateString) {
                                txt_tgl_kadaluarsa.setText(dateString);
                            }
                        });
                    }
                });

                dialog.findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nomor = txt_nomor.getText().toString();
                        String bank = txt_bank.getText().toString();
                        String nominal = txt_nominal.getText().toString();
                        //String tgl_terbit = txt_tgl_terbit.getText().toString();
                        String tgl_kadaluarsa = txt_tgl_kadaluarsa.getText().toString();

                        if(nomor.equals("") || bank.equals("") || nominal.equals("")||
                            //tgl_terbit.equals("") ||
                                tgl_kadaluarsa.equals("")){
                            Toast.makeText(ListGiroDetail.this, "Pastikan semua input terisi", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            JSONBuilder body = new JSONBuilder();
                            body.add("kode_pelanggan", id_customer);
                            body.add("nomor_giro", nomor);
                            body.add("bank", bank);
                            //body.add("tanggal_cair", tgl_terbit);
                            body.add("tanggal_expired", tgl_kadaluarsa);
                            body.add("total", nominal);

                            tambahGiro(dialog, body.create());
                        }
                    }
                });

                dialog.show();
            }
        });

        loadGiro(true);
    }

    private void tambahGiro(final Dialog dialog, JSONObject body){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GIRO_TAMBAH, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body, new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ListGiroDetail.this, "Berhasil menambah giro", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        loadGiro(true);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ListGiroDetail.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadGiro(final boolean init){
        final int LOAD_COUNT = 9;
        if(init){
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("kode_pelanggan", id_customer);
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("limit", LOAD_COUNT);
        body.add("search", search);

        AppLoading.getInstance().showLoading(this, R.layout.popup_loading);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GIRO_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listGiro.clear();
                            adapter.notifyDataSetChanged();
                        }

                        AppLoading.getInstance().stopLoading();
                        loadMoreScrollListener.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listGiro.clear();
                            }

                            JSONArray giro_list = new JSONObject(result).getJSONArray("giro_list");
                            for(int i = 0; i < giro_list.length(); i++){
                                JSONObject giro = giro_list.getJSONObject(i);
                                listGiro.add(new GiroModel(giro.getString("nomor_giro"), giro.getString("bank"),
                                        giro.getDouble("total"), giro.getString("tanggal_cair"), giro.getString("tanggal_expired")));
                            }

                            loadMoreScrollListener.finishLoad(giro_list.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadMoreScrollListener.finishLoad(0);
                            Toast.makeText(ListGiroDetail.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ListGiroDetail.this, message, Toast.LENGTH_SHORT).show();

                        loadMoreScrollListener.finishLoad(0);
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
                loadGiro(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadGiro(true);
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
