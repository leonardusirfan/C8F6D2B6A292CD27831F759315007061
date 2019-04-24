package id.net.gmedia.pal.Model;

import com.leonardus.irfan.bluetoothprinter.Model.Item;

import java.util.Date;
import java.util.List;

public class PelunasanModel {
    private String outlet;
    private String sales;
    private String area;
    private String no_nota;
    private Date tgl_transaksi;
    private double tunai;
    private List<NotaPenjualanModel> listItem;

    public PelunasanModel(String outlet, String area, String sales, String no_nota,
                          Date tgl_transaksi, double tunai, List<NotaPenjualanModel> listItem){
        this.outlet = outlet;
        this.area = area;
        this.sales = sales;
        this.no_nota = no_nota;
        this.tgl_transaksi = tgl_transaksi;
        this.tunai = tunai;
        this.listItem = listItem;
    }

    public String getArea() {
        return area;
    }

    public Date getTgl_transaksi() {
        return tgl_transaksi;
    }

    public double getTunai() {
        return tunai;
    }

    public List<NotaPenjualanModel> getListItem() {
        return listItem;
    }

    public String getNo_nota() {
        return no_nota;
    }

    public String getOutlet() {
        return outlet;
    }

    public String getSales() {
        return sales;
    }
}

