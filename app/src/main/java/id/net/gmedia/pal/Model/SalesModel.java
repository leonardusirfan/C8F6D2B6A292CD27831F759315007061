package id.net.gmedia.pal.Model;

public class SalesModel {
    private String nip;
    private String nama;
    private String jabatan;
    private String posisi;

    public SalesModel(String nip, String nama, String jabatan, String posisi){
        this.nip = nip;
        this.nama = nama;
        this.jabatan = jabatan;
        this.posisi = posisi;
    }

    public String getNama() {
        return nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getNip() {
        return nip;
    }

    public String getPosisi() {
        return posisi;
    }
}
