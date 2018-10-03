package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Truck_Detail_Table")
public class TruckDetailsDB implements Serializable {

    public TruckDetailsDB() {
    }


    public TruckDetailsDB(int truck_details_id, String invoiceNo, String invoiceDate,
                          int vehicleId, String pcoVehicleNo, String createdBy, String createdDate,
                          int idProduct, String typeOfuery, int godownId, String is_sync,
                          String mode_of_entry, String deviceId, int Quantity, int lostCylinder, String ERVNO, boolean isOneWay) {
        this.truck_details_id = truck_details_id;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.vehicleId = vehicleId;
        this.pcoVehicleNo = pcoVehicleNo;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.idProduct = idProduct;
        this.typeOfQuery = typeOfuery;
        this.godownId = godownId;
        this.is_sync = is_sync;
        this.mode_of_entry = mode_of_entry;
        this.deviceId = deviceId;
        this.Quantity = Quantity;
        this.LostCylinder = lostCylinder;
        this.ERVNO = ERVNO;
        this.isOneWay = isOneWay;
     }

    @DatabaseField(generatedId = true, columnName = "truck_details_id")
    public int truck_details_id;
    @DatabaseField
    public String invoiceNo;
    @DatabaseField
    public String invoiceDate;
    @DatabaseField
    public int vehicleId;
    @DatabaseField
    public String pcoVehicleNo;
    @DatabaseField
    public String createdBy;
    @DatabaseField
    public String createdDate;
    @DatabaseField
    public int idProduct;
    @DatabaseField
    public String typeOfQuery;
    @DatabaseField
    public int godownId;
    @DatabaseField
    public String is_sync;
    @DatabaseField
    public String mode_of_entry;
    @DatabaseField
    public String deviceId;

    @DatabaseField
    public int Quantity;

    @DatabaseField
    public int LostCylinder;

    @DatabaseField
    public int Defective;

    @DatabaseField
    public String ERVNO;

    @DatabaseField
    public boolean isOneWay;

    @DatabaseField
    public String Purchase_Code;

    @DatabaseField
    public String status;


    @Override
    public String toString() {
        return "TruckDetailsDB{" +
                "truck_details_id=" + truck_details_id +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", vehicleId=" + vehicleId +
                ", pcoVehicleNo='" + pcoVehicleNo + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", idProduct='" + idProduct + '\'' +
                ", typeOfQuery='" + typeOfQuery + '\'' +
                ", godownId=" + godownId +
                ", is_sync='" + is_sync + '\'' +
                ", mode_of_entry='" + mode_of_entry + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", Quantity='" + Quantity + '\'' +
                ", LostCylinder='" + LostCylinder + '\'' +
                '}';
    }
}
