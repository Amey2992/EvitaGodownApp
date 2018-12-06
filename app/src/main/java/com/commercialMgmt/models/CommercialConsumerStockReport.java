package com.commercialMgmt.models;

import com.infosolutions.network.VolleySingleton;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONObject;

import java.io.Serializable;

@DatabaseTable(tableName = "CommercialConsumerStockReportTable")
public class CommercialConsumerStockReport implements Serializable {

    @DatabaseField(generatedId = true, columnName = "id")
    public int id;

    @DatabaseField
    public String date;

    @DatabaseField
    public String ConsumerNo;

    @DatabaseField
    public String ConsumerName;

    @DatabaseField
    public int ProductId;

    @DatabaseField
    public String ProdName;

    @DatabaseField
    public int BPCLRate;

    @DatabaseField
    public int MRP;

    @DatabaseField
    public int Discount;

    @DatabaseField
    public int FullCyl;

    @DatabaseField
    public int EmptyCyl;

    @DatabaseField
    public int CreditCyl;

    @DatabaseField
    public int Amount;

    @DatabaseField
    public int CreditAmount;

    @DatabaseField
    public String PaymentMode;

    @DatabaseField
    public String PaymentStatus;

    @DatabaseField
    public String ChallanNo;

    @DatabaseField
    public int Credit;

    @DatabaseField
    public int Closing;

    @DatabaseField
    public int Opening;

    @DatabaseField
    public int sv;




    public CommercialConsumerStockReport(){}

    public CommercialConsumerStockReport(JSONObject jsonObject, VolleySingleton.CallType type){
        if(jsonObject != null){
            ProductId = jsonObject.optInt("ProductId");
            ProdName = jsonObject.optString("ProdName");
            date = jsonObject.optString("date");
            FullCyl = jsonObject.optInt("FullCyl");
            EmptyCyl = jsonObject.optInt("EmptyCyl");

            if(type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_CONSUMER)) {
                ConsumerNo = jsonObject.optString("ConsumerNo");
                ConsumerName = jsonObject.optString("ConsumerName");
                BPCLRate = jsonObject.optInt("BPCLRate");
                MRP = jsonObject.optInt("MRP");
                Discount = jsonObject.optInt("Discount");
                CreditCyl = jsonObject.optInt("CreditCyl");
                Amount = jsonObject.optInt("Amount");
                CreditAmount = jsonObject.optInt("CreditAmount");
                PaymentMode = jsonObject.optString("PaymentMode");
                PaymentStatus = jsonObject.optString("PaymentStatus");
                ChallanNo = jsonObject.optString("ChallanNo");
                sv = jsonObject.optInt("sv");

            }else if(type.equals(VolleySingleton.CallType.COMMERCIAL_REPORT_STOCK)){
                Credit = jsonObject.optInt("Credit");
                Closing = jsonObject.optInt("Closing");
                Opening = jsonObject.optInt("Opening");
                sv = jsonObject.optInt("sv");
            }
        }
    }










}
