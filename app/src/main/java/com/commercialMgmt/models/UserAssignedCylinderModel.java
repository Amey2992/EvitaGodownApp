package com.commercialMgmt.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "UserAssignedCylinderTable")
public class UserAssignedCylinderModel implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public int PRODUCT_ID;

    @DatabaseField
    public int Qty;

    public UserAssignedCylinderModel(){}

    public UserAssignedCylinderModel(JSONObject jsonObject){
        if(jsonObject != null){
            PRODUCT_ID = jsonObject.optInt("PRODUCT_ID");
            Qty = jsonObject.optInt("Qty");
        }
    }

}



