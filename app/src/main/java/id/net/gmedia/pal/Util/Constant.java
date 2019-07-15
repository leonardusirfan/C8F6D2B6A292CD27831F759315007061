package id.net.gmedia.pal.Util;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Constant {
    public final static Map<String, String> HEADER_AUTH = new HashMap<String, String>(){
        {
            put("Auth-Key", "frontend_client");
            put("Client-Service", "Gmedia_PAL");
        }
    };

    public final static String TAG = "pal_log";

    //EXTRA
    public final static String EXTRA_CUSTOMER = "customer";
    public final static String EXTRA_ID_CUSTOMER = "id_customer";
    public final static String EXTRA_NAMA_CUSTOMER = "nama_customer";
    public final static String EXTRA_BARANG = "barang";
    public final static String EXTRA_BARANG_PO = "barang_po";
    public final static String EXTRA_NOTA = "nota";
    public final static String EXTRA_NO_NOTA = "no_bukti";
    public final static String EXTRA_ID_NOTA = "id_bukti";
    public final static String EXTRA_JENIS_PENJUALAN = "jenis_penjualan";
    public final static String EXTRA_TYPE_NOTA = "type";
    public final static String EXTRA_LOKASI_USER = "lokasi_user";
    public final static String EXTRA_LOKASI_OUTLET = "lokasi_outlet";
    public final static String EXTRA_EDIT = "edit";
    public final static String EXTRA_MERCHANDISER = "merchandiser";
    public final static String EXTRA_ID_PENGAJUAN_PLAFON = "id_plafon";
    public final static String EXTRA_PENGAJUAN_PLAFON = "plafon";

    /*public final static String EXTRA_CARA_BAYAR = "cara_bayar";
    public final static String EXTRA_TEMPO = "tempo";*/

    public final static String EXTRA_ACT_CODE = "act_code";

    public final static int ACT_DISPENSASI = 100;
    public final static int ACT_GIRO = 102;
    public final static int ACT_MERCHANDISER = 104;
    public final static int ACT_PENAMBAHAN_PLAFON = 105;
    public final static int ACT_DAFTAR_PELUNASAN = 106;

    public final static int PENJUALAN_SO = 123;
    public final static int PENJUALAN_CANVAS = 321;

    //URL
    private final static String BASE_URL = "https://gmedia.bz/pal/";

    public final static String URL_LOGIN = BASE_URL + "api/authentication/login";
    public final static String URL_CUSTOMER_REGIONAL = BASE_URL + "api/customer/";
    public final static String URL_CUSTOMER_DETAIL = BASE_URL + "api/customer/view/";
    public final static String URL_CUSTOMER_TAMBAH = BASE_URL + "api/customer/store";
    public final static String URL_PENJUALAN_BARANG_SO = BASE_URL + "api/penjualan/barang_gudang_canvas";
    public final static String URL_PENJUALAN_BARANG_CANVAS = BASE_URL + "api/penjualan/barang_canvas";
    public final static String URL_UPLOAD_KTP = BASE_URL + "api/customer/upload_image_ktp";
    public final static String URL_UPLOAD_OUTLET = BASE_URL + "api/customer/upload_image_location";
    public final static String URL_CHECKOUT_SO = BASE_URL + "api/penjualan/sales_order";
    public final static String URL_CHECKOUT_CANVAS = BASE_URL + "api/penjualan/jual_canvas";
    public final static String URL_RIWAYAT = BASE_URL + "api/penjualan/riwayat";
    public final static String URL_PIUTANG = BASE_URL + "api/piutang/sales";
    public final static String URL_PIUTANG_CUSTOMER = BASE_URL + "api/piutang/customer/";
    public final static String URL_PIUTANG_NOTA = BASE_URL + "api/piutang/nota/";
    public final static String URL_GANTI_PASSWORD = BASE_URL + "api/authentication/change_password";
    public final static String URL_DAFTAR_SO = BASE_URL + "api/penjualan/list_sales_order";
    public final static String URL_DAFTAR_SO_STATUS = BASE_URL + "api/master/status_so";
    public final static String URL_DAFTAR_SO_DETAIL = BASE_URL + "api/penjualan/view_sales_order/";
    public final static String URL_MASTER_AKUN = BASE_URL + "api/master/akun";
    public final static String URL_PIUTANG_PELUNASAN = BASE_URL + "api/piutang/pembayaran";
    public final static String URL_MERCHANDISER_TAMBAH = BASE_URL + "api/merchandise/store";
    public final static String URL_MERCHANDISER_UPLOAD = BASE_URL + "api/merchandise/upload_image";
    public final static String URL_SO_CARI_BARCODE = BASE_URL + "api/master/scan_barang_gudang_canvas";
    public final static String URL_CANVAS_CARI_BARCODE = BASE_URL + "api/master/scan_barang_canvas";
    public final static String URL_BUDGET_DISKON = BASE_URL + "api/sales/budget_diskon";
    public final static String URL_MERCHANDISER_LIST = BASE_URL + "api/merchandise";
    public final static String URL_TOTAL_BARANG = BASE_URL + "api/penjualan/hitung_total_barang";
    public final static String URL_SO_EDIT = BASE_URL + "api/penjualan/update_barang_so";
    public final static String URL_SO_HAPUS = BASE_URL + "api/penjualan/delete_barang_so/";
    public final static String URL_SETORAN_SALES = BASE_URL + "api/sales/daftar_bayar_piutang";
    public final static String URL_APPROVAL_PELANGGAN = BASE_URL + "api/customer/approve_pelanggan";
    public final static String URL_PELANGGAN_PENDING = BASE_URL + "api/customer/daftar_pending";
    public final static String URL_SO_PENDING = BASE_URL + "api/penjualan/list_approval_so";
    public final static String URL_APPROVAL_SO = BASE_URL + "api/penjualan/update_approval_so";
    public final static String URL_BLACKlIST = BASE_URL + "api/customer/daftar_blacklist";
    public final static String URL_UPLOAD_FOTO_BUKTI = BASE_URL + "api/piutang/upload_image_bukti";
    public final static String URL_UPLOAD_BUKTI = BASE_URL + "api/piutang/tambah_bukti_transfer";
    public final static String URL_UPLOAD_FOTO_RETUR = BASE_URL + "api/penjualan/upload_image_barang_retur";
    public final static String URL_UPLOAD_RETUR = BASE_URL + "api/penjualan/request_retur_jual";
    public final static String URL_UPLOAD_FOTO_PELUNASAN = BASE_URL + "api/piutang/upload_bukti_transfer_pelanggan";
    public final static String URL_LIST_RETUR = BASE_URL + "api/penjualan/list_retur_jual";
    public final static String URL_LIST_SALES_BY_HELPER = BASE_URL + "api/helper/sales";
    public final static String URL_RETUR_DETAIL = BASE_URL + "api/penjualan/view_retur_jual";
    public final static String URL_APPROVAL_RETUR = BASE_URL + "api/penjualan/approve_request_retur";
    public final static String URL_MASTER_PROVINSI = BASE_URL + "api/master/provinsi";
    public final static String URL_MASTER_KOTA = BASE_URL + "api/master/kota";
    public final static String URL_PO_LIST = BASE_URL + "api/purchase/list_request_purchase_order";
    public final static String URL_PO_DETAIL = BASE_URL + "api/purchase/view_purchase_order";
    public final static String URL_PO_HAPUS = BASE_URL + "api/purchase/delete_barang_purchase_order";
    public final static String URL_PO_EDIT = BASE_URL + "api/purchase/update_barang_purchase_order";
    public final static String URL_MASTER_APPROVAL = BASE_URL + "api/master/kode_approval/purchase_order";
    public final static String URL_PO_APPROVE = BASE_URL + "api/purchase/approval_request_purchase_order";
    public final static String URL_RETUR_CUSTOMER_CREDIT = BASE_URL + "api/customer/credit_note";
    public final static String URL_UPDATE_FCM = BASE_URL + "api/authentication/update_fcm_id";
    public final static String URL_GIRO_LIST = BASE_URL + "api/giro/customer";
    public final static String URL_GIRO_TAMBAH = BASE_URL + "api/giro/store";
    public final static String URL_SALES_PENGGANTI_LIST = BASE_URL + "api/sales/list_request_pengganti";
    public final static String URL_SALES_PENGGANTI_APPROVAL = BASE_URL + "api/sales/approval_request_pengganti";
    public final static String URL_PLAFON_REQUEST = BASE_URL + "api/plafon/request_penambahan_plafon";
    public final static String URL_PLAFON_LIST = BASE_URL + "api/plafon/list_penambahan_plafon";
    public final static String URL_PLAFON_DETAIL = BASE_URL + "api/plafon/view_penambahan_plafon";
    public final static String URL_PLAFON_EDIT = BASE_URL + "api/plafon/update_penambahan_plafon";
    public final static String URL_PLAFON_APPROVE = BASE_URL + "api/plafon/approve_penambahan_plafon";
    public final static String URL_SURAT_JALAN_LIST = BASE_URL + "api/sales/list_surat_jalan";
    public final static String URL_SURAT_JALAN_DETAIL = BASE_URL + "api/sales/view_surat_jalan";
    public final static String URL_DISPENSASI_LIST = BASE_URL + "api/piutang/list_dispensasi_piutang";
    public final static String URL_DISPENSASI_APPROVE = BASE_URL + "api/piutang/approval_dispensasi_piutang";
    public final static String URL_DISPENSASI_REQUEST = BASE_URL + "api/piutang/request_dispensasi_piutang";
    public final static String URL_CUSTOMER_REKAP = BASE_URL + "api/customer/rekap_jual_bayar";
    public final static String URL_SURAT_JALAN_DOWNLOAD = BASE_URL + "gudang/Mutasi_sales/cetak_surat_jalan";
    public final static String URL_DAFTAR_PELUNASAN_PELANGGAN = BASE_URL + "api/piutang/list_pelanggan_sales";
    public final static String URL_DAFTAR_PELUNASAN_NOTA = BASE_URL + "api/piutang/pelunasan_pelanggan";
    public final static String URL_DAFTAR_PELUNASAN_NOTA_DETAIL = BASE_URL + "api/piutang/nota_pelunasan_piutang";

    //Token heaader dengan enkripsi
    public static Map<String, String> getTokenHeader(String id){
        Map<String, String> header = new HashMap<>();
        header.put("Auth-key", "frontend_client");
        header.put("Client-Service", "Gmedia_PAL");
        header.put("User-id", id);

        String timestamp =  new SimpleDateFormat("SSSHHyyyyssMMddmm", Locale.getDefault()).format(new Date());
        String signature = sha256(id+"&"+timestamp,id+"die");

        /*System.out.println("UUID : " + uuid);
        System.out.println("Timestamp : " + timestamp);
        System.out.println("Signature : " + signature);*/

        header.put("Timestamp", timestamp);
        header.put("Signature", signature);
        return header;
    }

    private static String sha256(String message, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            return Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Log.w("SHA256", "Return string kosong");
        return "";
    }
}
