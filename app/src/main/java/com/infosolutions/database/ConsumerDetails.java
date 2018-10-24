package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "ConsumerTable")
public class ConsumerDetails  implements Serializable{

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public String ConsumerNo;

    @DatabaseField
    public int ProductId;

    @DatabaseField
    public int NoOfCylinder;

    @DatabaseField
    public String ConsumerName;

    public ConsumerDetails(){}

    public ConsumerDetails(JSONObject jsonObject){

        if(jsonObject != null){
            ConsumerNo = jsonObject.optString("ConsumerNo");
            ProductId = jsonObject.optInt("ProductId");
            NoOfCylinder = jsonObject.optInt("NoOfCylinder");
            ConsumerName = jsonObject.optString("ConsumerName");
        }
    }

}
