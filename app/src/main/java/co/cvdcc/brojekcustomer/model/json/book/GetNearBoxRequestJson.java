package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class GetNearBoxRequestJson {

    @Expose
    @SerializedName("latitude")
    public double latitude;

    @Expose
    @SerializedName("longitude")
    public double longitude;
    @Expose
    @SerializedName("kendaraan_angkut")
    public int kendaraan_angkut;


}
