package com.infosolutions.model;

/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockReportListModel {


    public StockReportListModel() {
    }

    public String getSV() {
        return SV;
    }

    public void setSV(String SV) {
        this.SV = SV;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getDBC() {
        return DBC;
    }

    public void setDBC(String DBC) {
        this.DBC = DBC;
    }

    public String getRETURN_FULL() {
        return RETURN_FULL;
    }

    public void setRETURN_FULL(String RETURN_FULL) {
        this.RETURN_FULL = RETURN_FULL;
    }

    public String getFULL_CYLINDER() {
        return FULL_CYLINDER;
    }

    public void setFULL_CYLINDER(String FULL_CYLINDER) {
        this.FULL_CYLINDER = FULL_CYLINDER;
    }

    public String getEMPTY() {
        return EMPTY;
    }

    public void setEMPTY(String EMPTY) {
        this.EMPTY = EMPTY;
    }

    private String SV;
    private String NAME;
    private String DBC;
    private String RETURN_FULL;
    private String FULL_CYLINDER;
    private String EMPTY;


    public StockReportListModel(String SV, String NAME, String DBC, String RETURN_FULL, String FULL_CYLINDER, String EMPTY) {
        this.SV = SV;
        this.NAME = NAME;
        this.DBC = DBC;
        this.RETURN_FULL = RETURN_FULL;
        this.FULL_CYLINDER = FULL_CYLINDER;
        this.EMPTY = EMPTY;
    }

    @Override
    public String toString() {
        return "StockReportListModel{" +
                "SV='" + SV + '\'' +
                ", NAME='" + NAME + '\'' +
                ", DBC='" + DBC + '\'' +
                ", RETURN_FULL='" + RETURN_FULL + '\'' +
                ", FULL_CYLINDER='" + FULL_CYLINDER + '\'' +
                ", EMPTY='" + EMPTY + '\'' +
                '}';
    }
}
