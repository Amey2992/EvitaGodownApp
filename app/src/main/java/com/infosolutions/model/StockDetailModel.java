package com.infosolutions.model;

import com.infosolutions.network.VolleySingleton;

import org.json.JSONObject;

/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockDetailModel {

    public String consumer_no;
    public String erv_no;
    public String empty;
    public String full_cylinder;
    public String return_full;
    public String lost;
    public String name;
    public String sv;
    public String load_full;
    public String load_empty;
    public String dbc;
    public String delivery_empty;
    public String defective;
    public String description;
    public String opening_full;
    public String opening_empty;
    public String closing_empty;
    public String closing_full;
    public String delivery_full;
    public String vehicle_no;
    public String invoice_no;
    public String received_description;
    public String received_lost;
    public String quantity;

    public StockDetailModel(String sv, String load_full, String load_empty, String dbc, String delivery_empty, String defective, String description, String opening_full, String opening_empty, String closing_empty, String closing_full, String delivery_full) {
        this.sv = sv;
        this.load_full = load_full;
        this.load_empty = load_empty;
        this.dbc = dbc;
        this.delivery_empty = delivery_empty;
        this.defective = defective;
        this.description = description;
        this.opening_full = opening_full;
        this.opening_empty = opening_empty;
        this.closing_empty = closing_empty;
        this.closing_full = closing_full;
        this.delivery_full = delivery_full;
    }

    public StockDetailModel(JSONObject jsonObject, VolleySingleton.CallType type) {
        if (jsonObject != null) {
            if(type.equals(VolleySingleton.CallType.REPORT_DOMESTIC) || type.equals(VolleySingleton.CallType.REPORT_COMMERCIAL)) {
                sv = jsonObject.optString("SV");
                name = jsonObject.optString("NAME");
                dbc = jsonObject.optString("DBC");
                defective = jsonObject.optString("DEFECTIVE");
                description = jsonObject.optString("DESCRIPTION");
                lost = jsonObject.optString("LOST");
                return_full = jsonObject.optString("RETURN_FULL");
                full_cylinder = jsonObject.optString("FULL_CYLINDER");
                empty = jsonObject.optString("EMPTY");
            }else if(type.equals(VolleySingleton.CallType.REPORT_TRUCK_RECEIVED)){
                vehicle_no = jsonObject.optString("VECHICAL_NO");
                invoice_no = jsonObject.optString("INVOICE_NO");
                description = jsonObject.optString("DESCRIPTION");
                lost = jsonObject.optString("LOST");
                quantity = jsonObject.optString("QUANTITY");
                defective = jsonObject.optString("DEFECTIVE");
            }else if(type.equals(VolleySingleton.CallType.REPORT_TRUCK_SEND)){
                vehicle_no = jsonObject.optString("VECHICAL_NO");
                erv_no = jsonObject.optString("ERV_NO");
                description = jsonObject.optString("DESCRIPTION");
                // UPDATED BY SACHIN
                defective = jsonObject.optString("DEFECTIVE");

                quantity = jsonObject.optString("QUANTITY");
            }else if(type.equals(VolleySingleton.CallType.REPORT_TV_DETAILS)){
                description = jsonObject.optString("DESCRIPTION");
                quantity = jsonObject.optString("QUANTITY");
                consumer_no = jsonObject.optString("CONSUMER_NO");
            }
        }
    }


    @Override
    public String toString() {
        return "StockDetailModel{" +
                "sv='" + sv + '\'' +
                ", load_full='" + load_full + '\'' +
                ", load_empty='" + load_empty + '\'' +
                ", dbc='" + dbc + '\'' +
                ", delivery_empty='" + delivery_empty + '\'' +
                ", defective='" + defective + '\'' +
                ", description='" + description + '\'' +
                ", opening_full='" + opening_full + '\'' +
                ", opening_empty='" + opening_empty + '\'' +
                ", closing_empty='" + closing_empty + '\'' +
                ", closing_full='" + closing_full + '\'' +
                ", delivery_full='" + delivery_full + '\'' +
                '}';
    }


    public String getSv() {
        return sv;
    }

    public void setSv(String sv) {
        this.sv = sv;
    }

    public String getLoad_full() {
        return load_full;
    }

    public void setLoad_full(String load_full) {
        this.load_full = load_full;
    }

    public String getLoad_empty() {
        return load_empty;
    }

    public void setLoad_empty(String load_empty) {
        this.load_empty = load_empty;
    }

    public String getDbc() {
        return dbc;
    }

    public void setDbc(String dbc) {
        this.dbc = dbc;
    }

    public String getDelivery_empty() {
        return delivery_empty;
    }

    public void setDelivery_empty(String delivery_empty) {
        this.delivery_empty = delivery_empty;
    }

    public String getDefective() {
        return defective;
    }

    public void setDefective(String defective) {
        this.defective = defective;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpening_full() {
        return opening_full;
    }

    public void setOpening_full(String opening_full) {
        this.opening_full = opening_full;
    }

    public String getOpening_empty() {
        return opening_empty;
    }

    public void setOpening_empty(String opening_empty) {
        this.opening_empty = opening_empty;
    }

    public String getClosing_empty() {
        return closing_empty;
    }

    public void setClosing_empty(String closing_empty) {
        this.closing_empty = closing_empty;
    }

    public String getClosing_full() {
        return closing_full;
    }

    public void setClosing_full(String closing_full) {
        this.closing_full = closing_full;
    }

    public String getDelivery_full() {
        return delivery_full;
    }

    public void setDelivery_full(String delivery_full) {
        this.delivery_full = delivery_full;
    }

    public String getName() {
        return name;
    }
}
