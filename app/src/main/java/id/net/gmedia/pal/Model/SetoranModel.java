package id.net.gmedia.pal.Model;

public class SetoranModel {
    private String tanggal;
    private String customer;
    private String nota;
    private double jumlah;
    private String pembayaran;

    private String keterangan = "";

    public SetoranModel(String tanggal, String customer, String nota, double jumlah, String pembayaran){
        this.tanggal = tanggal;
        this.customer = customer;
        this.nota = nota;
        this.jumlah = jumlah;
        this.pembayaran = pembayaran;
    }

    public SetoranModel(String tanggal, String customer, String nota, double jumlah, String pembayaran, String keterangan){
        this.tanggal = tanggal;
        this.customer = customer;
        this.nota = nota;
        this.jumlah = jumlah;
        this.pembayaran = pembayaran;
        this.keterangan = keterangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getPembayaran() {
        return pembayaran;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getCustomer() {
        return customer;
    }

    public String getNota() {
        return nota;
    }

    public String getTanggal() {
        return tanggal;
    }
}
