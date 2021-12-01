package ru.kpfu.itis.garipov.model;

public class Response {
    public String updated_at;
    public Data data;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Response() {
    }
}
