package id.net.gmedia.pal.Activity.Approval;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;

public class Approval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Menu Approval");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.btn_so).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Approval.this, ApprovalSo.class));
            }
        });

        findViewById(R.id.btn_retur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Approval.this, ApprovalRetur.class));
            }
        });

        findViewById(R.id.btn_plafon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Approval.this, ApprovalPenambahanPlafon.class));
            }
        });

        //Inisialisasi menu approval untuk manager dan direktur
        if(AppSharedPreferences.getJabatan(this).equals("Manager") ||
                AppSharedPreferences.getJabatan(this).equals("Direktur")){

            findViewById(R.id.btn_login).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Approval.this, ApprovalLoginPengganti.class));
                }
            });

            findViewById(R.id.btn_po).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_po).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Approval.this, ApprovalPO.class));
                }
            });

            findViewById(R.id.btn_customer).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_customer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Approval.this, ApprovalPelanggan.class));
                }
            });

            findViewById(R.id.btn_dispensasi).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_dispensasi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Approval.this, ApprovalDispensasiPiutang.class));
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
