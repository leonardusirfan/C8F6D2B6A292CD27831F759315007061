package id.net.gmedia.pal.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String ID_PREF = "nip";
    private static final String GUDANG_PREF = "kode_gudang";
    private static final String REGIONAL_PREF = "kode_regional";
    private static final String NAMA_REGIONAL_PREF = "nama_regional";
    private static final String USERNAME_PREF = "nama";
    private static final String JABATAN_PREF = "jabatan";
    private static final String POSISI_PREF = "posisi";
    private static final String FCM_PREF = "fcm_id";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static String getNama(Context context){
        return getPreferences(context).getString(USERNAME_PREF, "");
    }

    public static String getId(Context context){
        return getPreferences(context).getString(ID_PREF, "");
    }


    public static String getGudang(Context context){
        return getPreferences(context).getString(GUDANG_PREF, "");
    }

    public static String getRegional(Context context){
        return getPreferences(context).getString(REGIONAL_PREF, "");
    }

    public static String getNamaRegional(Context context) {
        return getPreferences(context).getString(NAMA_REGIONAL_PREF, "");
    }

    public static String getJabatan(Context context) {
        return getPreferences(context).getString(JABATAN_PREF, "");
    }

    public static String getPosisi(Context context) {
        return getPreferences(context).getString(POSISI_PREF, "");
    }

    public static String getFcmId(Context context) {
        return getPreferences(context).getString(FCM_PREF, "");
    }

    public static void setFcmId(Context context, String fcm){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(FCM_PREF, fcm);
        editor.apply();
    }

    public static void Login(Context context, String id, String nama, String gudang,
                             String regional, String nama_regional, String jabatan, String posisi){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, true);
        editor.putString(ID_PREF, id);
        editor.putString(USERNAME_PREF, nama);
        editor.putString(GUDANG_PREF, gudang);
        editor.putString(REGIONAL_PREF, regional);
        editor.putString(NAMA_REGIONAL_PREF, nama_regional);
        editor.putString(JABATAN_PREF, jabatan);
        editor.putString(POSISI_PREF, posisi);
        editor.apply();
    }

    public static void Logout(Context context){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, false);
        editor.putString(ID_PREF, "");
        editor.putString(USERNAME_PREF, "");
        editor.putString(GUDANG_PREF, "");
        editor.putString(REGIONAL_PREF, "");
        editor.putString(NAMA_REGIONAL_PREF, "");
        editor.putString(JABATAN_PREF, "");
        editor.putString(POSISI_PREF, "");
        editor.apply();
    }
}