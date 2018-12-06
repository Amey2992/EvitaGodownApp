package com.infosolutions.ui.user.truckdelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.ProductDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.VehicleDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.utils.AppSettings;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.infosolutions.network.Constants.getSharedPrefWithKEY;
import static java.lang.Integer.parseInt;

public class TruckReceivedOwnFragment extends Fragment {
    private int USER_ID;
    private DatabaseHelper databaseHelper;
    private int godownId;
    private String selected_vehicle_number = "";
    private int selected_vehicle_id;
    private ScrollView scrollView;
    private EditText etQuantity;
    private AppCompatButton btnSubmit;
    private EditText etInvoiceNumber;
    private EditText  etErvNumber;
    private Button generateET;
    private LinearLayout myLinearLay;
    private AppCompatButton btnTruckNumber;
    private TextView tvSelectedTruck;
    android.widget.CheckBox chkOneWay;
    private List<String> listSpinItems = new ArrayList<>();
    private int spinItemsCount = -1;
    private String default_str = "--select--";
    private List<EditText> dynamicQuantity = new ArrayList<>();
    private List<EditText> dynamicLostCyl = new ArrayList<>();
    private List<Spinner> dynamicSpinner = new ArrayList<>();
    List<EditText> dynamicDefective = new ArrayList<>();
    private ArrayList<String> arrayDynamicViews = new ArrayList<>();
    private EditText et_defective;
    private ArrayAdapter<String> spinAdapter;
    private ArrayList<String> spinItems;
    private TextInputLayout input_layout_lost;
    private TextInputLayout input_layout_Qty;
    private TextInputLayout input_layout_defective;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_received_own_truck, container, false);
        USER_ID = parseInt(getSharedPrefWithKEY(getContext(), Constants.KEY_USER_ID));
        setGodownId(Integer.parseInt(Constants.getSharedPrefWithKEY(getContext(), Constants.KEY_GODOWN)));
        ButterKnife.bind(getActivity());
        initUI(rootView);
        //setLoad_type("OWN");
        return rootView;
    }

    private final void focusOnView(final View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getBottom());
            }
        });
    }

    private void initUI(View view) {

        scrollView = view.findViewById(R.id.scrollView);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etInvoiceNumber = view.findViewById(R.id.etInvoiceNumber);

        etErvNumber = view.findViewById(R.id.etErvNumber);
        myLinearLay = view.findViewById(R.id.dynamic);
        btnTruckNumber = view.findViewById(R.id.btnTruckNumber);
        tvSelectedTruck = view.findViewById(R.id.tvSelectedTruck);
        generateET = view.findViewById(R.id.generateBtn);

        chkOneWay = (CheckBox) view.findViewById(R.id.chkOneWay);
        chkOneWay.setChecked(false);
        chkOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    etErvNumber.setText("");
                    etErvNumber.setEnabled(false);
                }else{
                    etErvNumber.setEnabled(true);
                }
            }
        });

        applyDynamicViews();
        btnTruckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTruckList();
            }
        });

        submitBtnClick();
    }

    private void showTruckList() {

        RuntimeExceptionDao<VehicleDB, Integer> vehicleDB = getHelper().getVehicleRTExceptionDao();
        final List<VehicleDB> vehicleDBList = vehicleDB.queryForAll();
        int vehSize = vehicleDBList.size();

        if (vehSize > 0) {

            ArrayList<String> listTruckNumber = new ArrayList<>();
            for (VehicleDB truckNum : vehicleDB) {
                listTruckNumber.add(truckNum.vehicle_number);
            }

            final SpinnerDialog dialog = new SpinnerDialog(getActivity(), listTruckNumber, getResources().getString(R.string.select_truck_no));
            dialog.showSpinerDialog();
            dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String truckNumber, int position) {
                    int vehicle_id = vehicleDBList.get(position).vehicle_id;
                    String vehicle_num = vehicleDBList.get(position).vehicle_number;
                    Log.e("" + vehicle_id, vehicle_num);
                    tvSelectedTruck.setText(vehicle_num);
                    tvSelectedTruck.setVisibility(View.VISIBLE);
                    setSelected_vehicle_id(vehicle_id);
                    setSelected_vehicle_number(vehicle_num);

                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    etInvoiceNumber.requestFocus();
                }
            });
        } else {
            Toast.makeText(getContext(), "No Truck Available", Toast.LENGTH_SHORT).show();
        }

    }

    private void submitBtnClick() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = 0;
                arrayDynamicViews.clear();
                RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
                List<ProductDB> productDBList = productDB.queryForAll();
                List<TruckDetailsDB> lstTruckDetailsDB = new ArrayList<>();

                Log.d("dynamicQuantity", Integer.toString(dynamicQuantity.size()));
                final String invoice_number = etInvoiceNumber.getText().toString().trim();
                //random number
                String randomNumber = invoice_number + getGodownId() + AppSettings.currentDateTime()+ AppSettings.getInstance(getActivity()).getRandomNumber();

                for (int j = 0 ; j < dynamicQuantity.size(); j++) {
                    String etText = "";
                    String etLostCyl = "";
                    String etDefective = "";
                    try {
                        etText = String.valueOf(dynamicQuantity.get(j).getText());
                        etLostCyl = String.valueOf(dynamicLostCyl.get(j).getText());
                        etDefective = String.valueOf(dynamicDefective.get(j).getText());
                    }catch (Exception e){

                    }
                    /*Added ZERO by default if validation is missed */
                    if (TextUtils.isEmpty(etText)) {
                        etText = "0";
                    }

                    if (TextUtils.isEmpty(etLostCyl)) {
                        etLostCyl = "0";
                    }

                    if (TextUtils.isEmpty(etDefective)) {
                        etDefective = "0";
                    }

                    String spinner = (String) dynamicSpinner.get(j).getSelectedItem();

                    if(spinner.trim().equalsIgnoreCase(default_str)) {
                        Toast.makeText(getActivity(),"Invalid Product",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int spinnerPosition = spinAdapter.getPosition(spinner);

                    spinnerPosition = --spinnerPosition;
                    //fixing using try catch
                    int spinnerCode = 0;
                    try {
                        spinnerCode = productDBList.get(spinnerPosition).product_code;
                    }catch (Exception e){
                        System.out.println();
                    }
                    //arrayDynamicViews.add(spinnerCode+"|"+etText+"|"+etLostCyl+"~");

                    //Amey
                    int id_product = spinnerCode;
                    int quantity = Integer.parseInt(etText);
                    int lostCylinder = Integer.parseInt(etLostCyl);
                    int defective = Integer.parseInt(etDefective);


                    //String truck_no = etEnterTruckNo.getText().toString().trim();
                    String erv_no = etErvNumber.getText().toString().trim();
                    if(TextUtils.isEmpty(erv_no)){
                        erv_no = "0";
                    }

                    if (invoice_number.equalsIgnoreCase("")) {
                        etInvoiceNumber.requestFocus();
                        etInvoiceNumber.setError("Provide Invoice Number");
                        focusOnView(etInvoiceNumber);
                        return;
                    }else if (Integer.toString(id_product).equalsIgnoreCase("") ){
                        Toast.makeText(getContext(), "Please Add product type", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (etInvoiceNumber.getVisibility() == View.VISIBLE && invoice_number.equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Provide Invoice Number", Toast.LENGTH_SHORT).show();
                        focusOnView(etInvoiceNumber);
                        return;
                    } else if (btnTruckNumber.getVisibility() == View.VISIBLE && selected_vehicle_number.equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Select Truck Number", Toast.LENGTH_SHORT).show();
                        return;
                    } /*else if (etEnterTruckNo.getVisibility() == View.VISIBLE && truck_no.equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Enter Truck Number", Toast.LENGTH_SHORT).show();
                        focusOnView(etEnterTruckNo);
                        etEnterTruckNo.setError("Enter Truck Number");
                        etEnterTruckNo.requestFocus();
                        return;
                    }*/ else if (Integer.toString(quantity).equals("0") || Integer.toString(quantity).equals("00") ||
                            Integer.toString(quantity).equals("000")) {
                        Toast.makeText(getContext(), "Enter Quantity Of Cylinder's", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        if (btnTruckNumber.getVisibility() == View.GONE) {
                            //setSelected_vehicle_number(etEnterTruckNo.getText().toString().toUpperCase());
                        }


                        TruckDetailsDB truckDetailsDB;

                        truckDetailsDB = new TruckDetailsDB();
                        truckDetailsDB.truck_details_id = 1;
                        truckDetailsDB.invoiceNo = invoice_number;
                        truckDetailsDB.invoiceDate = getDateTime();
                        truckDetailsDB.vehicleId = getSelected_vehicle_id();
                        truckDetailsDB.pcoVehicleNo = "";
                        truckDetailsDB.createdBy = String.valueOf(USER_ID);
                        truckDetailsDB.createdDate = getDateTime();
                        truckDetailsDB.idProduct = id_product;
                        truckDetailsDB.typeOfQuery = "INSERT";
                        truckDetailsDB.godownId = getGodownId();
                        truckDetailsDB.is_sync = "N";
                        truckDetailsDB.mode_of_entry = "mobile";
                        truckDetailsDB.deviceId = getDeviceId();
                        truckDetailsDB.Quantity = quantity;
                        truckDetailsDB.LostCylinder = lostCylinder;
                        truckDetailsDB.Defective = defective;
                        truckDetailsDB.ERVNO = erv_no;
                        truckDetailsDB.isOneWay = chkOneWay.isChecked();
                        truckDetailsDB.Purchase_Code = randomNumber;
                        truckDetailsDB.status = "Open";

                        lstTruckDetailsDB.add(truckDetailsDB);
                    }

                    //i++;
                }
                if(lstTruckDetailsDB.size() > 0) {
                    saveReceiveTruck(lstTruckDetailsDB);
                }else{
                    Toast.makeText(getContext(), "Please Add product type", Toast.LENGTH_SHORT).show();
                    return;
                }



                /*String dynamicList = "";
                for (int list=0; list<arrayDynamicViews.size(); list++){
                    dynamicList = dynamicList+arrayDynamicViews.get(list);
                }
                String id_product = "";
                if (dynamicList.length()>0){
                    id_product = dynamicList.substring(0, dynamicList.length() - 1);
                }
*/

            }

        });
    }

    public String getDate() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = formatter.parse(new Date().toString());
            simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }


    private void saveReceiveTruck(final List<TruckDetailsDB> lstTruckDetailsDB) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Entry");
        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
        //final String finalId_product = id_product;
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            RuntimeExceptionDao<TruckDetailsDB, Integer> domesticDB = getHelper().getTruckDetailRTExceptionDao();
                            for (int i = 0; i < lstTruckDetailsDB.size(); i++) {
                                domesticDB.create(lstTruckDetailsDB.get(i));
                            }
                            getActivity().finish();
                            Toast.makeText(getContext(), "Entry saved", Toast.LENGTH_SHORT).show();

                        } catch (SQLException e) {
                            Toast.makeText(getContext(), "Invalid Entry", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void applyDynamicViews() {

        spinItems = new ArrayList<>();
        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> productDBList = productDB.queryForAll();
        int productSize = productDBList.size();

        spinItems.clear();
        if (productSize > 0) {
            for (ProductDB item : productDBList)
                spinItems.add(item.product_description);
        }


        spinItems.add(0,default_str);
        //spinItemsCount = spinItems.size();
        spinAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinItems);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //isQtyEmpty = false;
        generateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listSpinItems.contains(default_str)){
                    Toast.makeText(getActivity(),"Please Enter valid Product Id",Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Spinner spinner1 : dynamicSpinner){
                /*    if (!selectedSpinItems.contains(spinner1.getSelectedItem().toString())) {
                        selectedSpinItems.add(spinner1.getSelectedItem().toString());
                    }
*/
                    spinner1.setClickable(false);
                    spinner1.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
                spinItemsCount++;
                Log.d("incrementcount",Integer.toString(spinItemsCount));
                final View viewToAdd = getActivity().getLayoutInflater().inflate(R.layout.dynemic_truck_layout, null);
                final Button btnDelete = viewToAdd.findViewById(R.id.btnDelete);
                btnDelete.setTag(spinItemsCount);
                final Spinner spinner = viewToAdd.findViewById(R.id.spinner);
                spinner.setTag(spinItemsCount);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String selectedItem = spinner.getSelectedItem().toString();
                        int pos = spinItemsCount;

                        if(listSpinItems.contains(selectedItem)){

                            try {
                                if((listSpinItems.size() - 1) == spinItemsCount) {
                                    listSpinItems.remove(spinItemsCount);
                                }
                            }catch (Exception e){
                                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                            }
                            spinner.setSelection(0);
                            Toast.makeText(getActivity(),"Already Product Selected",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(selectedItem.trim().equalsIgnoreCase(default_str)){


                            if (listSpinItems.contains(selectedItem)) {
                                //pos = spinItemsCount;
                                //pos = --pos;
                                listSpinItems.remove(pos);
                            }else{
                                productsAddRemoveCommon(pos,selectedItem);
                            }
                            return;
                        }
                        /*if(listSpinItems.contains(selectedItem)){
                            Toast.makeText(getActivity(),"cannot select same product type",Toast.LENGTH_SHORT).show();
                            return;
                        }*/else {

                            if(Integer.parseInt(spinner.getTag().toString()) == spinItemsCount){

                                try {
                                    productsAddRemoveCommon(pos,selectedItem);
                                }catch (Exception e ){
                                    listSpinItems.add(pos, selectedItem);
                                }
                            }else{
                                listSpinItems.set(pos, selectedItem);

                            }
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                etQuantity = viewToAdd.findViewById(R.id.et_quantity);
                final EditText etLost = viewToAdd.findViewById(R.id.et_lost);
                et_defective = viewToAdd.findViewById(R.id.et_defective);

                input_layout_Qty = viewToAdd.findViewById(R.id.input_layout_quantity);
                input_layout_lost = viewToAdd.findViewById(R.id.input_layout_lost);
                input_layout_defective  = viewToAdd.findViewById(R.id.input_layout_defective);

                input_layout_Qty.requestFocus();
                focusOnView(input_layout_Qty);


                spinner.setAdapter(spinAdapter);
                dynamicQuantity.add(etQuantity);
                dynamicLostCyl.add(etLost);
                dynamicSpinner.add(spinner);
                dynamicDefective.add((et_defective));


                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = "";
                        Spinner spinner2 = null;
                        RelativeLayout relativeLayout = (RelativeLayout)((Button)view).getParent();
                        if(relativeLayout != null){
                            spinner2 = (Spinner) relativeLayout.findViewById(R.id.spinner);
                            if(spinner2 != null) {
                                text = spinner2.getSelectedItem().toString();
                            }

                            //test Amey
                            RelativeLayout parentLayout = (RelativeLayout)relativeLayout.getParent();
                            if(parentLayout != null) {
                                RelativeLayout relativeLayout1 = (RelativeLayout) parentLayout.findViewById(R.id.leftcontainer);
                                if(relativeLayout1 != null){
                                    LinearLayout linearLayout = (LinearLayout) parentLayout.findViewById(R.id.left_child_container);
                                    if(linearLayout != null) {
                                        etQuantity = (EditText) linearLayout
                                                .findViewById(R.id.et_quantity);
                                    }
                                }
                            }

                        }
                        int pos = (int)btnDelete.getTag();

                        //pos = --pos;

                        spinItemsCount = --spinItemsCount;

                        /*if(view1 != null) {
                             spinner1 = view1.findViewById(R.id.spinner);
                            if(spinner1 != null) {
                                text = spinner1.getSelectedItem().toString();
                            }
                        }*/



                        listSpinItems.remove(text);
                        myLinearLay.removeView(viewToAdd);
                        dynamicQuantity.remove(etQuantity);
                        dynamicLostCyl.remove(etLost);
                        dynamicSpinner.remove(spinner);
                        dynamicDefective.remove(et_defective);

                    }
                });

                //selectedSpinItems.add(spinner.getSelectedItem().toString());
                myLinearLay.addView(viewToAdd);
            }
        });


    }

    void productsAddRemoveCommon(int pos, String selectedItem){
        if(listSpinItems.size() >0) {
            try{
                String str  = listSpinItems.get(pos);
                if(!TextUtils.isEmpty(str)){
                    listSpinItems.set(pos,selectedItem);

                }
            }catch (Exception e){
                listSpinItems.add(pos, selectedItem);
            }

            //selectedSpinItems.remove(pos);
        }else {
            listSpinItems.add(pos, selectedItem);


        }

    }



    private void showHideSegmentedButton() {

    }


    protected String getDeviceId() {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public String getSelected_vehicle_number() {
        return selected_vehicle_number;
    }

    public void setSelected_vehicle_number(String selected_vehicle_number) {
        this.selected_vehicle_number = selected_vehicle_number;
    }

    public int getSelected_vehicle_id() {
        return selected_vehicle_id;
    }

    public void setSelected_vehicle_id(int selected_vehicle_id) {
        this.selected_vehicle_id = selected_vehicle_id;
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


    public int getGodownId() {
        return godownId;
    }

    public void setGodownId(int godownId) {
        this.godownId = godownId;
    }

    public String getDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            date = formatter.parse(new Date().toString());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return simpleDateFormat.format(date);
    }
}
