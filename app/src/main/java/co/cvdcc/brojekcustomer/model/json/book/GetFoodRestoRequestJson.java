package co.cvdcc.brojekcustomer.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Afif on 12/31/2016.
 */

public class GetFoodRestoRequestJson {

    @Expose
    @SerializedName("id_resto")
    private int idResto;

    public int getIdResto() {
        return idResto;
    }

    public void setIdResto(int idResto) {
        this.idResto = idResto;
    }
}
