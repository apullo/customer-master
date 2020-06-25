package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradhawk on 12/7/2016.
 */

public class RequestMartResponseJson {

    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public List<Transaksi> data = new ArrayList<>();

}
