package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradhawk on 10/19/2016.
 */

public class RequestSendResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<Transaksi> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Transaksi> getData() {
        return data;
    }

    public void setData(List<Transaksi> data) {
        this.data = data;
    }
}
