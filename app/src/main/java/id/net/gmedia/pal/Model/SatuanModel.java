package id.net.gmedia.pal.Model;

import android.support.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        return satuan;
    }
}
