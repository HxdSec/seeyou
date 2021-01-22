package main.model;

import javafx.beans.property.SimpleStringProperty;

public class ItemModel {

    private final SimpleStringProperty url;
    private final SimpleStringProperty status;

    public ItemModel(String url, String status) {
        this.url = new SimpleStringProperty(url);
        this.status = new SimpleStringProperty(status);
    }

    public String getUrl() {
        return this.url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public String getStatus() {
        return this.status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }


}
