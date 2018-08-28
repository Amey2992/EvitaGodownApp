package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Commercial_Delivery_Credit")
public class CommercialDeliveryCreditDB implements Serializable {

    public CommercialDeliveryCreditDB() {
    }

    public CommercialDeliveryCreditDB(int product_id, int delivery_id, int credit_given, int godown_id, String date_time) {
        this.product_id = product_id;
        this.delivery_id = delivery_id;
        this.credit_given = credit_given;
        this.godown_id = godown_id;
        this.date_time = date_time;
    }

    public CommercialDeliveryCreditDB(JSONObject jsonObject) {
        if (jsonObject != null) {
            product_id = jsonObject.optInt("Id_Product");
            delivery_id = jsonObject.optInt("Emp_Id");
            godown_id = jsonObject.optInt("GODOWN_CODE");
            credit_given = jsonObject.optInt("Crdit");
            //date_time = jsonObject.optString("DATE_TIME");

        }
    }

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField()
    public int product_id;
    @DatabaseField
    public int delivery_id;
    @DatabaseField
    public int credit_given;
    @DatabaseField
    public int godown_id;
    @DatabaseField
    public String date_time;


    @Override
    public String toString() {
        return "CommercialDeliveryCreditDB{" +
                "product_id=" + product_id +
                ", delivery_id=" + delivery_id +
                ", credit_given='" + credit_given + '\'' +
                ", godown_id='" + godown_id + '\'' +
                ", date_time='" + date_time + '\'' +
                '}';
    }
}

