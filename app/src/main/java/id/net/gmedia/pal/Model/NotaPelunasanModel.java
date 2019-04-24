package id.net.gmedia.pal.Model;

public class NotaPelunasanModel extends NotaModel {

    private CustomerModel customer;

    private String gambar;
    private String cara_bayar;

    public NotaPelunasanModel(String id, String nama, String tanggal, double total,
                              String keterangan, String cara_bayar, CustomerModel customer){
        super(id, nama, tanggal, total, keterangan);
        this.cara_bayar = cara_bayar;
        this.customer = customer;
    }

    public NotaPelunasanModel(String id, String nama, String tanggal, double total,
                              String keterangan, String cara_bayar, String gambar, CustomerModel customer){
        super(id, nama, tanggal, total, keterangan);
        this.cara_bayar = cara_bayar;
        this.gambar = gambar;

        this.customer = customer;
    }

    public String getCara_bayar() {
        return cara_bayar;
    }

    public String getGambar() {
        return gambar;
    }

    public CustomerModel getCustomer() {
        return customer;
    }
}
