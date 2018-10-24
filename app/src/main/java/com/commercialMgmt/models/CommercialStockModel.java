package com.commercialMgmt.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "CommercialStockTable")
public class CommercialStockModel implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public int prodid;

    @DatabaseField
    public String prodname;

    @DatabaseField
    public int fullcylgivenopening;

    @DatabaseField
    public int fullcyldelivery;

    @DatabaseField
    public int emptyrecievd;

    @DatabaseField
    public int credit;

    @DatabaseField
    public int availbal;

    public CommercialStockModel(){}

    public CommercialStockModel(JSONObject jsonObject){
        if(jsonObject != null){
            prodid = jsonObject.optInt("prodid");
            prodname = jsonObject.optString("prodname");
            fullcylgivenopening = jsonObject.optInt("fullcylgivenopening");
            fullcyldelivery = jsonObject.optInt("fullcyldelivery");
            emptyrecievd = jsonObject.optInt("emptyrecievd");
            credit = jsonObject.optInt("credit");
            availbal = jsonObject.optInt("availbal");
        }
    }



}
