package co.cvdcc.brojekcustomer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bradhawk on 12/7/2016.
 */

public class Pesanan implements Serializable {
    @Expose
    @SerializedName("nama_barang")
    private String namaBarang;

    @Expose
    @SerializedName("qty")
    private int qty;

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
