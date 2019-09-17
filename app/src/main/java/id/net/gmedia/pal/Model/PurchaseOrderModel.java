package id.net.gmedia.pal.Model;

public class PurchaseOrderModel {
    private String id;
    private String tanggal;
    private String tanggal_tempo;
    private String kode_supplier;
    private String nama_supplier;
    private double total;
    private double ppn;
    private double biaya_lain;
    private String keterangan;

    public PurchaseOrderModel(String id, String tanggal, double total, double ppn, double biaya_lain,
                              String kode_supplier, String nama_supplier, String tanggal_tempo, String keterangan){
        this.id = id;
        this.tanggal = tanggal;
        this.total = total;
        this.ppn = ppn;
        this.biaya_lain = biaya_lain;
        this.kode_supplier = kode_supplier;
        this.nama_supplier = nama_supplier;
        this.tanggal_tempo = tanggal_tempo;
        this.keterangan = keterangan;
    }

    public String getNama_supplier() {
        return nama_supplier;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getId() {
        return id;
    }

    public double getBiaya_lain() {
        return biaya_lain;
    }

    public double getPpn() {
        return ppn;
    }

    public double getTotal() {
        return total;
    }

    public String getKode_supplier() {
        return kode_supplier;
    }

    public String getTanggal_tempo() {
        return tanggal_tempo;
    }
}
