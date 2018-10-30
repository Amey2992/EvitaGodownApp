package com.commercialMgmt.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "CommercialProductTable")
public class CommercialProductModel implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public int product_category;

    @DatabaseField
    public String product_name;

    @DatabaseField
    public int product_id;

    @DatabaseField
    public double product_rate;

    @DatabaseField
    public double bpcl_rate;

    public CommercialProductModel(){}

    public CommercialProductModel(JSONObject jsonObject){
        if(jsonObject != null){
            product_category = jsonObject.optInt("ID_PRODUCT_CATEGORY");
            product_name = jsonObject.optString("PRODUCT_NAME");
            product_id = jsonObject.optInt("PRODUCT_ID");
            product_rate = jsonObject.optDouble("rate");
            bpcl_rate=jsonObject.optDouble("BPCLRate");

        }
    }
}
