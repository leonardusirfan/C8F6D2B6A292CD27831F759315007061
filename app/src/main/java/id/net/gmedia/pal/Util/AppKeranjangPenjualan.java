package id.net.gmedia.pal.Util;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;

public class AppKeranjangPenjualan {
    private static final AppKeranjangPenjualan ourInstance = new AppKeranjangPenjualan();
    private List<BarangModel> listBarang = new ArrayList<>();
    private double budget_terpakai = 0;

    public static AppKeranjangPenjualan getInstance() {
        return ourInstance;
    }

    private AppKeranjangPenjualan() {
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

    public boolean isBarangAda(String id){
        for(BarangModel b : listBarang){
            if(b.getId().equals(id)){
                return true;
            }
        }
        return false;
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

    public void selesaikanTransaksi(){
        listBarang.clear();
        budget_terpakai = 0;
    }
}
