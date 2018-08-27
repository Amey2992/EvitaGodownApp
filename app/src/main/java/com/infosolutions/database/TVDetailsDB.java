package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "TV_DETAILS")
public class TVDetailsDB implements Serializable {

    public TVDetailsDB() {
    }

    public TVDetailsDB(int tv_table_id, String consumer_no, int numberOfCylinder, int godownId, int product_code ,String typeOfQuery,String surrender_date, String is_sync, String deviceId, int userId, String tv_status) {
        this.tv_table_id = tv_table_id;
        this.consumer_no = consumer_no;
        this.numberOfCylinder = numberOfCylinder;
        this.godownId = godownId;
        this.product_code = product_code;
        this.typeOfQuery = typeOfQuery;
        this.surrender_date = surrender_date;
        this.is_sync = is_sync;
        this.deviceId = deviceId;
        this.userId = userId;
        this.tv_status = tv_status;
    }

    @DatabaseField(generatedId = true, columnName = "tv_detail_id")
    public int tv_table_id;
    @DatabaseField
    public String consumer_no;
    @DatabaseField
    public int numberOfCylinder;
    @DatabaseField
    public int godownId;
    @DatabaseField
    public int product_code;
    @DatabaseField
    public String typeOfQuery;
    @DatabaseField
    public String surrender_date;
    @DatabaseField
    public String is_sync;
    @DatabaseField
    public String deviceId;
    @DatabaseField
    public int userId;
    @DatabaseField
    public String tv_status;

    @Override
    public String toString() {
        return "TVDetailsDB{" +
                "tv_table_id=" + tv_table_id +
                ", consumer_no='" + consumer_no + '\'' +
                ", numberOfCylinder=" + numberOfCylinder +
                ", godownId=" + godownId +
                ", product_code=" + product_code +
                ", typeOfQuery='" + typeOfQuery + '\'' +
                ", surrender_date='" + surrender_date + '\'' +
                ", is_sync='" + is_sync + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", userId=" + userId +
                ", tv_status='" + tv_status + '\'' +
                '}';
    }
}
