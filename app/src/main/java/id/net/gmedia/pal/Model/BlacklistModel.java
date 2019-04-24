package id.net.gmedia.pal.Model;

public class BlacklistModel {
    private CustomerModel customer;
    private float tanggungan;
    private double piutang;

    public BlacklistModel(CustomerModel customer, double piutang, float tanggungan){
        this.customer = customer;
        this.piutang = piutang;
        this.tanggungan = tanggungan;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public double getPiutang() {
        return piutang;
    }

    public float getTanggungan() {
        return tanggungan;
    }
}
