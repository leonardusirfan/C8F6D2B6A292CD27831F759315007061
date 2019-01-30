package id.net.gmedia.pal.Model;

public class MerchandiserModel {
    private String id;
    private String nama;
    private String alamat;
    private String foto;
    private String no_telp;
    private String keterangan;

    public MerchandiserModel(String id, String nama, String alamat, String no_telp, String foto, String keterangan){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.foto = foto;
        this.no_telp = no_telp;
        this.keterangan = keterangan;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public String getId() {
        return id;
    }

    public String getFoto() {
        return foto;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNama() {
        return nama;
    }
}

