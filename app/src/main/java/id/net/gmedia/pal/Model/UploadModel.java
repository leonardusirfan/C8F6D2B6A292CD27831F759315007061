package id.net.gmedia.pal.Model;

import android.graphics.Bitmap;

public class UploadModel {

    private Bitmap bitmap;
    private boolean uploaded = false;
    private String id = "";
    private String url;

    public UploadModel(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
}

