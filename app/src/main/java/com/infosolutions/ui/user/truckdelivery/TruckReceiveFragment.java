package com.infosolutions.ui.user.truckdelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.database.DatabaseHelper;
import com.infosolutions.database.ProductDB;
import com.infosolutions.database.TruckDetailsDB;
import com.infosolutions.database.TruckSendDetailsDB;
import com.infosolutions.database.VehicleDB;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
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


public class TruckReceiveFragment extends Fragment {

    private String TAG = TruckDeliveryActivity.class.getSimpleName();
    private AppCompatButton btnSubmit;
    private EditText etInvoiceNumber;
    private EditText etEnterTruckNo;
    private AppCompatButton btnTruckNumber;
    private TextView tvSelectedTruck;
    private SegmentedButtonGroup segmentedButtonGroup;
    private List<EditText> dynamicQuantity = new ArrayList<>();
    private List<EditText> dynamicLostCyl = new ArrayList<>();
    private List<Spinner> dynamicSpinner = new ArrayList<>();
    private ArrayList<String> arrayDynamicViews = new ArrayList<>();
    private TextInputLayout input_layout_lost;
    private TextInputLayout input_layout_Qty;
    private Button generateET;
    private LinearLayout myLinearLay;
    private String selected_vehicle_number = "";
    private int selected_vehicle_id;
    private DatabaseHelper databaseHelper = null;
    private int USER_ID;
    private ArrayAdapter<String> spinAdapter;
    private boolean isQtyEmpty = false;
    private int godownId;

    public String getLoad_type() {
        return load_type;
    }

    public void setLoad_type(String load_type) {
        this.load_type = load_type;
    }

    private String load_type;
    private ScrollView scrollView;
    private EditText etQuantity;

