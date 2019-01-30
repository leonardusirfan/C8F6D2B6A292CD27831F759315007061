package id.net.gmedia.pal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.ApprovalPelangganAct.ApprovalPelanggan;
import id.net.gmedia.pal.ApprovalSoAct.ApprovalSo;
import id.net.gmedia.pal.CustomerAct.Customer;
import id.net.gmedia.pal.DaftarSoAct.DaftarSO;
import id.net.gmedia.pal.MerchandiserAct.Merchandiser;
import id.net.gmedia.pal.PenjualanAct.Penjualan;
import id.net.gmedia.pal.PiutangAct.Piutang;
import id.net.gmedia.pal.RiwayatAct.Riwayat;
import id.net.gmedia.pal.SetoranSalesAct.SetoranSales;
import id.net.gmedia.pal.StokCanvasAct.StokCanvas;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Util.DialogFactory;
import id.net.gmedia.pal.Util.JSONBuilder;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity {

    private boolean pass_lama = true, pass_baru = true, re_pass_baru = true;
    private List<String> listImage = new ArrayList<>();
    private Slider slider;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_nama, txt_nip, txt_regional;
        txt_nama = findViewById(R.id.txt_nama);
        txt_nip = findViewById(R.id.txt_nip);
        txt_regional = findViewById(R.id.txt_regional);
        slider = findViewById(R.id.slider);
        Toolbar toolbar = findViewById(R.id.toolbar);

        txt_nama.setText(AppSharedPreferences.getNama(this));
        String temp = "NIP : " + AppSharedPreferences.getId(this);
        txt_nip.setText(temp);
        temp = "Regional : " + AppSharedPreferences.getNamaRegional(this);
        txt_regional.setText(temp);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        findViewById(R.id.img_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = DialogFactory.getInstance().createDialog(MainActivity.this,
                        R.layout.popup_change_password, 80, 50);

                final EditText txt_password_lama, txt_password_baru, txt_re_password_baru;
                final ImageView img_password_lama, img_password_baru, img_re_password_baru;
                txt_password_lama = dialog.findViewById(R.id.txt_password_lama);
                txt_password_baru = dialog.findViewById(R.id.txt_password_baru);
                txt_re_password_baru = dialog.findViewById(R.id.txt_re_password_baru);
                img_password_lama = dialog.findViewById(R.id.img_password_lama);
                img_password_baru = dialog.findViewById(R.id.img_password_baru);
                img_re_password_baru = dialog.findViewById(R.id.img_re_password_baru);

                TextView btn_batal, btn_simpan;
                btn_batal = dialog.findViewById(R.id.btn_batal);
                btn_simpan = dialog.findViewById(R.id.btn_simpan);

                btn_batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();

                btn_simpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetPassword(dialog, txt_password_lama.getText().toString(),
                                txt_password_baru.getText().toString(),
                                txt_re_password_baru.getText().toString());
                    }
                });

                img_password_lama.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pass_lama = !pass_lama;

                        if(pass_lama){
                            img_password_lama.setImageResource(R.drawable.eye_hide);
                            txt_password_lama.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        else{
                            img_password_lama.setImageResource(R.drawable.eye);
                            txt_password_lama.setTransformationMethod(null);
                        }
                    }
                });

                img_password_baru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pass_baru = !pass_baru;

                        if(pass_baru){
                            img_password_baru.setImageResource(R.drawable.eye_hide);
                            txt_password_baru.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        else{
                            img_password_baru.setImageResource(R.drawable.eye);
                            txt_password_baru.setTransformationMethod(null);
                        }
                    }
                });

                img_re_password_baru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        re_pass_baru = !re_pass_baru;

                        if(re_pass_baru){
                            img_re_password_baru.setImageResource(R.drawable.eye_hide);
                            txt_re_password_baru.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        else{
                            img_re_password_baru.setImageResource(R.drawable.eye);
                            txt_re_password_baru.setTransformationMethod(null);
                        }
                    }
                });
            }
        });

        findViewById(R.id.img_data_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Customer.class));
            }
        });

        findViewById(R.id.img_piutang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Piutang.class));
            }
        });

        findViewById(R.id.img_penjualan_so).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Penjualan.class);
                i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);
                startActivity(i);
            }
        });

        findViewById(R.id.img_penjualan_canvas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Penjualan.class);
                i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_CANVAS);
                startActivity(i);
            }
        });

        findViewById(R.id.img_data_riwayat_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Riwayat.class));
            }
        });

        findViewById(R.id.img_daftar_so).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DaftarSO.class));
            }
        });

        findViewById(R.id.img_merchandiser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Merchandiser.class));
            }
        });

        findViewById(R.id.img_stok_canvas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StokCanvas.class));
            }
        });

        findViewById(R.id.img_setoran_sales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SetoranSales.class));
            }
        });

        if(AppSharedPreferences.getJabatan(this).equals("Manager")){
            LinearLayout img_approval_pelanggan = findViewById(R.id.img_approval_pelanggan);
            LinearLayout img_approval_so = findViewById(R.id.img_approval_so);
            img_approval_pelanggan.setVisibility(View.VISIBLE);
            img_approval_so.setVisibility(View.VISIBLE);
            img_approval_pelanggan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ApprovalPelanggan.class));
                }
            });
            img_approval_so.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ApprovalSo.class));
                }
            });
        }

        initSlider();
    }

    private void initSlider(){
        listImage.add(Converter.getURLForResource(R.drawable.header1));
        listImage.add(Converter.getURLForResource(R.drawable.header2));
        listImage.add(Converter.getURLForResource(R.drawable.header3));
        listImage.add(Converter.getURLForResource(R.drawable.header4));

        slider.setAdapter(new MainSliderAdapter(this, listImage));
        slider.setAnimateIndicators(true);
        slider.setInterval(3000);
    }

    private void resetPassword(final Dialog dialog, String pass_lama, String pass_baru, String re_pass_baru){
        AppLoading.getInstance().showLoading(this, R.layout.popup_progress_bar);

        JSONBuilder body = new JSONBuilder();
        body.add("current_password", pass_lama);
        body.add("new_password", pass_baru);
        body.add("repeat_new_password", re_pass_baru);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GANTI_PASSWORD,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(MainActivity.this, "Password berhasil diganti", Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout");
                builder.setMessage("Apakah anda yakin ingin keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        AppSharedPreferences.Logout(MainActivity.this);
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return true;
                default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(exit){
            super.onBackPressed();
        }
        else{
            exit = true;
            Toast.makeText(MainActivity.this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }
}
