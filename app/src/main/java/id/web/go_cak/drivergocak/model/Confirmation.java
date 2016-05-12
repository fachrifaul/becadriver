package id.web.go_cak.drivergocak.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fachrifebrian on 5/12/16.
 */
public class Confirmation {
    @SerializedName("confirmasi")
    public Integer confirmasi;

    public Integer getConfirmasi() {
        return confirmasi;
    }
}
