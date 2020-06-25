package co.cvdcc.brojekcustomer.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradhawk on 10/13/2016.
 */

public class TopupResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<String> data = new ArrayList<>();
}
