package com.commercialMgmt.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "CommercialConsumerTable")
public class ConsumerModel implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public String consumer_no;

    @DatabaseField
    public String LedgerCode;

    @DatabaseField
    public String consumer_name;

    @DatabaseField
    public String mobile_no;

    @DatabaseField
    public String address;

    @DatabaseField
    public String email_id;

    @DatabaseField
    public String product_name;

    @DatabaseField
    public int discount;

    @DatabaseField
    public String pan_card;

    @DatabaseField
    public String gstin;

    @DatabaseField
    public int credit_cylinder;

    @DatabaseField
    public int user_id;

    @DatabaseField
    public int amount_credit_cylinder;

    public ConsumerModel(){}

    public ConsumerModel( int id, JSONObject jsonObject){
        if(jsonObject != null){
            this.id = id;
            consumer_no = jsonObject.optString("consumer_no");
            LedgerCode = jsonObject.optString("LedgerCode");
            consumer_name = jsonObject.optString("consumer_name");
            mobile_no = jsonObject.optString("mobile_no");
            address = jsonObject.optString("address");
            email_id = jsonObject.optString("email_id");
            product_name = jsonObject.optString("product_name");
            discount = jsonObject.optInt("discount");
            pan_card = jsonObject.optString("pan_card");
            gstin = jsonObject.optString("gstin");
            credit_cylinder = jsonObject.optInt("credit_cylinder");
            user_id = jsonObject.optInt("user_id");
            amount_credit_cylinder = jsonObject.optInt("amount_credit_cylinder");

        }
    }
}
