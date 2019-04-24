package id.net.gmedia.pal.Model;

import com.leonardus.irfan.Converter;

import java.util.Date;

public class PiutangModel {
    private String id;
    private String nama;
    private double jumlah;

    private String tanggal;
    private String tanggal_tempo;
    private double terbayar;
    private int type;

    private boolean selected = false;

    public PiutangModel(String id, String nama, double jumlah, double terbayar, String tanggal, String tanggal_tempo, int type){
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.terbayar = terbayar;
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

    public int getTempo(){
        long tgl_tempo = Converter.stringDToDate(tanggal_tempo).getTime();
        long now = new Date().getTime();
        double divider = 84600000;
        double selisih = (tgl_tempo - now)/divider;
        int tempo = 0;
        if(selisih < 0){
            tempo = (int) Math.ceil((tgl_tempo - now)/divider);
        }
        else{
            tempo = (int) Math.floor((tgl_tempo - now)/divider);
        }
        return tempo;
    }

    public double getTerbayar() {
        return terbayar;
    }

    public String getNama() {
        return nama;
    }

    public double getJumlah() {
        return jumlah;
    }

    public double getPiutangSisa() {
        return jumlah - terbayar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTanggal_tempo() {
        return tanggal_tempo;
    }
}
