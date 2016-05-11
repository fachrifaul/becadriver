package id.web.go_cak.drivergocak.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaksi implements Serializable{

    @SerializedName("ID")
    public String ID;
    @SerializedName("userName")
    public String userName;
    @SerializedName("distance")
    public String distance;
    @SerializedName("driver")
    public String driver;
    @SerializedName("drivername")
    public String drivername;
    @SerializedName("driverkonfirmasi")
    public String driverkonfirmasi;
    @SerializedName("driverdone")
    public String driverdone;
    @SerializedName("jarak")
    public String jarak;
    @SerializedName("lastUpdate")
    public String lastUpdate;
    @SerializedName("ongkos")
    public String ongkos;
    @SerializedName("LatJemput")
    public String LatJemput;
    @SerializedName("LongJemput")
    public String LongJemput;
    @SerializedName("LatTujuan")
    public String LatTujuan;
    @SerializedName("LongTujuan")
    public String LongTujuan;
    @SerializedName("AlamatLengkap")
    public String AlamatLengkap;
    @SerializedName("PenyewaID")
    public String PenyewaID;
    @SerializedName("nama")
    public String nama;
    @SerializedName("telp")
    public String telp;
    @SerializedName("email")
    public String email;
    @SerializedName("code")
    public String code;
    @SerializedName("result")
    public String result;

    public String getID() {
        return ID;
    }

    public String getUserName() {
        return userName;
    }

    public String getDistance() {
        return distance;
    }

    public String getDriver() {
        return driver;
    }

    public String getDrivername() {
        return drivername;
    }

    public String getDriverkonfirmasi() {
        return driverkonfirmasi;
    }

    public String getDriverdone() {
        return driverdone;
    }

    public String getJarak() {
        return jarak;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getOngkos() {
        return ongkos;
    }

    public String getLatJemput() {
        return LatJemput;
    }

    public String getLongJemput() {
        return LongJemput;
    }

    public String getLatTujuan() {
        return LatTujuan;
    }

    public String getLongTujuan() {
        return LongTujuan;
    }

    public String getAlamatLengkap() {
        return AlamatLengkap;
    }

    public String getPenyewaID() {
        return PenyewaID;
    }

    public String getNama() {
        return nama;
    }

    public String getTelp() {
        return telp;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    public String getResult() {
        return result;
    }
}