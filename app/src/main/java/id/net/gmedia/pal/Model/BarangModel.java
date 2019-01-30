package id.net.gmedia.pal.Model;

import java.util.ArrayList;
import java.util.List;

public class BarangModel {
    private String id;
    private String nama;
    private double harga = 0;

    private String kode = "";
    private int jumlah = 0;
    private double diskon = 0;
    private List<SatuanModel> listSatuan = new ArrayList<>();

    private double subtotal = 0;
    private String satuan = "";

    public BarangModel(String id, String nama){
        this.id = id;
        this.nama = nama;
    }

    public BarangModel(String id, String nama, double harga){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    public BarangModel(String id, String nama, double harga, int jumlah, String satuan, double diskon, double subtotal){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.diskon = diskon;
        this.subtotal = subtotal;
    }

    public BarangModel(String id, String kode, String nama, double harga, int jumlah, String satuan, double diskon, double subtotal){
        this.id = id;
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.diskon = diskon;
        this.subtotal = subtotal;
    }

    public BarangModel(String id, String nama, double harga, double diskon){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.diskon = diskon;
    }

    public String getKode() {
        return kode;
    }

    /*public BarangModel(String id, String nama, double harga, int stok){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }*/

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStok(){
        if(listSatuan.size() >= 1){
            return listSatuan.get(0).getJumlah();
        }
        else{
            return 0;
        }
    }

    /*public double getTotal(){
        return jumlah * harga - diskon;
    }*/

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiskon() {
        return diskon;
    }

    public void setListSatuan(List<SatuanModel> listSatuan){
        this.listSatuan = listSatuan;
    }

    public List<SatuanModel> getListSatuan() {
        return listSatuan;
    }
}
