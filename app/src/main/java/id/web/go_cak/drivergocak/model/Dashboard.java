package id.web.go_cak.drivergocak.model;

import java.io.Serializable;

public class Dashboard implements Serializable {

    private String idDashboard;
    private String titleDashboard;
    private String imageDashboard;

    public Dashboard(String idDashboard, String titleDashboard, String imageDashboard) {
        this.idDashboard = idDashboard;
        this.titleDashboard = titleDashboard;
        this.imageDashboard = imageDashboard;
    }

    public String getIdDashboard() {
        return idDashboard;
    }

    public String getTitleDashboard() {
        return titleDashboard;
    }

    public String getImageDashboard() {
        return imageDashboard;
    }
}
