package com.infosolutions.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;


public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?> [] classes = new Class[]{ DomesticDeliveryDB.class,
			                                                CommercialDeliveryDB.class,
			                                                ProductDB.class, EmployeeDB.class,
			                                                VehicleDB.class};

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
	}
}
