package co.cvdcc.brojekcustomer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Afif on 12/20/2016.
 */

public class DiskonMpay extends RealmObject implements Serializable {

    @SerializedName("diskon")
    @Expose
    private String diskon;
    @SerializedName("biaya_akhir")
    @Expose
    private Double biayaAkhir;

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public Double getBiayaAkhir() {
        return biayaAkhir;
    }

    public void setBiayaAkhir(Double biayaAkhir) {
        this.biayaAkhir = biayaAkhir;
    }
}
