package id.net.gmedia.pal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalDispensasiPiutang;
import id.net.gmedia.pal.Activity.Approval.ApprovalLoginPengganti;
import id.net.gmedia.pal.Activity.Approval.ApprovalPO;
import id.net.gmedia.pal.Activity.Approval.ApprovalPelanggan;
import id.net.gmedia.pal.Activity.Approval.ApprovalPenambahanPlafon;
import id.net.gmedia.pal.Activity.Approval.ApprovalPengajuanMutasi;
import id.net.gmedia.pal.Activity.Approval.ApprovalRetur;
import id.net.gmedia.pal.Activity.Approval.ApprovalSo;
import id.net.gmedia.pal.Activity.BlacklistCustomer;
import id.net.gmedia.pal.Activity.Customer;
import id.net.gmedia.pal.Activity.CustomerDetail;
import id.net.gmedia.pal.Activity.DaftarSO.DaftarSO;
import id.net.gmedia.pal.Activity.PengajuanMutasi.PengajuanMutasiActivity;
import id.net.gmedia.pal.Activity.ReturCanvas.ReturCanvas;
import id.net.gmedia.pal.Activity.ReturKonfirmasi;
import id.net.gmedia.pal.Activity.RiwayatSetoran;
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

    private Dialog dialogVersion;
    private String link = "";
    private String version = "";

    //Variabel slider
    private List<String> listImage = new ArrayList<>();
    private Slider slider;

    //Variabel flag double click exit
    private boolean exit = false;

    private Dialog sub_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppSharedPreferences.isLoggedIn(this)){
            handleNotif();
        }
        else{
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

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
        findViewById(R.id.img_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_customer, 90);

                View btn_data, btn_tambah, btn_blacklist;
                btn_data = sub_menu.findViewById(R.id.btn_data);
                btn_tambah = sub_menu.findViewById(R.id.btn_tambah);
                btn_blacklist = sub_menu.findViewById(R.id.btn_blacklist);

                btn_data.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, Customer.class));
                        sub_menu.dismiss();
                    }
                });

                btn_tambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, CustomerDetail.class));
                        sub_menu.dismiss();
                    }
                });

                btn_blacklist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, BlacklistCustomer.class));
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_piutang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_piutang, 90);

                View btn_piutang, btn_dispensasi, btn_plafon;
                btn_piutang = sub_menu.findViewById(R.id.btn_piutang);
                btn_dispensasi = sub_menu.findViewById(R.id.btn_dispensasi);
                btn_plafon = sub_menu.findViewById(R.id.btn_plafon);

                btn_piutang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, Piutang.class));
                        sub_menu.dismiss();
                    }
                });

                btn_dispensasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Customer.class);
                        i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_DISPENSASI);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                btn_plafon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Customer.class);
                        i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_PENAMBAHAN_PLAFON);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_penjualan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_penjualan, 90);

                View btn_so, btn_canvas;
                btn_so = sub_menu.findViewById(R.id.btn_so);
                btn_canvas = sub_menu.findViewById(R.id.btn_canvas);

                btn_so.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Penjualan.class);
                        i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_SO);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                btn_canvas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Penjualan.class);
                        i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, Constant.PENJUALAN_CANVAS);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_history, 90);

                View btn_so, btn_penjualan, btn_setoran;
                btn_so = sub_menu.findViewById(R.id.btn_so);
                btn_penjualan = sub_menu.findViewById(R.id.btn_penjualan);
                btn_setoran = sub_menu.findViewById(R.id.btn_setoran);

                btn_so.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, DaftarSO.class));
                        sub_menu.dismiss();
                    }
                });

                btn_penjualan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, Riwayat.class));
                        sub_menu.dismiss();
                    }
                });

                btn_setoran.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, RiwayatSetoran.class));
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_setoranpelunasan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_setoranpelunasan, 90);

                View btn_setoran, btn_pelunasan,  btn_giro;
                btn_setoran = sub_menu.findViewById(R.id.btn_setoran);
                btn_pelunasan = sub_menu.findViewById(R.id.btn_pelunasan);
                btn_giro = sub_menu.findViewById(R.id.btn_giro);

                btn_setoran.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, SetoranSales.class));
                        sub_menu.dismiss();
                    }
                });

                btn_pelunasan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Customer.class);
                        i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_DAFTAR_PELUNASAN);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                btn_giro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Customer.class);
                        i.putExtra(Constant.EXTRA_ACT_CODE, Constant.ACT_GIRO);
                        startActivity(i);
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_retur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_retur, 90);

                View btn_customer, btn_canvas, btn_konfirmasi;
                btn_customer = sub_menu.findViewById(R.id.btn_customer);
                btn_canvas = sub_menu.findViewById(R.id.btn_canvas);
                btn_konfirmasi = sub_menu.findViewById(R.id.btn_konfirmasi);

                btn_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ReturBarang.class));
                        sub_menu.dismiss();
                    }
                });

                btn_canvas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ReturCanvas.class));
                        sub_menu.dismiss();
                    }
                });

                btn_konfirmasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ReturKonfirmasi.class));
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_stok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsDialogShowing();
                sub_menu = DialogFactory.getInstance().
                        createDialog(MainActivity.this,
                                R.layout.popup_main_stok, 90);

                View btn_stok, btn_pengajuan;
                btn_stok = sub_menu.findViewById(R.id.btn_stok);
                btn_pengajuan = sub_menu.findViewById(R.id.btn_pengajuan);

                btn_stok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, StokCanvas.class));
                        sub_menu.dismiss();
                    }
                });

                btn_pengajuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, PengajuanMutasiActivity.class));
                        sub_menu.dismiss();
                    }
                });

                sub_menu.show();
            }
        });

        findViewById(R.id.img_suratjalan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SuratJalan.class));
            }
        });

        //Inisialisasi menu berdasarkan jabatan user
        if(AppSharedPreferences.getJabatan(this).equals("Manager") ||
                AppSharedPreferences.getJabatan(this).equals("Direktur") ||
                AppSharedPreferences.getPosisi(this).equals("Accounting") ||
                AppSharedPreferences.getJabatan(this).equals("Supervisor") ){

            //Inisialisasi menu approval
            LinearLayout img_approval = findViewById(R.id.img_approval);
            img_approval.setVisibility(View.VISIBLE);
            img_approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(MainActivity.this, Approval.class));
                    checkIsDialogShowing();
                    sub_menu = DialogFactory.getInstance().
                            createDialog(MainActivity.this,
                                    R.layout.popup_main_approval, 90);

                    View btn_customer, btn_so, btn_po, btn_retur, btn_dispensasi, btn_login, btn_plafon, btn_mutasi;
                    btn_customer = sub_menu.findViewById(R.id.btn_customer);
                    btn_so = sub_menu.findViewById(R.id.btn_so);
                    btn_po = sub_menu.findViewById(R.id.btn_po);
                    btn_retur = sub_menu.findViewById(R.id.btn_retur);
                    btn_dispensasi = sub_menu.findViewById(R.id.btn_dispensasi);
                    btn_login = sub_menu.findViewById(R.id.btn_login);
                    btn_plafon = sub_menu.findViewById(R.id.btn_plafon);
                    btn_mutasi = sub_menu.findViewById(R.id.btn_mutasi);

                    btn_so.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, ApprovalSo.class));
                            sub_menu.dismiss();
                        }
                    });

                    btn_retur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, ApprovalRetur.class));
                            sub_menu.dismiss();
                        }
                    });

                    btn_plafon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, ApprovalPenambahanPlafon.class));
                            sub_menu.dismiss();
                        }
                    });

                    if(AppSharedPreferences.getJabatan(MainActivity.this).equals("Manager") ||
                            AppSharedPreferences.getJabatan(MainActivity.this).equals("Direktur") ||
                            AppSharedPreferences.getJabatan(MainActivity.this).equals("Supervisor")) {

                        btn_customer.setVisibility(View.VISIBLE);
                        btn_po.setVisibility(View.VISIBLE);
                        btn_dispensasi.setVisibility(View.VISIBLE);
                        btn_login.setVisibility(View.VISIBLE);
                        btn_mutasi.setVisibility(View.VISIBLE);

                        btn_customer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ApprovalPelanggan.class));
                                sub_menu.dismiss();
                            }
                        });

                        btn_po.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ApprovalPO.class));
                                sub_menu.dismiss();
                            }
                        });

                        btn_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ApprovalLoginPengganti.class));
                                sub_menu.dismiss();
                            }
                        });

                        btn_dispensasi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ApprovalDispensasiPiutang.class));
                                sub_menu.dismiss();
                            }
                        });

                        btn_mutasi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, ApprovalPengajuanMutasi.class));
                                sub_menu.dismiss();
                            }
                        });
                    }

                    sub_menu.show();
                }
            });
        }

        //startActivity(new Intent(MainActivity.this, Merchandiser.class));
        initSlider();
    }

    private void handleNotif(){
        //masuk intent dari notif
        if(getIntent().hasExtra("type")){
            String type = getIntent().getStringExtra("type");
            switch (type) {
                case "customer": {
                    Intent i = new Intent(this, ApprovalPelanggan.class);
                    startActivity(i);
                    break;
                }
                case "purchase_order": {
                    Intent i = new Intent(this, ApprovalPO.class);
                    startActivity(i);
                    break;
                }
                case "sales_order": {
                    Intent i = new Intent(this, ApprovalSo.class);
                    startActivity(i);
                    break;
                }
                case "retur_jual": {
                    Intent i = new Intent(this, ApprovalRetur.class);
                    startActivity(i);
                    break;
                }
                case "request_barang_canvas" : {
                    Intent i = new Intent(this, ApprovalPengajuanMutasi.class);
                    startActivity(i);
                    break;
                }
            }
        }
        else if(getIntent().hasExtra("id_bayar_piutang")){
            startActivity(new Intent(MainActivity.this, SetoranSales.class));
        }
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
        if (item.getItemId() == R.id.action_logout) {//logout user
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
        }
        else{
            return super.onOptionsItemSelected(item);
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

    private void checkVersion(){
        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constant.TAG, e.getMessage());
        }

        if(pInfo != null){
            version = pInfo.versionName;
        }

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_VERSION, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Log.e(Constant.TAG, message);
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);
                            String latestVersion = response.getString("version");
                            link = response.getString("url");
                            boolean updateRequired = response.getString("is_required").equals("1");

                            if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                if (updateRequired) {
                                    builder.setIcon(R.mipmap.ic_launcher)
                                            .setTitle("Update")
                                            .setMessage("Versi terbaru " + latestVersion +
                                                    " telah tersedia, mohon download versi terbaru.")
                                            .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                }
                                            });
                                    dialogVersion = builder.create();
                                    dialogVersion.setCancelable(false);
                                    dialogVersion.show();
                                } else {
                                    builder.setIcon(R.mipmap.ic_launcher)
                                            .setTitle("Update")
                                            .setMessage("Versi terbaru " + latestVersion +
                                                    " telah tersedia, mohon download versi terbaru.")
                                            .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(browserIntent);
                                                }
                                            })
                                            .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    dialogVersion = builder.create();
                                    dialogVersion.show();
                                }
                            }

                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Log.e(Constant.TAG, message);
                    }
                }));
    }

    private void checkIsDialogShowing(){
        if(sub_menu != null){
            if(sub_menu.isShowing()){
                sub_menu.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        updateFcm();
        checkVersion();
        super.onResume();
    }
}
