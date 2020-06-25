package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.AdditionalMbox;

/**
 * Created by haris on 12/23/16.
 */

public class GetAdditionalMboxResponseJson {

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("data")
    public AdditionalMbox data;

}
