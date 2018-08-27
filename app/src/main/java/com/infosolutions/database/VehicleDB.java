package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Vehicle_Table")
public class VehicleDB implements Serializable {

    public VehicleDB() {
    }

    public VehicleDB(int vehicle_table_id, int vehicle_id, String vehicle_number) {
        this.vehicle_table_id = vehicle_table_id;
        this.vehicle_id = vehicle_id;
        this.vehicle_number = vehicle_number;
    }

    @DatabaseField(generatedId = true, columnName = "vehicle_table_id")
    public int vehicle_table_id;
    @DatabaseField
    public int vehicle_id;
    @DatabaseField
    public String vehicle_number;

    @Override
    public String toString() {
        return "VehicleDB{" +
                "vehicle_table_id=" + vehicle_table_id +
                ", vehicle_id=" + vehicle_id +
                ", vehicle_number='" + vehicle_number + '\'' +
                '}';
    }
}
