package id.net.gmedia.pal.Util;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.CustomerModel;

public class AppKeranjangPenjualan {
    private static final AppKeranjangPenjualan ourInstance = new AppKeranjangPenjualan();

    private boolean penjualan = false;

    //Variabel penjualan
    private CustomerModel customer;
    private int JENIS_PENJUALAN;

    //variabel nota
    private List<BarangModel> listBarang = new ArrayList<>();
    private double budget_terpakai = 0;
    private int cara_bayar = 0;
    private String tempo = "";

    public static AppKeranjangPenjualan getInstance() {
        return ourInstance;
    }

    private AppKeranjangPenjualan() {

    }

    public void startPenjualan(CustomerModel customer, int JENIS_PENJUALAN){
        this.customer = customer;
        this.JENIS_PENJUALAN = JENIS_PENJUALAN;

        penjualan = true;
    }

    public void clearPenjualan(){
        customer = null;
        JENIS_PENJUALAN = 0;

        listBarang.clear();
        budget_terpakai = 0;
        cara_bayar = 0;
        tempo = "";

        penjualan = false;
    }

    public int getTotalBarangDiskon(int edited){
        int sum = 0;
        for(int i = 0; i < listBarang.size(); i++){
            if(i != edited){
                BarangModel b = listBarang.get(i);
                if(b.getDiskon() > 0){
                    sum += b.getJumlah();
                }
            }
        }
        return sum;
    }

    public void pakai_budget(double nilai){
        budget_terpakai += nilai;
    }

    public void hapus_budget(double nilai){
        budget_terpakai -= nilai;
    }

    public void edit_pakai_budget(double nilai_lama, double nilai_baru){
        hapus_budget(nilai_lama);
        pakai_budget(nilai_baru);
    }

    public boolean isBarangBelumAda(String id){
        for(BarangModel b : listBarang){
            if(b.getId().equals(id)){
                return false;
            }
        }

        return true;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public void setCara_bayar(int cara_bayar) {
        this.cara_bayar = cara_bayar;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public int getJENIS_PENJUALAN() {
        return JENIS_PENJUALAN;
    }

    public String getTempo() {
        return tempo;
    }

    public int getCara_bayar() {
        return cara_bayar;
    }

    public boolean isPenjualan() {
        return penjualan;
    }

    public double getBudget_terpakai() {
        return budget_terpakai;
    }

    public List<BarangModel> getBarang(){
        return listBarang;
    }

    public BarangModel getBarang(int i){
        return listBarang.get(i);
    }

    public void addBarang(BarangModel b){
        listBarang.add(b);
    }
}
