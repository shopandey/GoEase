package uem.shopan.goease;

public class cardRemoteModel {

    private String remote_name;
    private String remote_id;

    private String device_name;
    private String device_id;
    private String device_key;


    // Constructor
    public cardRemoteModel(String remote_name, String remote_id, String device_name, String device_id, String device_key) {
        this.remote_name = remote_name;
        this.remote_id = remote_id;

        this.device_name = device_name;
        this.device_id = device_id;
        this.device_key = device_key;
    }

    // Getter and Setter
    public String getRemote_name() {
        return remote_name;
    }

    public void setRemote_name(String remote_name) {
        this.remote_name = remote_name;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
    }

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

}
