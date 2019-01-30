package id.net.gmedia.pal.DaftarSoAct;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.DateTimeChooser;

public class DaftarSO extends AppCompatActivity {

    private String tanggal_mulai = "";
    private String tanggal_selesai = "";
    private String status = "";

    private TextView txt_tgl_mulai, txt_tgl_selesai;
    private Spinner spn_status;
    private SearchView searchView;

    private List<NotaModel> listNota = new ArrayList<>();
    private DaftarSOAdapter adapter;
    private ArrayAdapter<String> spinner_adapter;
    List<String> spinnerItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_so);

        //Inisialisasi UI
        txt_tgl_mulai = findViewById(R.id.txt_tgl_mulai);
        txt_tgl_selesai = findViewById(R.id.txt_tgl_selesai);
        spn_status = findViewById(R.id.spn_status);

        Calendar c = Calendar.getInstance();
        tanggal_selesai = Converter.DToString(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DATE, -7);
        tanggal_mulai = Converter.DToString(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        txt_tgl_mulai.setText(tanggal_mulai);
        txt_tgl_selesai.setText(tanggal_selesai);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar SO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.img_tgl_mulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(DaftarSO.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        tanggal_mulai = dateString;
                        txt_tgl_mulai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.img_tgl_selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(DaftarSO.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        tanggal_selesai = dateString;
                        txt_tgl_selesai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSo(searchView.getQuery().toString());
            }
        });

        RecyclerView rv_so = findViewById(R.id.rv_so);
        adapter = new DaftarSOAdapter(this, listNota);
        rv_so.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_so.setItemAnimator(new DefaultItemAnimator());
        rv_so.setAdapter(adapter);

        spinnerItem.add("Semua");
        spinner_adapter = new ArrayAdapter<>(
                DaftarSO.this, android.R.layout.simple_spinner_item, spinnerItem);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_status.setAdapter(spinner_adapter);
        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = position == 0 ? "" : spn_status.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                status = "";
            }
        });

        initStatus();
        loadSo("");
    }

    private void initStatus(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_SO_STATUS, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray status_list = new JSONObject(result).getJSONArray("status_list");
                            for(int i = 0; i < status_list.length(); i++){
                                spinnerItem.add(status_list.getString(i));
                            }
                            spinner_adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarSO.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarSO.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadSo(String search){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format(Locale.getDefault(), "?start_date=%s&end_date=%s&search=%s&status=%s",
                tanggal_mulai, tanggal_selesai, search, status);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_DAFTAR_SO + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        AppLoading.getInstance().stopLoading();
                        listNota.clear();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listNota.clear();
                            JSONArray nota_list = new JSONObject(result).getJSONArray("nota_list");
                            for(int i = 0; i < nota_list.length(); i++){
                                JSONObject nota = nota_list.getJSONObject(i);
                                listNota.add(new NotaModel(nota.getString("nomor_nota"),
                                        new CustomerModel("", nota.getString("nama_pelanggan")), Constant.PENJUALAN_SO,
                                        nota.getString("tanggal"), nota.getDouble("total"), nota.getString("status")));
                            }

                            AppLoading.getInstance().stopLoading();
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(DaftarSO.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(DaftarSO.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                loadSo(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    loadSo("");
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
