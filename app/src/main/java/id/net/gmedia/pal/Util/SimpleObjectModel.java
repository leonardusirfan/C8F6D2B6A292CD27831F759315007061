package id.net.gmedia.pal.Util;

public class SimpleObjectModel {

    private String id;
    private String value;

    public SimpleObjectModel(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
