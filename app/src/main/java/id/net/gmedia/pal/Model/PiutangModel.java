package id.net.gmedia.pal.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PiutangModel {
    private String id;
    private String nama;
    private double jumlah;

    private String tanggal;
    private String tanggal_tempo;
    private int tempo;
    private int type;

    private boolean selected = false;

    public PiutangModel(String id, String nama, double jumlah, String tanggal, String tanggal_tempo, int type){
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;

        this.tanggal = tanggal;
        this.tanggal_tempo = tanggal_tempo;
        this.type = type;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getJumlah() {
        return jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTanggal_tempo() {
        return tanggal_tempo;
    }
}
