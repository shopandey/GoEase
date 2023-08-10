package uem.shopan.goease;

public class cardModel {

    private String device_name;
    private String device_id;
    private String device_key;
    private int device_icon;

    // Constructor
    public cardModel(String device_name, String device_id, String device_key, int device_icon) {
        this.device_name = device_name;
        this.device_id = device_id;
        this.device_key = device_key;
        this.device_icon = device_icon;
    }

    // Getter and Setter
    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public int getDevice_icon() {
        return device_icon;
    }

    public void setDevice_icon(int course_image) {
        this.device_icon = device_icon;
    }

}
