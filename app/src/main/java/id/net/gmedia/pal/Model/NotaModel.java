package id.net.gmedia.pal.Model;

public class NotaModel {
    private String id;
    private String nama = "";
    private String tanggal = "";
    private double total = 0;
    private String keterangan = "";

    public NotaModel(String id){
        this.id = id;
    }

    public NotaModel(String id, String nama, String tanggal, double total){
        this.id = id;
        this.nama = nama;
        this.tanggal = tanggal;
        this.total = total;
    }

    public NotaModel(String id, String nama, String tanggal, double total, String keterangan){
        this.id = id;
        this.nama = nama;
        this.tanggal = tanggal;
        this.total = total;
        this.keterangan = keterangan;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
