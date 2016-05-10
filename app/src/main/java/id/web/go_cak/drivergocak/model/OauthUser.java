package id.web.go_cak.drivergocak.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fachrifebrian on 4/24/16.
 */
public class OauthUser {
    @SerializedName("users")
    public List<User> users = new ArrayList<User>();
    @SerializedName("success")
    public Integer success;

}