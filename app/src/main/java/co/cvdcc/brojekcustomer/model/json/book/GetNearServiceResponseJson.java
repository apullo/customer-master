package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.cvdcc.brojekcustomer.model.Driver;

import java.util.ArrayList;

/**
 * Created by Afif on 12/21/2016.
 */

public class GetNearServiceResponseJson {

    @Expose
    @SerializedName("data")
    private ArrayList<Driver> data = new ArrayList<>();

    public ArrayList<Driver> getData() {
        return data;
    }

    public void setData(ArrayList<Driver> data) {
        this.data = data;
    }

}
