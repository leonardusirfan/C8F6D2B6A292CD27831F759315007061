package id.net.gmedia.pal.Model;

public class GiroModel {
    private String nomor;
    private String bank;
    private double nominal;
    private String tanggal_terbit;
    private String tanggal_kadaluarsa;

    public GiroModel(String nomor, String bank, double nominal, String tanggal_terbit, String tanggal_kadaluarsa){
        this.nomor = nomor;
        this.bank = bank;
        this.nominal = nominal;
        this.tanggal_terbit = tanggal_terbit;
        this.tanggal_kadaluarsa = tanggal_kadaluarsa;
    }

    public double getNominal() {
        return nominal;
    }

    public String getBank() {
        return bank;
    }

    public String getNomor() {
        return nomor;
    }

    public String getTanggal_kadaluarsa() {
        return tanggal_kadaluarsa;
    }

    public String getTanggal_terbit() {
        return tanggal_terbit;
    }
}
