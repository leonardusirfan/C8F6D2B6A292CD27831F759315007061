package id.net.gmedia.pal.Model;

public class LoginPenggantiModel {
    private String id;
    private String sales;
    private String pengganti;
    private String mulai;
    private String selesai;
    private String pengajuan;

    public LoginPenggantiModel(String id, String sales, String pengganti, String mulai, String selesai, String pengajuan){
        this.id = id;
        this.sales = sales;
        this.pengganti = pengganti;
        this.mulai = mulai;
        this.selesai = selesai;
        this.pengajuan = pengajuan;
    }

    public String getId() {
        return id;
    }

    public String getMulai() {
        return mulai;
    }

    public String getPengajuan() {
        return pengajuan;
    }

    public String getPengganti() {
        return pengganti;
    }

    public String getSales() {
        return sales;
    }

    public String getSelesai() {
        return selesai;
    }
}


