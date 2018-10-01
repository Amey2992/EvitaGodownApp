package com.infosolutions.database;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;

public class PurchaseERVProduct implements Serializable {

    public String ERV_No;

    public String PCO_Vehical_No;

    public String Vehicle_No;


    public String Product_Name;

    public int Sound_Quantity;

    public int Defective;

    public int vehicleId;

    public PurchaseERVProduct(JSONObject jsonObject, String ervno, String vehicle_No, String PCO_Vehical_No,int vehicleId){
        if(jsonObject != null){
            Product_Name = jsonObject.optString("Product_Name");
            Sound_Quantity = jsonObject.optInt("Quantity");
            Defective =  jsonObject.optInt("Defective");
            this.vehicleId =  vehicleId;
            this.ERV_No =  ervno;
            this.PCO_Vehical_No = PCO_Vehical_No ;
            this.Vehicle_No =  vehicle_No;
        }
    }
}
