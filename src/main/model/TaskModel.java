package main.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty url;
    private final SimpleStringProperty status;
    private final SimpleStringProperty result;

    public TaskModel(Integer id,String url, String status,String result) {
        this.id = new SimpleIntegerProperty(id);
        this.url = new SimpleStringProperty(url);
        this.status = new SimpleStringProperty(status);
        this.result = new SimpleStringProperty(result);
    }

    public Integer getId() {
        return this.id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
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

    public String getResult() {
        return this.result.get();
    }

    public void setResult(String result) {
        this.result.set(result);
    }


}
