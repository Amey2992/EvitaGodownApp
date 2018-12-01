package com.infosolutions.database;

import com.google.gson.JsonObject;
import com.infosolutions.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;


@DatabaseTable(tableName = "DomesticDeliveryTable")
public class DomesticDeliveryDB implements Serializable, Comparator<DomesticDeliveryDB> {


  public DomesticDeliveryDB() {
  }

  public DomesticDeliveryDB(int delivery_id, int employee_id, int trip_number, int product_id, String given_time,
                            int given_by, int fresh_full_cylinder, int empty_received, int sv_field, int dbc_field,
                            int defective_field, int return_full_cylinder, String received_time, int received_by, int lost_cyl,
                            String mode_of_entry, String type_of_query, String is_sync, int godown_code, String deviceId,
                            String given_date, boolean isFresh, boolean isReturn, boolean isCompleted, String uniqueID) {
    this.delivery_id = delivery_id;
    this.employee_id = employee_id;
    this.trip_number = trip_number;
    this.product_id = product_id;
    this.given_time = given_time;
    this.given_by = given_by;
    this.fresh_full_cylinder = fresh_full_cylinder;
    this.empty_received = empty_received;
    this.sv_field = sv_field;
    this.dbc_field = dbc_field;
    this.defective_field = defective_field;
    this.return_full_cylinder = return_full_cylinder;
    this.received_time = received_time;
    this.received_by = received_by;
    this.lost_cylinder = lost_cyl;
    this.mode_of_entry = mode_of_entry;
    this.type_of_query = type_of_query;
    this.is_sync = is_sync;
    this.godown_Id = godown_code;
    this.deviceId = deviceId;
    this.given_date = given_date;
    this.isFresh = isFresh;
    this.isReturn = isReturn;
    this.isCompleted = isCompleted;
    this.uniqueID = uniqueID;
  }

  public DomesticDeliveryDB(JSONObject joDomestic) {
    if (joDomestic != null) {
      given_time = joDomestic.optString("Given_Time");
      employee_id = joDomestic.optInt("Id_Emp");
      trip_number = joDomestic.optInt("Trip_No");
      type_of_query = joDomestic.optString("TypeOfQuery");
      product_id = joDomestic.optInt("Id_Product");
      fresh_full_cylinder = joDomestic.optInt("Full_Cyl_Given");
      given_by = joDomestic.optInt("Giveen_By");
      empty_received = joDomestic.optInt("Empty_Recieved");
      sv_field = joDomestic.optInt("SV");
      dbc_field = joDomestic.optInt("DBC");
      defective_field = joDomestic.optInt("Defective");
      return_full_cylinder = joDomestic.optInt("Return_Full");
      received_by = joDomestic.optInt("Recieved_By");
      lost_cylinder = joDomestic.optInt("Lost");
      received_time = joDomestic.optString("Recieved_Time");
      mode_of_entry = joDomestic.optString("Mode_of_Entry", "Mobile");
      godown_Id = joDomestic.optInt("Godown_Code");
      deviceId = joDomestic.optString("Device_Id");
      is_sync = "Y";
      given_date = Constants.getDateTime();
      isFresh = joDomestic.optBoolean("isFresh");
      isReturn = joDomestic.optBoolean("isReturn");
      isCompleted = joDomestic.optBoolean("isCompleted");
      uniqueID = joDomestic.optString("uniqueID");
      //is_sync = joDomestic.put("Sync_Time", getDateTime());

    }
  }

  @DatabaseField(generatedId = true, columnName = "delivery_id")
  public int delivery_id;
  @DatabaseField
  public int employee_id;
  @DatabaseField
  public int trip_number;
  @DatabaseField
  public int product_id;
  @DatabaseField
  public String given_time;
  @DatabaseField
  public int given_by;
  @DatabaseField
  public int fresh_full_cylinder;
  @DatabaseField
  public int empty_received;
  @DatabaseField
  public int lost_cylinder;
  @DatabaseField
  public int sv_field;
  @DatabaseField
  public int dbc_field;
  @DatabaseField
  public int defective_field;
  @DatabaseField
  public int return_full_cylinder;
  @DatabaseField
  public String received_time;
  @DatabaseField
  public int received_by;
  @DatabaseField
  public String mode_of_entry;
  @DatabaseField
  public String type_of_query;
  @DatabaseField
  public String is_sync;
  @DatabaseField
  public int godown_Id;
  @DatabaseField
  public String deviceId;
  @DatabaseField
  public String given_date;

  @DatabaseField
  public boolean isFresh;

  @DatabaseField
  public boolean isReturn;

  @DatabaseField
  public boolean isCompleted;

  @DatabaseField
  public String uniqueID;

  @Override
  public String toString() {
    return "DomesticDeliveryDB{" +
            "delivery_id=" + delivery_id +
            ", employee_id=" + employee_id +
            ", trip_number=" + trip_number +
            ", product_id=" + product_id +
            ", given_time='" + given_time + '\'' +
            ", given_by=" + given_by +
            ", fresh_full_cylinder=" + fresh_full_cylinder +
            ", empty_received=" + empty_received +
            ", lost_cylinder=" + lost_cylinder +
            ", sv_field=" + sv_field +
            ", dbc_field=" + dbc_field +
            ", defective_field=" + defective_field +
            ", return_full_cylinder=" + return_full_cylinder +
            ", received_time='" + received_time + '\'' +
            ", received_by=" + received_by +
            ", mode_of_entry='" + mode_of_entry + '\'' +
            ", type_of_query='" + type_of_query + '\'' +
            ", is_sync='" + is_sync + '\'' +
            ", godown_Id=" + godown_Id +
            ", deviceId='" + deviceId + '\'' +
            ", given_date='" + given_date + '\'' +
            '}';
  }

  @Override
  public int compare(DomesticDeliveryDB o1, DomesticDeliveryDB o2) {
    return Integer.compare(o2.trip_number, o1.trip_number);
  }
}