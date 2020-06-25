package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.KendaraanAngkut;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 11/22/2016.
 */

public class GetKendaraanAngkutResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<KendaraanAngkut> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<KendaraanAngkut> getData() {
        return data;
    }

    public void setData(List<KendaraanAngkut> data) {
        this.data = data;
    }
}
