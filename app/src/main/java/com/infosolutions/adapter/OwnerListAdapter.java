package com.infosolutions.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.infosolutions.evita.R;
import com.infosolutions.model.OwnerModel;
import com.infosolutions.network.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shailesh on 12/11/17.
 */

public class OwnerListAdapter extends BaseAdapter {

    private List<OwnerModel> listOwner = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    //private Typeface typefaceBold, typefaceLarge;

    public OwnerListAdapter(Context context, List<OwnerModel> listOwner) {
        this.listOwner = listOwner;
        this.mContext = context;
        inflater = LayoutInflater.from(this.mContext);
        //typefaceBold = Typeface.createFromAsset(mContext.getAssets(), Constants.TYPEFACE_BOLD);
        //typefaceLarge = Typeface.createFromAsset(mContext.getAssets(), Constants.TYPEFACE_LARGE);
    }

    @Override
    public int getCount() {
        return listOwner.size();
    }

    @Override
    public Object getItem(int position) {
        return listOwner.get(position);
    }



    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OwnerViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_owner_view, parent, false);
            holder = new OwnerViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (OwnerViewHolder) convertView.getTag();
        }

        OwnerModel currentListData = listOwner.get(position);
        holder.tvTitle.setText(currentListData.getTile());
        final OwnerModel album = listOwner.get(position);
        JSONArray ownerArray = album.getArrayJson();

        try {
            switch (position){

                case 0:
                    holder.tvTitle.setText(album.getTile());
                    openingLayout(holder.tableOwner,ownerArray);
                    break;

                case 1:
                    holder.tvTitle.setText(album.getTile());
                    domesticLayout(holder.tableOwner, ownerArray);
                    break;

                case 2:
                    holder.tvTitle.setText(album.getTile());
                    commercialLayout(holder.tableOwner, ownerArray);
                    break;

                case 3:
                    holder.tvTitle.setText(album.getTile());
                    receiveLayout(holder.tableOwner, ownerArray);
                    break;

                case 4:
                    holder.tvTitle.setText(album.getTile());
                    sendingLayout(holder.tableOwner, ownerArray);
                    break;

                case 5:
                    holder.tvTitle.setText(album.getTile());
                    tvDetailLayout(holder.tableOwner, ownerArray);
                    break;

                case 6:
                    holder.tvTitle.setText(album.getTile());
                    closingLayout(holder.tableOwner, ownerArray);
                    break;

                case 7:
                    holder.tvTitle.setText(album.getTile());
                    otherLayout(holder.tableOwner, ownerArray);
                    break;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }


    class OwnerViewHolder {

        private TextView tvTitle;
        private TableLayout tableOwner;

        public OwnerViewHolder(View itemView) {

            tvTitle = itemView.findViewById(R.id.tvTitleOpening);
            tableOwner = itemView.findViewById(R.id.tableOwner);
            //tvTitle.setTypeface(typefaceBold);
        }
    }



    private void openingLayout(TableLayout tableLayout, JSONArray arrayOpening) throws JSONException {

        if (arrayOpening.length()>0){
            setHeaders(tableLayout,"PRODUCT", "FULL", "EMPTY", "DEFECTIVE","","","");

            for (int i=0; i<arrayOpening.length(); i++){

                JSONObject openingItem = arrayOpening.optJSONObject(i);
                String DESCRIPTION = openingItem.optString("DESCRIPTION");
                String OPENING_FULL = openingItem.optString("OPENING_FULL");
                String OPENING_EMPTY = openingItem.optString("OPENING_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");

                createLayout(tableLayout, DESCRIPTION, OPENING_FULL, OPENING_EMPTY, DEFECTIVE,"","","");
            }
        }
    }
    private void domesticLayout(TableLayout tableLayout, JSONArray arrayDomestic) throws JSONException{

        if (arrayDomestic.length()>0){
            setHeaders(tableLayout, "PRODUCT", "FULL","EMPTY", "DEFECTIVE","SV", "TV","" );

            for (int i=0; i<arrayDomestic.length(); i++){
                JSONObject openingItem = arrayDomestic.optJSONObject(i);

                String Description = openingItem.optString("DESCRIPTION");
                String DELIVERY_FULL = openingItem.optString("DELIVERY_FULL");
                String DELIVERY_EMPTY = openingItem.optString("DELIVERY_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");
                String SV = openingItem.optString("SV");
                String TV = openingItem.optString("TV");
                /* create table view*/

                setHeaders(tableLayout, Description, DELIVERY_FULL, DELIVERY_EMPTY,DEFECTIVE, SV, TV,"");
            }
        }

    }
    private void commercialLayout(TableLayout tableLayout, JSONArray arrayCommercial) throws JSONException{

        if (arrayCommercial.length()>0){
            setHeaders(tableLayout,"PRODUCT", "FULL","EMPTY", "DEFECTIVE","SV", "TV" ,"");

            for (int i=0; i<arrayCommercial.length(); i++){
                JSONObject openingItem = arrayCommercial.optJSONObject(i);

                String Description = openingItem.optString("DESCRIPTION");
                String DELIVERY_FULL = openingItem.optString("DELIVERY_FULL");
                String DELIVERY_EMPTY = openingItem.optString("DELIVERY_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");
                String SV = openingItem.optString("SV");
                String TV = openingItem.optString("TV");
                /* create table view*/

                createLayout(tableLayout, Description, DELIVERY_FULL, DELIVERY_EMPTY,DEFECTIVE, SV, TV,"");
            }
        }
    }
    private void receiveLayout(TableLayout tableLayout, JSONArray arrayReceive) throws JSONException {

        if (arrayReceive.length()>0){
            setHeaders(tableLayout,"PRODUCT", "SOUND", "LOST","","","","");

            for (int i=0; i<arrayReceive.length(); i++){
                JSONObject openingItem = arrayReceive.optJSONObject(i);

                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String SOUND = openingItem.optString("SOUND");
                String LOST_TRUCK_RECEVING = openingItem.optString("LOST_TRUCK_RECEVING");

                createLayout(tableLayout, PRODUCT_NAME, SOUND, LOST_TRUCK_RECEVING,"","","","");
            }
        }


    }
    private void sendingLayout(TableLayout tableLayout, JSONArray arraySend) throws JSONException{
        if (arraySend.length()>0){
            setHeaders(tableLayout, "PRODUCT", "SOUND","DEFECTIVE","","","","");

            for (int i=0; i<arraySend.length(); i++){
                JSONObject openingItem = arraySend.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String SOUND = openingItem.optString("SOUND");
                String DEFECTIVE = openingItem.optString("TRUCK_DEFECTIVE");

                createLayout(tableLayout, PRODUCT_NAME, SOUND, DEFECTIVE,"","","","");
            }
        }

    }
    private void tvDetailLayout(TableLayout tableLayout, JSONArray arrayTVDetails) throws JSONException {
        if (arrayTVDetails.length()>0){
            setHeaders(tableLayout, "PRODUCT", "FULL", "EMPTY", "DEFECTIVE","","","");

            for (int i=0; i<arrayTVDetails.length(); i++){
                JSONObject openingItem = arrayTVDetails.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String CLOSING_FULL = openingItem.optString("CLOSING_FULL");
                String CLOSING_EMPTY = openingItem.optString("CLOSING_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");

                createLayout(tableLayout, PRODUCT_NAME,CLOSING_FULL,CLOSING_EMPTY, DEFECTIVE,"","","");
            }
        }

    }
    private void closingLayout(TableLayout tableLayout, JSONArray arrayClosing) throws JSONException{

        if (arrayClosing.length()>0){
            setHeaders(tableLayout, "PRODUCT", "FULL", "EMPTY", "DEFECTIVE","","","");

            for (int i=0; i<arrayClosing.length(); i++){
                JSONObject openingItem = arrayClosing.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");

                String CLOSING_FULL = openingItem.optString("CLOSING_FULL");
                String CLOSING_EMPTY = openingItem.optString("CLOSING_EMPTY");
                String DEFECTIVE = openingItem.optString("DEFECTIVE");

                createLayout(tableLayout, PRODUCT_NAME,CLOSING_FULL,CLOSING_EMPTY, DEFECTIVE,"","","");
            }
        }
    }
    private void otherLayout(TableLayout tableLayout, JSONArray arrayOther) throws JSONException{

        if (arrayOther.length()>0){
            setHeaders(tableLayout, "PRODUCT", "CREDIT", "ON FEILD", "LOST","","","");

            for (int i=0; i<arrayOther.length(); i++){
                JSONObject openingItem = arrayOther.optJSONObject(i);
                String PRODUCT_NAME = openingItem.optString("DESCRIPTION");
                String CREDIT = openingItem.optString("CREDIT");
                String ON_FIELD = openingItem.optString("ON_FIELD");
                String LOST_DELIVERY_CYLINDERS = openingItem.optString("LOST_DELIVERY_CYLINDERS");

                createLayout(tableLayout, PRODUCT_NAME, CREDIT, ON_FIELD, LOST_DELIVERY_CYLINDERS,"","","");
            }
        }
    }
    private void createLayout(TableLayout tableLayout,String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7){

        TableRow row= new TableRow(mContext);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,10,10,10);
        row.setLayoutParams(lp);


        TextView tvHeaderTitle1 = new TextView(mContext);
        TextView tvHeaderTitle2 = new TextView(mContext);
        TextView tvHeaderTitle3 = new TextView(mContext);
        TextView tvHeaderTitle4 = new TextView(mContext);
        TextView tvHeaderTitle5 = new TextView(mContext);
        TextView tvHeaderTitle6 = new TextView(mContext);


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);

        applyRowMarginLeft(tvHeaderTitle1);
        applyRowMarginRight(tvHeaderTitle2);
        applyRowMarginRight(tvHeaderTitle3);
        applyRowMarginRight(tvHeaderTitle4);
        applyRowMarginRight(tvHeaderTitle5);
        applyRowMarginRight(tvHeaderTitle6);

        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);

        tableLayout.addView(row);
    }
    private void setHeaders(TableLayout tableLayout, String headerTitle1,String headerTitle2,  String headerTitle3, String headerTitle4, String headerTitle5, String headerTitle6, String headerTitle7) {

        TableRow row= new TableRow(mContext);
        row.setBackground(mContext.getResources().getDrawable(R.drawable.circle_background));
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tvHeaderTitle1 = new TextView(mContext);
        TextView tvHeaderTitle2 = new TextView(mContext);
        TextView tvHeaderTitle3 = new TextView(mContext);
        TextView tvHeaderTitle4 = new TextView(mContext);
        TextView tvHeaderTitle5 = new TextView(mContext);
        TextView tvHeaderTitle6 = new TextView(mContext);


        tvHeaderTitle1.setText(headerTitle1);
        tvHeaderTitle2.setText(headerTitle2);
        tvHeaderTitle3.setText(headerTitle3);
        tvHeaderTitle4.setText(headerTitle4);
        tvHeaderTitle5.setText(headerTitle5);
        tvHeaderTitle6.setText(headerTitle6);


        applyHeaderMargin(tvHeaderTitle1);
        applyHeaderMargin(tvHeaderTitle2);
        applyHeaderMargin(tvHeaderTitle3);
        applyHeaderMargin(tvHeaderTitle4);
        applyHeaderMargin(tvHeaderTitle5);
        applyHeaderMargin(tvHeaderTitle6);


        row.addView(tvHeaderTitle1);
        row.addView(tvHeaderTitle2);
        row.addView(tvHeaderTitle3);
        row.addView(tvHeaderTitle4);
        row.addView(tvHeaderTitle5);
        row.addView(tvHeaderTitle6);

        tableLayout.addView(row);
    }
    private TextView applyHeaderMargin(TextView view) {

        view.setTextColor(Color.WHITE);
        view.setTextSize(15);
        //view.setTypeface(typefaceBold);
        view.setPadding(20, 10, 10, 4);
        return view;
    }
    private TextView applyRowMarginLeft(TextView viewHeader) {

        viewHeader.setTextSize(15);
        viewHeader.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        //viewHeader.setTypeface(typefaceBold);
        viewHeader.setPadding(10, 10, 10, 10);
        viewHeader.setGravity(Gravity.LEFT);
        return viewHeader;
    }
    private TextView applyRowMarginRight(TextView viewRow) {

        viewRow.setTextSize(15);
        viewRow.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        //viewRow.setTypeface(typefaceLarge);
        viewRow.setPadding(10, 10, 10, 10);
        viewRow.setGravity(Gravity.RIGHT);
        return viewRow;
    }

}
