package com.infosolutions.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import com.commercialMgmt.models.CommercialConsumerModel;
import com.commercialMgmt.models.CommercialProductModel;
import com.commercialMgmt.models.CommercialStockModel;
import com.commercialMgmt.models.ConsumerModel;
import com.commercialMgmt.models.UserAssignedCylinderModel;
import com.infosolutions.evita.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;



public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	public static final String DATABASE_NAME = "Evita.db";
	private static final int DATABASE_VERSION = 7;

	private Dao<DomesticDeliveryDB, Integer> domesticDao = null;
	private RuntimeExceptionDao<DomesticDeliveryDB, Integer>
			domesticRTExceptionDao = null;

	private Dao<CommercialDeliveryDB, Integer> commercialDao = null;
	private RuntimeExceptionDao<CommercialDeliveryDB, Integer>
			commercialRTExceptionDao = null;

	private Dao<EmployeeDB, Integer> employeeDao = null;
	private RuntimeExceptionDao<EmployeeDB, Integer>
			employeeRTExceptionDao = null;

	private Dao<ProductDB, Integer> productDao = null;
	private RuntimeExceptionDao<ProductDB, Integer>
			productRTExceptionDao = null;

	private Dao<CommercialProductModel, Integer> comProductDao = null;
	private RuntimeExceptionDao<CommercialProductModel, Integer>
			comProductRTExceptionDao = null;

	private Dao<ConsumerModel, Integer> comCosnumerDao = null;
	private RuntimeExceptionDao<ConsumerModel, Integer>
			comConsumerRTExceptionDao = null;

	private Dao<VehicleDB, Integer> vehicleDao = null;
	private RuntimeExceptionDao<VehicleDB, Integer>
			vehicleRTExceptionDao = null;

	private Dao<TVDetailsDB, Integer> tvDetailsDao = null;
	private RuntimeExceptionDao<TVDetailsDB, Integer>
			tvDetailRTExceptionDao = null;

	private Dao<TruckDetailsDB, Integer> truckDetailsDao = null;
	private RuntimeExceptionDao<TruckDetailsDB, Integer>
			truckDetailRTExceptionDao = null;

	private Dao<TruckSendDetailsDB, Integer>
			truckDetailsSendDao = null;

	private RuntimeExceptionDao<TruckSendDetailsDB, Integer>
			truckDetailSendRTExceptionDao = null;

	private Dao<CommercialDeliveryCreditDB, Integer>
			commercialDeliveryCreditDao = null;

	private RuntimeExceptionDao<CommercialDeliveryCreditDB, Integer>
			commercialDeliveryCreditExceptionDao = null;

	private Dao<ConsumerDetails, Integer>
			consumerDetailsDao= null;

	private RuntimeExceptionDao<ConsumerDetails, Integer>
			consumerDetailsRuntimeExceptionDao = null;


	private Dao<ConsumerModel, Integer>
			consumerModelDao= null;

	private RuntimeExceptionDao<ConsumerModel, Integer>
			consumerModelRuntimeExceptionDao = null;

	private RuntimeExceptionDao<CommercialProductModel, Integer>
			commercialProductModelRuntimeExceptionDao = null;

	private RuntimeExceptionDao<CommercialStockModel, Integer>
			commercialStockModelRuntimeExceptionDao = null;

	private RuntimeExceptionDao<UserAssignedCylinderModel, Integer>
			userAssignedCylinderModelRuntimeExceptionDao = null;


	/*private RuntimeExceptionDao<CommercialConsumerModel, Integer>
			commercialConsumerModelRuntimeExceptionDao = null;
*/

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null,
				DATABASE_VERSION, R.raw.ormlite_config);
	}


	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {

			TableUtils.createTable(connectionSource, DomesticDeliveryDB.class);
			TableUtils.createTable(connectionSource, CommercialDeliveryDB.class);
			TableUtils.createTable(connectionSource, EmployeeDB.class);
			TableUtils.createTable(connectionSource, ProductDB.class);
			TableUtils.createTable(connectionSource, VehicleDB.class);
			TableUtils.createTable(connectionSource, TVDetailsDB.class);
			TableUtils.createTable(connectionSource, TruckDetailsDB.class);
			TableUtils.createTable(connectionSource, TruckSendDetailsDB.class);
			TableUtils.createTable(connectionSource, CommercialDeliveryCreditDB.class);
			TableUtils.createTable(connectionSource, ConsumerDetails.class);
			TableUtils.createTable(connectionSource, ConsumerModel.class);
			TableUtils.createTable(connectionSource, CommercialProductModel.class);
			TableUtils.createTable(connectionSource, CommercialStockModel.class);
			TableUtils.createTable(connectionSource, UserAssignedCylinderModel.class);
			//TableUtils.createTable(connectionSource, CommercialConsumerModel.class);

		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
		}
	}

	public boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {
		SQLiteDatabase db = this.getReadableDatabase();
		String Query = "Select * from " + "DomesticDeliveryTable" + " where " + "given_time" + " = " + fieldValue;
		Cursor cursor = db.rawQuery(Query, null);
		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
// ----------------------------------------   Full Cylinder available or not  ----------------------------------------

	public boolean CheckIsDataAlreadyAvailableDBorNot(String fieldValue) {
		SQLiteDatabase db = this.getReadableDatabase();
		String Query = "Select employee_id,product_id,given_time,trip_number,godown_Id,fresh_full_cylinder from " + "DomesticDeliveryTable" + " where " + "timeStamp" + " = " + fieldValue;
		Cursor cursor = db.rawQuery(Query, null);
		if(cursor.getCount() <= 0){
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

//---------------------------------------------------------------------------------------------------------------------


	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, DomesticDeliveryDB.class, true);
			TableUtils.dropTable(connectionSource, CommercialDeliveryDB.class, true);
			TableUtils.dropTable(connectionSource, EmployeeDB.class, true);
			TableUtils.dropTable(connectionSource, ProductDB.class, true);
			TableUtils.dropTable(connectionSource, VehicleDB.class, true);
			TableUtils.dropTable(connectionSource, TVDetailsDB.class, true);
			TableUtils.dropTable(connectionSource, TruckDetailsDB.class, true);
			TableUtils.dropTable(connectionSource, TruckSendDetailsDB.class, true);
			TableUtils.dropTable(connectionSource, CommercialDeliveryCreditDB.class, true);
			TableUtils.dropTable(connectionSource, ConsumerDetails.class, true);
			TableUtils.dropTable(connectionSource, ConsumerModel.class,true);
			TableUtils.dropTable(connectionSource, CommercialProductModel.class,true);
			TableUtils.dropTable(connectionSource, CommercialStockModel.class,true);
			TableUtils.dropTable(connectionSource, UserAssignedCylinderModel.class,true);
			//TableUtils.dropTable(connectionSource, CommercialConsumerModel.class,true);

			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new " + newVer, e);
		}
	}


	/**
	 * @return
	 * @throws SQLException
	 */
	public Dao<DomesticDeliveryDB, Integer> getDomesticDeliveryDao() throws SQLException {
		if (domesticDao == null) {
			domesticDao = getDao(DomesticDeliveryDB.class);
		}
		return domesticDao;
	}
	public RuntimeExceptionDao<DomesticDeliveryDB, Integer> getDomesticRuntimeExceptionDao() {

		if (domesticRTExceptionDao == null) {
			domesticRTExceptionDao = getRuntimeExceptionDao(DomesticDeliveryDB.class);
		}
		return domesticRTExceptionDao;
	}


	/**
	 * @return
	 * @throws SQLException
	 */

	public Dao<CommercialDeliveryDB, Integer> getCommercialDao() throws SQLException {
		if (commercialDao == null) {
			commercialDao = getDao(CommercialDeliveryDB.class);
		}
		return commercialDao;
	}

	public RuntimeExceptionDao<CommercialDeliveryDB, Integer> getCommercialRuntimeExceptionDao() {

		if (commercialRTExceptionDao == null) {
			commercialRTExceptionDao = getRuntimeExceptionDao(CommercialDeliveryDB.class);
		}
		return commercialRTExceptionDao;
	}
	/*********************************************************************************************/


	/**
	 * @return
	 * @throws SQLException
	 */

	public Dao<EmployeeDB, Integer> getEmployeeDao() throws SQLException {
		if (employeeDao == null) {
			employeeDao = getDao(EmployeeDB.class);
		}
		return employeeDao;
	}

	public RuntimeExceptionDao<EmployeeDB, Integer> getEmployeeRTExceptionDao() {

		if (employeeRTExceptionDao == null) {
			employeeRTExceptionDao = getRuntimeExceptionDao(EmployeeDB.class);
		}
		return employeeRTExceptionDao;
	}
	/*********************************************************************************************/


	/**
	 * @return
	 * @throws SQLException
	 */

	public Dao<ProductDB, Integer> getProductDao() throws SQLException {
		if (productDao == null) {
			productDao = getDao(ProductDB.class);
		}
		return productDao;
	}

	public RuntimeExceptionDao<ProductDB, Integer> getProductRTExceptionDao() {

		if (productRTExceptionDao == null) {
			productRTExceptionDao = getRuntimeExceptionDao(ProductDB.class);
		}
		return productRTExceptionDao;
	}

	public RuntimeExceptionDao<CommercialProductModel, Integer> getComProductRTExceptionDao() {

		if (comProductRTExceptionDao == null) {
			comProductRTExceptionDao = getRuntimeExceptionDao(CommercialProductModel.class);
		}
		return comProductRTExceptionDao;
	}

	public RuntimeExceptionDao<ConsumerModel, Integer> getComConsumerRTExceptionDao() {

		if (comConsumerRTExceptionDao == null) {
			comConsumerRTExceptionDao = getRuntimeExceptionDao(ConsumerModel.class);
		}
		return comConsumerRTExceptionDao;
	}



	/**
	 * @return
	 * @throws SQLException
	 */

	public Dao<VehicleDB, Integer> getVehicleDao() throws SQLException {
		if (vehicleDao == null) {
			vehicleDao = getDao(VehicleDB.class);
		}
		return vehicleDao;
	}

	public RuntimeExceptionDao<VehicleDB, Integer> getVehicleRTExceptionDao() {

		if (vehicleRTExceptionDao == null) {
			vehicleRTExceptionDao = getRuntimeExceptionDao(VehicleDB.class);
		}
		return vehicleRTExceptionDao;
	}


	/**
	 * @return
	 * @throws SQLException
	 */
	public Dao<TVDetailsDB, Integer> getTVDetailsDao() throws SQLException {
		if (tvDetailsDao == null) {
			tvDetailsDao = getDao(TVDetailsDB.class);
		}
		return tvDetailsDao;
	}

	public RuntimeExceptionDao<TVDetailsDB, Integer> getTVDetailRTExceptionDao() {

		if (tvDetailRTExceptionDao == null) {
			tvDetailRTExceptionDao = getRuntimeExceptionDao(TVDetailsDB.class);
		}
		return tvDetailRTExceptionDao;
	}


	/**
	 * @return
	 * @throws SQLException
	 */
	public Dao<TruckDetailsDB, Integer> getTruckDetailsDao() throws SQLException {
		if (truckDetailsDao == null) {
			truckDetailsDao = getDao(TruckDetailsDB.class);
		}
		return truckDetailsDao;
	}

	public RuntimeExceptionDao<TruckDetailsDB, Integer> getTruckDetailRTExceptionDao() {

		if (truckDetailRTExceptionDao == null) {
			truckDetailRTExceptionDao = getRuntimeExceptionDao(TruckDetailsDB.class);
		}
		return truckDetailRTExceptionDao;
	}


	/**
	 * @return
	 * @throws SQLException
	 */
	public Dao<TruckSendDetailsDB, Integer> getTruckDetailSendDao() throws SQLException {
		if (truckDetailsSendDao == null) {
			truckDetailsSendDao = getDao(TruckSendDetailsDB.class);
		}
		return truckDetailsSendDao;
	}

	public RuntimeExceptionDao<TruckSendDetailsDB, Integer> getTruckDetailSendRTExceptionDao() {

		if (truckDetailSendRTExceptionDao == null) {
			truckDetailSendRTExceptionDao = getRuntimeExceptionDao(TruckSendDetailsDB.class);
		}
		return truckDetailSendRTExceptionDao;
	}
	/*********************************************************************************************/


	/**
	 * @return
	 * @throws SQLException
	 */

	public Dao<CommercialDeliveryCreditDB, Integer> getCommCreditDao() throws SQLException {
		if (commercialDeliveryCreditDao == null) {
			commercialDeliveryCreditDao = getDao(CommercialDeliveryCreditDB.class);
		}
		return commercialDeliveryCreditDao;
	}

	/**
	 *
	 * @return
	 */
	public RuntimeExceptionDao<CommercialDeliveryCreditDB, Integer> getCommercialCreditExceptionDao() {

		if (commercialDeliveryCreditExceptionDao == null) {
			commercialDeliveryCreditExceptionDao = getRuntimeExceptionDao(CommercialDeliveryCreditDB.class);
		}
		return commercialDeliveryCreditExceptionDao;
	}

	public Dao<ConsumerDetails, Integer> getConsumerDao() throws SQLException {
		if (consumerDetailsDao == null) {
			consumerDetailsDao = getDao(ConsumerDetails.class);
		}
		return consumerDetailsDao;
	}


	public RuntimeExceptionDao<ConsumerDetails, Integer> getConsumerExceptionDao() {

		if (consumerDetailsRuntimeExceptionDao == null) {
			consumerDetailsRuntimeExceptionDao = getRuntimeExceptionDao(ConsumerDetails.class);
		}
		return consumerDetailsRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<ConsumerModel, Integer> getConsumerModelExceptionDao() {

		if (consumerModelRuntimeExceptionDao == null) {
			consumerModelRuntimeExceptionDao = getRuntimeExceptionDao(ConsumerModel.class);
		}
		return consumerModelRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<CommercialProductModel, Integer> getCommercialProductModelExceptionDao() {

		if (commercialProductModelRuntimeExceptionDao == null) {
			commercialProductModelRuntimeExceptionDao = getRuntimeExceptionDao(CommercialProductModel.class);
		}
		return commercialProductModelRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<CommercialStockModel, Integer> getCommercialStockModelExceptionDao() {

		if (commercialStockModelRuntimeExceptionDao == null) {
			commercialStockModelRuntimeExceptionDao = getRuntimeExceptionDao(CommercialStockModel.class);
		}
		return commercialStockModelRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<UserAssignedCylinderModel, Integer> getUserAssignedCylinderModelRuntimeExceptionDao() {

		if (userAssignedCylinderModelRuntimeExceptionDao == null) {
			userAssignedCylinderModelRuntimeExceptionDao = getRuntimeExceptionDao(UserAssignedCylinderModel.class);
		}
		return userAssignedCylinderModelRuntimeExceptionDao;
	}

	/*public RuntimeExceptionDao<CommercialConsumerModel, Integer> getCommercialConsumerModelExceptionDao() {

		if (commercialConsumerModelRuntimeExceptionDao == null) {
			commercialConsumerModelRuntimeExceptionDao = getRuntimeExceptionDao(CommercialConsumerModel.class);
		}
		return commercialConsumerModelRuntimeExceptionDao;
	}*/
}
