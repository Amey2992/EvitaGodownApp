package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;


@DatabaseTable(tableName = "CommercialTable")
public class CommercialDeliveryDB implements Serializable {

	public CommercialDeliveryDB(){ }

	public CommercialDeliveryDB(int com_delivery_id, int employee_id, int godownId,
								int product_id, String given_time, int given_by, int fresh_full_cylinder,
								int empty_received, int sv_field, int dbc_field, int defective_field,
								int return_full_cylinder, int return_credit_given, String received_time,
								int received_by, int lost_cyl, String mode_of_entry, String type_of_query, String is_sync,
								String deviceId , String uniqueID){  //, String uniqueID) {
		this.delivery_id = com_delivery_id;
		this.employee_id = employee_id;
		this.godownId = godownId;
		this.product_id = product_id;
		this.given_time = given_time;
		this.given_by = given_by;
		this.fresh_full_cylinder = fresh_full_cylinder;
		this.empty_received = empty_received;
		this.sv_field = sv_field;
		this.dbc_field = dbc_field;
		this.defective_field = defective_field;
		this.return_full_cylinder = return_full_cylinder;
		this.return_credit_given = return_credit_given;
		this.received_time = received_time;
		this.received_by = received_by;
		this.lost_cyl = lost_cyl;
		this.mode_of_entry = mode_of_entry;
		this.type_of_query = type_of_query;
		this.is_sync = is_sync;
		this.deviceId = deviceId;
		this.uniqueID=uniqueID;
	}




	@DatabaseField(generatedId = true, columnName = "delivery_id")
	public int delivery_id;

	@DatabaseField
	public int employee_id;
	@DatabaseField
	public int godownId;
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
	public int sv_field;
	@DatabaseField
	public int dbc_field;
	@DatabaseField
	public int defective_field;
	@DatabaseField
	public int return_full_cylinder;
	@DatabaseField
	public int return_credit_given;
	@DatabaseField
	public String received_time;
	@DatabaseField
	public int received_by;
	@DatabaseField
	public int lost_cyl;
	@DatabaseField
	public String mode_of_entry;
	@DatabaseField
	public String type_of_query;
	@DatabaseField
	public String is_sync;
	@DatabaseField
	public String deviceId;
	@DatabaseField
	public String uniqueID;

	@Override
	public String toString() {
		return "CommercialDeliveryDB{" +
				"delivery_id=" + delivery_id +
				", employee_id=" + employee_id +
				", godownId=" + godownId +
				", product_id=" + product_id +
				", given_time='" + given_time + '\'' +
				", given_by=" + given_by +
				", fresh_full_cylinder=" + fresh_full_cylinder +
				", empty_received=" + empty_received +
				", sv_field=" + sv_field +
				", dbc_field=" + dbc_field +
				", defective_field=" + defective_field +
				", return_full_cylinder=" + return_full_cylinder +
				", return_credit_given=" + return_credit_given +
				", received_time='" + received_time + '\'' +
				", received_by=" + received_by +
				", lost_cyl=" + lost_cyl +
				", mode_of_entry='" + mode_of_entry + '\'' +
				", type_of_query='" + type_of_query + '\'' +
				", is_sync='" + is_sync + '\'' +
				", deviceId='" + deviceId + '\'' +
				", uniqueID='" + uniqueID +'\''+
				'}';
	}
}
