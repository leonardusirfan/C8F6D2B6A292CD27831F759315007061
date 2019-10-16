package id.net.gmedia.pal.Model;

public class CustomerModel {
    private String id;
    private String nama;

    private String alamat = "";
    private String no_ktp = "";
    private String kode_area = "";
    private String npwp = "";
    private String no_hp = "";
    private String kota = "";
    private String status = "";

    private String img_ktp = "";
    private String img_customer = "";

    private double latitude = 0;
    private double longitude = 0;

    private double total_piutang;

    public CustomerModel(String id, String nama){
        this.id = id;
        this.nama = nama;
    }

    public CustomerModel(String id, String nama, String alamat, String kota, String status){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.status = status;
    }

    public CustomerModel(String id, String nama, String kode_area, String alamat, String kota, String status){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.status = status;
        this.kode_area = kode_area;
    }

    public CustomerModel(String id, String nama, String alamat, String kota, double total_piutang){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.kota = kota;
        this.total_piutang = total_piutang;
    }

    public CustomerModel(String id, String nama, String alamat){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
    }

    public CustomerModel(String id, String nama, String alamat, String kode_area){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.kode_area = kode_area;
    }

    public CustomerModel(String id, String nama, double total_piutang){
        this.id = id;
        this.nama = nama;
        this.total_piutang = total_piutang;
    }

    public CustomerModel(String id, String nama, String alamat, String kota, double latitude, double longitude, String status){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kota = kota;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getKota() {
        return kota;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getTotalPiutang(){
        return total_piutang;
    }

    public String getAlamat() {
        return alamat;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getKode_area() {
        return kode_area;
    }
}
