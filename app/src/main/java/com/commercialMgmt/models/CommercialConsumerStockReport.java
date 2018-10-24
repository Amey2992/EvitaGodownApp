package com.commercialMgmt.models;

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


    public CommercialConsumerStockReport(){}

    public CommercialConsumerStockReport(JSONObject jsonObject){
        if(jsonObject != null){
            ConsumerNo = jsonObject.optString("ConsumerNo");
            ConsumerName = jsonObject.optString("ConsumerName");
            ProductId = jsonObject.optInt("ProductId");
            ProdName = jsonObject.optString("ProdName");
            date = jsonObject.optString("date");
            BPCLRate = jsonObject.optInt("BPCLRate");
            MRP = jsonObject.optInt("MRP");
            Discount = jsonObject.optInt("Discount");
            FullCyl = jsonObject.optInt("FullCyl");
            EmptyCyl = jsonObject.optInt("EmptyCyl");
            CreditCyl = jsonObject.optInt("CreditCyl");
            Amount = jsonObject.optInt("Amount");
            CreditAmount = jsonObject.optInt("CreditAmount");
            PaymentMode = jsonObject.optString("PaymentMode");
            PaymentStatus = jsonObject.optString("PaymentStatus");
            ChallanNo = jsonObject.optString("ChallanNo");
            Credit = jsonObject.optInt("Credit");
            Closing = jsonObject.optInt("Closing");
            Opening = jsonObject.optInt("Opening");
        }
    }










}