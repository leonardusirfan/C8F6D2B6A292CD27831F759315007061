package id.net.gmedia.pal.Model;

public class PengajuanPlafonModel {
    private String id;
    private CustomerModel customer;
    private int plafon_nota;
    private double plafon_nominal;
    private String alasan;

    public PengajuanPlafonModel(String id, CustomerModel customer, int plafon_nota, double plafon_nominal, String alasan){
        this.id = id;
        this.customer = customer;
        this.plafon_nota = plafon_nota;
        this.plafon_nominal = plafon_nominal;
        this.alasan = alasan;
    }

    public String getId() {
        return id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public double getPlafon_nominal() {
        return plafon_nominal;
    }

    public int getPlafon_nota() {
        return plafon_nota;
    }

    public String getAlasan() {
        return alasan;
    }
}
