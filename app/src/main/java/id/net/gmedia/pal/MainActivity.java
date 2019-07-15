package id.net.gmedia.pal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.Guideline;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.bluetoothprinter.BluetoothPrinter;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Activity.Approval.Approval;
import id.net.gmedia.pal.Activity.BlacklistCustomer;
import id.net.gmedia.pal.Activity.Customer;
import id.net.gmedia.pal.Activity.DaftarSO.DaftarSO;
import id.net.gmedia.pal.Activity.Merchandiser;
import id.net.gmedia.pal.Activity.SuratJalan;
import id.net.gmedia.pal.Activity.PenjualanSoCanvas.Penjualan;
import id.net.gmedia.pal.Activity.Piutang.Piutang;
import id.net.gmedia.pal.Activity.ReturBarang;
import id.net.gmedia.pal.Activity.Riwayat;
import id.net.gmedia.pal.Activity.SetoranSales;
import id.net.gmedia.pal.Activity.StokCanvas;
import id.net.gmedia.pal.Adapter.MainSliderAdapter;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity {

    //Variabel flag edit password
    private boolean pass_lama = true, pass_baru = true, re_pass_baru = true;

    //Variabel slider
    private List<String> listImage = new ArrayList<>();
    private Slider slider;

    //Variabel flag double click exit
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inisialisasi UI
        TextView txt_nama, txt_nip, txt_regional;
        txt_nama = findViewById(R.id.txt_nama);
        txt_nip = findViewById(R.id.txt_nip);
        txt_regional = findViewById(R.id.txt_regional);
        slider = findViewById(R.id.slider);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //Inisialisasi data user
        txt_nama.setText(AppSharedPreferences.getNama(this));
        String temp = "NIP : " + AppSharedPreferences.getId(this);
        txt_nip.setText(temp);
        temp = "Regional : " + AppSharedPreferences.getNamaRegional(this);
        txt_regional.setText(temp);

        //Inisialisasi toolbar
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        //Edit password
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

        //Menu button click
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

        findViewById(R.id.img_blacklist_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BlacklistCustomer.class));
            }
        });

        findViewById(R.id.img_retur_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReturBarang.class));
            }
        });

        findViewById(R.id.img_giro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Customer.class);
                i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_GIRO);
                startActivity(i);
            }
        });

        findViewById(R.id.img_mutasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SuratJalan.class));
            }
        });

        findViewById(R.id.img_dispensasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Customer.class);
                i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_DISPENSASI);
                startActivity(i);
            }
        });

        findViewById(R.id.img_pengajuan_penambahan_plafon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Customer.class);
                i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_PENAMBAHAN_PLAFON);
                startActivity(i);
            }
        });

        findViewById(R.id.img_daftar_pelunasan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Customer.class);
                i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_DAFTAR_PELUNASAN);
                startActivity(i);
            }
        });

        //Inisialisasi menu berdasarkan jabatan user
        if(AppSharedPreferences.getJabatan(this).equals("Manager") ||
                AppSharedPreferences.getJabatan(this).equals("Direktur") ||
                AppSharedPreferences.getPosisi(this).equals("Accounting")){
            ((Guideline)findViewById(R.id.guideline2)).setGuidelinePercent(0.15f);

            //Inisialisasi menu approval
            LinearLayout img_approval = findViewById(R.id.img_approval);
            img_approval.setVisibility(View.VISIBLE);
            img_approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Approval.class));
                }
            });
        }

        initSlider();
    }

    private void initSlider(){
        //Inisialisasi slider
        listImage.add(Converter.getURLForResource(R.class.getPackage().getName(), R.drawable.header1));
        listImage.add(Converter.getURLForResource(R.class.getPackage().getName(), R.drawable.header2));
        listImage.add(Converter.getURLForResource(R.class.getPackage().getName(), R.drawable.header3));
        listImage.add(Converter.getURLForResource(R.class.getPackage().getName(), R.drawable.header4));

        slider.setAdapter(new MainSliderAdapter(this, listImage));
        slider.setAnimateIndicators(true);
        slider.setInterval(3000);
    }

    private void resetPassword(final Dialog dialog, String pass_lama, String pass_baru, String re_pass_baru){
        //Kirim password baru ke Web Service
        AppLoading.getInstance().showLoading(this, R.layout.popup_progress_bar);

        JSONBuilder body = new JSONBuilder();
        body.add("current_password", pass_lama);
        body.add("new_password", pass_baru);
        body.add("repeat_new_password", re_pass_baru);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GANTI_PASSWORD,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
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
                //logout user
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
            new BluetoothPrinter(this).stopService();
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

    private void updateFcm(){
        JSONBuilder body = new JSONBuilder();
        body.add("fcm_id", AppSharedPreferences.getFcmId(this));

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_UPDATE_FCM,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                body.create(), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.v(Constant.TAG, "Update FCM berhasil");
                    }

                    @Override

                    public void onError(String result) {
                        Log.v(Constant.TAG, "Update FCM gagal");
                    }
                });
    }

    @Override
    protected void onResume() {
        updateFcm();
        super.onResume();
    }
}
