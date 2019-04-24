package id.net.gmedia.pal.Model;

import id.net.gmedia.pal.Util.Constant;

public class NotaSOModel extends NotaPenjualanModel {
    private String nota_pengeluaran;
    private boolean status_kirim;

    private String status;

    public NotaSOModel(String id, CustomerModel customer, String tanggal, double total, String status,
                     String nota_pengeluaran){
        super(id, customer, Constant.PENJUALAN_SO, tanggal, total);
        this.nota_pengeluaran = nota_pengeluaran;
        this.status_kirim = !nota_pengeluaran.equals("");
        this.status = status;
    }

    public String getNota_pengeluaran() {
        return nota_pengeluaran;
    }

    public boolean isStatus_kirim() {
        return status_kirim;
    }

    public String getStatus() {
        return status;
    }
}
