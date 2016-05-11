package id.web.go_cak.drivergocak.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DaftarTransaksi {
    @SerializedName("transaksi")
    public List<Transaksi> transaksi = new ArrayList<>();
    @SerializedName("success")
    public Integer success;

}