package id.net.gmedia.pal.Util;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

public class AppImeiManager {

    public final static int PERMISSION_PHONE = 967;
    private AppCompatActivity activity;
    private IMEIListener listener;

    public AppImeiManager(AppCompatActivity activity, IMEIListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    public void getImei(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            if(Build.VERSION.SDK_INT >= 26){
                listener.onGet(manager.getImei());
            }
            else{
                listener.onGet(manager.getDeviceId());
            }
        }
        else{
            askPermission();
        }
    }

    private void askPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)){

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Izin IMEI");
            builder.setMessage("Aplikasi membutuhkan izin untuk membaca informasi dan status telepon anda untuk dapat berjalan dengan benar.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_PHONE);
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
        else{
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_PHONE);
        }
    }

    public interface IMEIListener{
        void onGet(String imei);
    }
}
