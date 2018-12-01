package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Product_Table")
public class ProductDB implements Serializable {

    public ProductDB() {
    }

    public ProductDB(int product_id, int product_code, String product_category, String product_description, String product_unit_measurement) {
        this.product_id = product_id;
        this.product_code = product_code;
        this.product_category = product_category;
        this.product_description = product_description;
        this.product_unit_measurement = product_unit_measurement;
    }

    @DatabaseField(generatedId = true, columnName = "product_id")
    public int product_id;

    @DatabaseField
    public int product_code;
    @DatabaseField
    public String product_category;
    @DatabaseField
    public String product_description;
    @DatabaseField
    public String product_unit_measurement;

    @Override
    public String toString() {
        return "ProductDB{" +
                "product_id=" + product_id +
                ", product_code='" + product_code + '\'' +
                ", product_category='" + product_category + '\'' +
                ", product_description='" + product_description + '\'' +
                ", product_unit_measurement='" + product_unit_measurement + '\'' +
                '}';
    }
}
