package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Commercial_Delivery_Credit")
public class CommercialDeliveryCreditDB implements Serializable {

    public CommercialDeliveryCreditDB() {
    }

    public CommercialDeliveryCreditDB(int product_id, int delivery_id, String credit_given, String godown_id, String date_time) {
        this.product_id = product_id;
        this.delivery_id = delivery_id;
        this.credit_given = credit_given;
        this.godown_id = godown_id;
        this.date_time = date_time;
    }

    @DatabaseField(generatedId = true, columnName = "product_id")
    public int product_id;
    @DatabaseField
    public int delivery_id;
    @DatabaseField
    public String credit_given;
    @DatabaseField
    public String godown_id;
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
