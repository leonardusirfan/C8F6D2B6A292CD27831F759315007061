package id.net.gmedia.pal.SetoranSalesAct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.SetoranModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.DateTimeChooser;

public class SetoranSales extends AppCompatActivity {

    private String pembayaran = "";
    private String date_start = "";
    private String date_end = "";
    private String search = "";
    private boolean calendar = false;

    private TextView txt_total;
    private TextView txt_tgl_mulai, txt_tgl_selesai;
    private SlidingUpPanelLayout slidingPanel;
    private AppCompatSpinner spn_pembayaran;

    private List<SetoranModel> listSetoran = new ArrayList<>();
    private SetoranSalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setoran_sales);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Daftar Setoran");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txt_total = findViewById(R.id.txt_total);
        slidingPanel = findViewById(R.id.layout_parent);
        txt_tgl_mulai = findViewById(R.id.txt_tgl_mulai);
        txt_tgl_selesai = findViewById(R.id.txt_tgl_selesai);
        spn_pembayaran = findViewById(R.id.spn_pembayaran);
        txt_tgl_mulai.setText(Converter.DToString(new Date()));
        txt_tgl_selesai.setText(Converter.DToString(new Date()));

        findViewById(R.id.img_tgl_mulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(SetoranSales.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_mulai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.img_tgl_selesai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(SetoranSales.this, new DateTimeChooser.OnDateTimeSelected() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_tgl_selesai.setText(dateString);
                    }
                });
            }
        });

        findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_start = txt_tgl_mulai.getText().toString();
                date_end = txt_tgl_selesai.getText().toString();
                pembayaran = spn_pembayaran.getSelectedItemPosition() == 0 ? "" : spn_pembayaran.getSelectedItem().toString();
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                calendar = false;
                loadSetoran();
            }
        });

        RecyclerView rv_setoran = findViewById(R.id.rv_setoran);
        rv_setoran.setLayoutManager(new LinearLayoutManager(this));
        rv_setoran.setItemAnimator(new DefaultItemAnimator());
        adapter = new SetoranSalesAdapter(listSetoran);
        rv_setoran.setAdapter(adapter);

        loadSetoran();
    }

    private void loadSetoran(){
        AppLoading.getInstance().showLoading(this);
        String parameter = String.format(Locale.getDefault(), "?date_start=%s&date_end=%s&search=%s&cara=%s", date_start, date_end, search, pembayaran);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SETORAN_SALES + parameter, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listSetoran.clear();
                        adapter.notifyDataSetChanged();

                        String str_total = "Total : " + Converter.doubleToRupiah(0);
                        txt_total.setText(str_total);

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        listSetoran.clear();

                        try{
                            double total = 0;
                            JSONArray list_bayar = new JSONObject(result).getJSONArray("bayar_list");
                            for(int i = 0; i < list_bayar.length(); i++){
                                JSONObject obj = list_bayar.getJSONObject(i);
                                SetoranModel setoran = new SetoranModel(obj.getString("tanggal"), obj.getString("nama_pelanggan"),
                                        obj.getString("nomor_nota"), obj.getDouble("bayar"), obj.getString("cara_bayar"));
                                total += setoran.getJumlah();
                                listSetoran.add(setoran);
                            }

                            String str_total = "Total : " + Converter.doubleToRupiah(total);
                            txt_total.setText(str_total);
                        }
                        catch (JSONException e){
                            Toast.makeText(SetoranSales.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SetoranSales.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_setoran_sales, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadSetoran();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadSetoran();
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
            case R.id.action_filter:
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                calendar = true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(calendar){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            calendar = false;
        }
        else{
            super.onBackPressed();
        }
    }
}
