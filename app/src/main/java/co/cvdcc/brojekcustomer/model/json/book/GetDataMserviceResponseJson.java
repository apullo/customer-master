package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.DataMservice;

/**
 * Created by Afif on 12/18/2016.
 */

public class GetDataMserviceResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataMservice dataMservice;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataMservice getDataMservice() {
        return dataMservice;
    }

    public void setData(DataMservice dataMservice) {
        this.dataMservice = dataMservice;
    }

}
