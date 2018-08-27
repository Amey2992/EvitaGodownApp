package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 1/9/17.
 */
@DatabaseTable(tableName = "TruckDetailTable")
public class TruckPurchaseDB  implements Serializable{

    public TruckPurchaseDB(int purchase_id, int invoice_no, String invoice_date, int vehicle_id, int pco_vehicle_no,
                           int created_by, String created_date, int product_id, int quantity, String is_sync) {
        this.purchase_id = purchase_id;
        this.invoice_no = invoice_no;
        this.invoice_date = invoice_date;
        this.vehicle_id = vehicle_id;
        this.pco_vehicle_no = pco_vehicle_no;
        this.created_by = created_by;
        this.created_date = created_date;
        this.product_id = product_id;
        this.quantity = quantity;
        this.is_sync = is_sync;
    }

    @DatabaseField(generatedId = true, columnName = "purchase_id")
    public int purchase_id;
    @DatabaseField
    public int invoice_no;
    @DatabaseField
    public String invoice_date;
    @DatabaseField
    public int vehicle_id;
    @DatabaseField
    public int pco_vehicle_no;
    @DatabaseField
    public int created_by;
    @DatabaseField
    public String created_date;
    @DatabaseField
    public int product_id;
    @DatabaseField
    public int quantity;
    @DatabaseField
    public String is_sync;


    @Override
    public String toString() {
        return "TruckPurchaseDB{" +
                "purchase_id=" + purchase_id +
                ", invoice_no=" + invoice_no +
                ", invoice_date='" + invoice_date + '\'' +
                ", vehicle_id=" + vehicle_id +
                ", pco_vehicle_no=" + pco_vehicle_no +
                ", created_by=" + created_by +
                ", created_date='" + created_date + '\'' +
                ", product_id=" + product_id +
                ", quantity=" + quantity +
                ", is_sync='" + is_sync + '\'' +
                '}';
    }
}

