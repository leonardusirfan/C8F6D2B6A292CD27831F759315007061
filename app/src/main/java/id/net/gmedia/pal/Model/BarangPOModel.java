package id.net.gmedia.pal.Model;

public class BarangPOModel extends BarangModel {

    private double ppn = 0;

    public BarangPOModel(String id, String kode, String nama, double harga, int jumlah, String satuan, double ppn, double subtotal){
        super(id, kode, nama, harga, jumlah, satuan, 0, subtotal);
        this.ppn = ppn;
    }

    public double getPpn() {
        return ppn;
    }
}
