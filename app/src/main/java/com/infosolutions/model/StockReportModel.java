package com.infosolutions.model;

/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockReportModel {

    public String getStockReportTitle() {
        return stockReportTitle;
    }

    public void setStockReportTitle(String stockReportTitle) {
        this.stockReportTitle = stockReportTitle;
    }

    private String stockReportTitle;

    public StockReportModel(String stockReportTitle) {
        this.stockReportTitle = stockReportTitle;
    }

    public StockReportModel() {
    }
}
