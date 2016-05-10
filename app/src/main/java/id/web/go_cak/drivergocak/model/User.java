package id.web.go_cak.drivergocak.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("ID")
    public String ID;

    @SerializedName("nama")
    public String nama;

    @SerializedName("no_anggota")
    public String noAnggota;

    @SerializedName("telp")
    public String telp;

    @SerializedName("foto")
    public String foto;

    @SerializedName("code")
    public String code;

    @SerializedName("result")
    public String result;
}