package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Truck_send_Detail_Table")
public class TruckSendDetailsDB implements Serializable {

    public TruckSendDetailsDB() {
    }


    public TruckSendDetailsDB(int truck_details_send_id, int truck_send_id, String ervNo, int idProduct,
                              int vehicleId, String pcoVehicleNo, int createdBy, String deviceId,
                              String typeOfQuery,int godown_Id, String send_date, String is_sync, String mode_of_entry, int quantity, int defective) {

        this.truck_details_send_id = truck_details_send_id;
        this.truck_send_id = truck_send_id;
        this.ervNo = ervNo;
        this.idProduct = idProduct;
        this.vehicleId = vehicleId;
        this.pcoVehicleNo = pcoVehicleNo;
        this.createdBy = createdBy;
        this.deviceId = deviceId;
        this.typeOfQuery = typeOfQuery;
        this.godown_Id =godown_Id;
        this.send_date = send_date;
        this.is_sync = is_sync;
        this.mode_of_entry = mode_of_entry;
        this.Quantity = quantity;
        this.Defective = defective;

    }

    @DatabaseField(generatedId = true, columnName = "truck_details_send_id")
    public int truck_details_send_id;
    @DatabaseField
    public int truck_send_id;
    @DatabaseField
    public String ervNo;
    @DatabaseField
    public int idProduct;
    @DatabaseField
    public int vehicleId;
    @DatabaseField
    public String pcoVehicleNo;
    @DatabaseField
    public int createdBy;
    @DatabaseField
    public String deviceId;
    @DatabaseField
    public String typeOfQuery;
    @DatabaseField
    public int godown_Id;
    @DatabaseField
    public String send_date;
    @DatabaseField
    public String is_sync;
    @DatabaseField
    public String mode_of_entry;

    @DatabaseField
    public int Quantity;

    @DatabaseField
    public int Defective;

    @DatabaseField
    public int unique_id;


    @DatabaseField
    public String Id_ERV;

    @Override
    public String toString() {

        return "TruckSendDetailsDB{" +
                "truck_details_send_id=" + truck_details_send_id +
                ", truck_send_id=" + truck_send_id +
                ", ervNo='" + ervNo + '\'' +
                ", idProduct='" + idProduct + '\'' +
                ", vehicleId=" + vehicleId +
                ", pcoVehicleNo='" + pcoVehicleNo + '\'' +
                ", createdBy=" + createdBy +
                ", deviceId='" + deviceId + '\'' +
                ", send_date='" +send_date +'\'' +
                ", typeOfQuery='" + typeOfQuery + '\'' +
                ", godown_Id=" + godown_Id +
                ", is_sync='" + is_sync + '\'' +
                ", mode_of_entry='" + mode_of_entry + '\'' +
                ", Quantity='" + Quantity + '\'' +
                ", Defective='" + Defective + '\'' +
                '}';
    }
}
