package com.infosolutions.database;

import org.json.JSONObject;

public class ERVModel {

    public ERVModel(){}

    public String ERV_No;

    public String PCO_Vehical_No;

    public String Vehicle_No;

    public String Product_Name;

    public String Sound_Quantity;

    public String Defective;

    public ERVModel(JSONObject jsonObject){
        if(jsonObject != null){
            this.ERV_No = jsonObject.optString("ERV_No");
            this.PCO_Vehical_No = jsonObject.optString("PCO_Vehical_No");
            this.Vehicle_No = jsonObject.optString("Vehicle_No");
            this.Product_Name = jsonObject.optString("Product_Name");
            this.Sound_Quantity = jsonObject.optString("Sound_Quantity");
            this.Defective = jsonObject.optString("Defective");
        }
    }
}
