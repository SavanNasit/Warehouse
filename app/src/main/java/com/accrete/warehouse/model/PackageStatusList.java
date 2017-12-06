package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/4/17.
 */

public class PackageStatusList {
    private String narration;

    public String getNarration() {
        return narration;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public String getDate() {
        return date;
    }

    private String packageStatus;
    private String date;

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
