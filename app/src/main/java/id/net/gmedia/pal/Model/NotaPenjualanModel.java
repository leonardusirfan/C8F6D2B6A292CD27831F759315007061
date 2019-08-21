package id.net.gmedia.pal.Model;

public class NotaPenjualanModel extends NotaModel{

    private CustomerModel customer;
    private int type = 0;

    private double terbayar;

    public NotaPenjualanModel(String id, CustomerModel customer, int type){
        super(id);
        this.customer = customer;
        this.type = type;
    }

    public NotaPenjualanModel(String id, CustomerModel customer,
                              int type, double terbayar, double total){
        super(id, "", "", total);
        this.customer = customer;
        this.type = type;
        this.terbayar = terbayar;
    }

    public NotaPenjualanModel(String id, CustomerModel customer, int type, String tanggal, double total){
        super(id, "", tanggal, total);
        this.customer = customer;
        this.type = type;
    }

    public NotaPenjualanModel(String id, String nama, CustomerModel customer, double total){
        super(id, nama, "", total);
        this.customer = customer;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public int getType() {
        return type;
    }

    public double getTerbayar() {
        return terbayar;
    }
}
