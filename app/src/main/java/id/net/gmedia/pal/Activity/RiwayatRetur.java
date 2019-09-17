package id.net.gmedia.pal.Activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;

import id.net.gmedia.pal.R;

public class RiwayatRetur extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_retur);

        RecyclerView rv_riwayat = findViewById(R.id.rv_riwayat);
        rv_riwayat.setItemAnimator(new DefaultItemAnimator());
        rv_riwayat.setLayoutManager(new LinearLayoutManager(this));
        //rv_riwayat.setAdapter();

        loadRiwayat();
    }

    private void loadRiwayat(){
        AppLoading.getInstance().showLoading(this);
    }
}