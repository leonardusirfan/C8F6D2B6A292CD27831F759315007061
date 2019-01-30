package id.net.gmedia.pal.Model;

public class SatuanModel {
    private String satuan;
    private int jumlah;

    public SatuanModel(String satuan){
        this.satuan = satuan;
    }

    public SatuanModel(String satuan, int jumlah){
        this.satuan = satuan;
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public int getJumlah() {
        return jumlah;
    }
}
