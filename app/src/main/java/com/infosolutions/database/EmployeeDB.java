package com.infosolutions.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by shailesh on 11/9/17.
 */

@DatabaseTable(tableName = "Employee_Table")
public class EmployeeDB implements Serializable {

    public EmployeeDB() {
    }

    public EmployeeDB(int employee_table_id, int employee_id, String full_name, String ID_DESIGNATION, String CREDIT_GIVEN) {
        this.employee_table_id = employee_table_id;
        this.employee_id = employee_id;
        this.full_name = full_name;
        this.ID_DESIGNATION = ID_DESIGNATION;
        this.CREDIT_GIVEN = CREDIT_GIVEN;
    }

    @DatabaseField(generatedId = true, columnName = "employee_table_id")
    public int employee_table_id;

    @DatabaseField
    public int employee_id;

    @DatabaseField
    public String full_name;

    @DatabaseField
    public String ID_DESIGNATION;

    @DatabaseField
    public String CREDIT_GIVEN;


    @Override
    public String toString() {
        return "EmployeeDB{" +
                "employee_table_id=" + employee_table_id +
                ", employee_id=" + employee_id +
                ", full_name='" + full_name + '\'' +
                ", ID_DESIGNATION='" + ID_DESIGNATION + '\'' +
                '}';
    }
}
