package id.net.gmedia.pal.Model;

import java.util.ArrayList;
import java.util.List;

public class NotaModel {
    private String id;
    private CustomerModel customer;

    private int type = 0;
    private String tanggal = "";
    private double total = 0;

    private String status;

    public NotaModel(String id, CustomerModel customer, int type){
        this.id = id;
        this.customer = customer;
        this.type = type;
    }

    public NotaModel(String id, CustomerModel customer, int type, String tanggal, double total){
        this.id = id;
        this.customer = customer;
        this.type = type;
        this.tanggal = tanggal;
        this.total = total;
    }

    public NotaModel(String id, CustomerModel customer, int type, String tanggal, double total, String status){
        this.id = id;
        this.customer = customer;
        this.type = type;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getId() {
        return id;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public double getTotal() {
        return total;
    }

    public int getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}