    public TruckReceiveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_received_truck, container, false);
        USER_ID = parseInt(getSharedPrefWithKEY(getContext(), Constants.KEY_USER_ID));
        setGodownId(Integer.parseInt(Constants.getSharedPrefWithKEY(getContext(), Constants.KEY_GODOWN)));
        ButterKnife.bind(getActivity());
        initUI(rootView);
        setLoad_type("OWN");
        return rootView;
    }

    private void initUI(View view) {

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        btnSubmit = (AppCompatButton) view.findViewById(R.id.btnSubmit);
        etInvoiceNumber = (EditText) view.findViewById(R.id.etInvoiceNumber);
        etEnterTruckNo = (EditText) view.findViewById(R.id.etEnterTruckNo);
        myLinearLay = (LinearLayout) view.findViewById(R.id.dynamic);
        btnTruckNumber = (AppCompatButton) view.findViewById(R.id.btnTruckNumber);
        tvSelectedTruck = (TextView) view.findViewById(R.id.tvSelectedTruck);
        segmentedButtonGroup = (SegmentedButtonGroup) view.findViewById(R.id.segmentedButtonGroup);
        generateET = (Button) view.findViewById(R.id.generateBtn);
        btnTruckNumber.setVisibility(View.GONE);
        etEnterTruckNo.setVisibility(View.GONE);


        showHideSegmentedButton();
        submitBtnClick();
    }


    private void showHideSegmentedButton() {

        segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {

                switch (position) {

                    case 0:
                        setLoad_type("OWN");
                        btnTruckNumber.setVisibility(View.VISIBLE);
                        etEnterTruckNo.setVisibility(View.GONE);
                        etEnterTruckNo.setText("");
                        focusOnView(etInvoiceNumber);
                        break;
                    case 1:
                        setLoad_type("PCO");
                        btnTruckNumber.setVisibility(View.GONE);
                        etEnterTruckNo.setVisibility(View.VISIBLE);
                        etEnterTruckNo.requestFocus();
                        tvSelectedTruck.setText("");
                        tvSelectedTruck.setVisibility(View.GONE);
                        focusOnView(etEnterTruckNo);
                        break;
                }
            }
        });
        segmentedButtonGroup.setPosition(0, 0);
        applyDynamicViews();
        btnTruckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTruckList();
            }
        });
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

            final SpinnerDialog dialog = new SpinnerDialog(getActivity(), listTruckNumber, getResources().getString(R.string.select_deliveryman));
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


    private void applyDynamicViews() {

        List<String> spinItems = new ArrayList<>();
        RuntimeExceptionDao<ProductDB, Integer> productDB = getHelper().getProductRTExceptionDao();
        List<ProductDB> productDBList = productDB.queryForAll();
        int productSize = productDBList.size();

        spinItems.clear();
        if (productSize > 0) {
            for (ProductDB item : productDBList)
                spinItems.add(item.product_description);
        }
        spinAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinItems);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //isQtyEmpty = false;
        generateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View viewToAdd = getActivity().getLayoutInflater().inflate(R.layout.dynemic_truck_layout, null);
                Button btnDelete = (Button) viewToAdd.findViewById(R.id.btnDelete);
                final Spinner spinner = (Spinner) viewToAdd.findViewById(R.id.spinner);
                etQuantity = (EditText) viewToAdd.findViewById(R.id.et_quantity);
                final EditText etLost = (EditText) viewToAdd.findViewById(R.id.et_lost);

                input_layout_Qty = (TextInputLayout) viewToAdd.findViewById(R.id.input_layout_quantity);
                input_layout_lost = (TextInputLayout) viewToAdd.findViewById(R.id.input_layout_lost);

                input_layout_Qty.requestFocus();
                focusOnView(input_layout_Qty);


                spinner.setAdapter(spinAdapter);
                dynamicQuantity.add(etQuantity);
                dynamicLostCyl.add(etLost);
                dynamicSpinner.add(spinner);


                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myLinearLay.removeView(viewToAdd);
                        dynamicQuantity.remove(etQuantity);
                        dynamicLostCyl.remove(etLost);
                        dynamicSpinner.remove(spinner);
                    }
                });
                myLinearLay.addView(viewToAdd);
            }
        });


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

                while (i < dynamicQuantity.size()) {

                    String etText = String.valueOf(dynamicQuantity.get(i).getText());
                    String etLostCyl = String.valueOf(dynamicLostCyl.get(i).getText());

                    /*Added ZERO by default if validation is missed */
                    if (TextUtils.isEmpty(etText)) {
                        etText = "0";
                    }

                    if (TextUtils.isEmpty(etLostCyl)) {
                        etLostCyl = "0";
                    }


                    String spinner = (String) dynamicSpinner.get(i).getSelectedItem();
                    int spinnerPosition = spinAdapter.getPosition(spinner);
                    int spinnerCode = productDBList.get(spinnerPosition).product_code;

                    //arrayDynamicViews.add(spinnerCode+"|"+etText+"|"+etLostCyl+"~");

                    //Amey
                    int id_product = spinnerCode;
                    int quantity = Integer.parseInt(etText);
                    int lostCylinder = Integer.parseInt(etLostCyl);

                    final String invoice_number = etInvoiceNumber.getText().toString().trim();
                    String truck_no = etEnterTruckNo.getText().toString().trim();


                    if (invoice_number.equalsIgnoreCase("")) {
                        etInvoiceNumber.requestFocus();
                        etInvoiceNumber.setError("Provide Invoice Number");
                        focusOnView(etInvoiceNumber);
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
                    } else if (etEnterTruckNo.getVisibility() == View.VISIBLE && truck_no.equalsIgnoreCase("")) {
                        Toast.makeText(getContext(), "Enter Truck Number", Toast.LENGTH_SHORT).show();
                        focusOnView(etEnterTruckNo);
                        etEnterTruckNo.setError("Enter Truck Number");
                        etEnterTruckNo.requestFocus();
                        return;
                    } else if (etQuantity.getText().toString().equals("0") || etQuantity.getText().toString().equals("00") ||
                            etQuantity.getText().toString().equals("000")) {
                        Toast.makeText(getContext(), "Enter Quantity Of Cylinder's", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        if (btnTruckNumber.getVisibility() == View.GONE) {
                            setSelected_vehicle_number(etEnterTruckNo.getText().toString().toUpperCase());
                        }


                        TruckDetailsDB truckDetailsDB;
                        if (getLoad_type().toString().equalsIgnoreCase("OWN")) {

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


                        } else {

                            truckDetailsDB = new TruckDetailsDB();
                            truckDetailsDB.truck_details_id = 1;
                            truckDetailsDB.invoiceNo = invoice_number;
                            truckDetailsDB.invoiceDate = getDateTime();
                            truckDetailsDB.vehicleId = 0;
                            truckDetailsDB.pcoVehicleNo = getSelected_vehicle_number();
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
                        }

                        lstTruckDetailsDB.add(truckDetailsDB);
                    }

                    i++;
                }
                if(lstTruckDetailsDB.size() > 0) {
                    saveReceiveTruck(lstTruckDetailsDB);
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
            date = (Date) formatter.parse(new Date().toString());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return simpleDateFormat.format(date);
    }


    private final void focusOnView(final View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getBottom());
            }
        });
    }


}