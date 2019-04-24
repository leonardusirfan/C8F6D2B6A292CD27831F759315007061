package id.net.gmedia.pal.Model;

public class ReturModel {
    private String kode_barang;
    private String nama_barang;
    private boolean barang_baik;
    private int jumlah;
    private String satuan;
    private String alasan;
    private String gambar;

    public ReturModel(String kode_barang, int jumlah, String satuan, String alasan, String gambar, boolean barang_baik){
        this.kode_barang = kode_barang;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.alasan = alasan;
        this.gambar = gambar;
        this.barang_baik = barang_baik;
    }

    public ReturModel(String kode_barang, String nama_barang, int jumlah, String satuan, String alasan, String gambar){
        this.kode_barang = kode_barang;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.alasan = alasan;
        this.gambar = gambar;
        this.nama_barang = nama_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public String getAlasan() {
        return alasan;
    }

    public String getGambar() {
        return gambar;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public boolean isBarang_baik() {
        return barang_baik;
    }
}
