package id.net.gmedia.pal.Model;

public class DispensasiPiutangModel {

    private String id;
    private CustomerModel customer;
    private double total;
    private String keterangan;

    public DispensasiPiutangModel(String id, CustomerModel customer, double total, String keterangan){
        this.id = id;
        this.customer = customer;
        this.total = total;
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public double getTotal() {
        return total;
    }

    public CustomerModel getCustomer() {
        return customer;
    }
}
