package com.infosolutions.adapter;

/**
 * Created by shailesh on 10/7/17.
 */

public class ChipModel {

    public ChipModel() {
    }

    public ChipModel(String chipTitle, String chipTitleId, String productCategory) {
        this.chipTitle = chipTitle;
        this.chipTitleId = chipTitleId;
        this.productCategory = productCategory;
    }

    public String getChipTitle() {
        return chipTitle;
    }

    public void setChipTitle(String chipTitle) {
        this.chipTitle = chipTitle;
    }

    String chipTitle;

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    String productCategory;

    public String getChipTitleId() {
        return chipTitleId;
    }

    public void setChipTitleId(String chipTitleId) {
        this.chipTitleId = chipTitleId;
    }

    String chipTitleId;


    @Override
    public String toString() {
        return "ChipModel{" +
                "chipTitle='" + chipTitle + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", chipTitleId='" + chipTitleId + '\'' +
                '}';
    }
}
