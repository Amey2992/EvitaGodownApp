package module.infosolutions.others.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.evita.R;

import org.json.JSONObject;

import java.util.ArrayList;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * Created @author Shailesh Mishra on 10/8/17.
 */

public class DomesticFragment5KG extends Fragment {


    private Typeface custom_font;
    private LinearLayout layout_fresh;
    private LinearLayout layout_return;
    private SegmentedButtonGroup segmentedButtonGroup;
    private TextView tvSelectedUser;
    private EditText et_FreshTripno, et_Full_Cylndr;
    private EditText et_returnTripno, et_returnCylnrType, et_returnEmptyClynr;
    private EditText et_returnSV, et_returnDefective, et_returnDBC, et_returnFull;
    private Button btnFreshSubmit, btnReturnSubmit;
    private Button btnDeliveryMan;
    private String SELECTED_DELIVERY_MAN;
    private String TAG = DomesticFragment5KG.class.getSimpleName();
    private ProgressBar progressBar;

    private String selectedDeliveryMan;

    private int productId;
    public DomesticFragment5KG(){

    }


    public String getSelectedDeliveryMan() {
        return selectedDeliveryMan;
    }

    public void setSelectedDeliveryMan(String selectedDeliveryMan) {
        this.selectedDeliveryMan = selectedDeliveryMan;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_domestic, container, false);

        initUi(rootView);
        getAndSetProductId();
        switchShowHideLayout(rootView);

        return rootView;
    }



    private void getAndSetProductId() {


    }


    private void initUi(View rootView) {

        custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-M.ttf");
        layout_fresh = rootView.findViewById(R.id.layout_fresh);
        layout_return = rootView.findViewById(R.id.layout_return);
        btnDeliveryMan = rootView.findViewById(R.id.btnDeliveryMan);
        progressBar = rootView.findViewById(R.id.progressBar);
        segmentedButtonGroup = rootView.findViewById(R.id.segmentedButtonGroup);
        segmentedButtonGroup.setVisibility(View.GONE);
        tvSelectedUser = rootView.findViewById(R.id.tvSelectedUser);
        tvSelectedUser.setTypeface(custom_font);


        //String logcat = productId+" "+description+" "+category+" "+taxCategory+" "+productCode+" "+isActive;
        //Log.d(TAG, logcat);

        btnDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSelectedUser.setVisibility(View.INVISIBLE);
                ArrayList<String> listNames = new ArrayList();
                for (int i = 0; i < 5; i++) {
                    listNames.add("Delivery Man: " + (i + 1));
                }

                final SpinnerDialog dialog = new SpinnerDialog(getActivity(), listNames, "Select Delivery Man");
                dialog.showSpinerDialog();

                dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String selectedDeliveryMan, int position) {
                        setSelectedDeliveryMan(selectedDeliveryMan);

                        tvSelectedUser.setText(selectedDeliveryMan);
                        tvSelectedUser.setVisibility(View.VISIBLE);
                        segmentedButtonGroup.setVisibility(View.VISIBLE);

                    }
                }
                );

            }
        });

    }


    private void switchShowHideLayout(final View rootView) {


        segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(getContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    initFreshLayout(rootView);
                } else {
                    initReturnLayout(rootView);
                }

            }
        });


    }


    private void initFreshLayout(View rootView) {
        layout_fresh.setVisibility(View.VISIBLE);
        layout_return.setVisibility(View.GONE);

        et_FreshTripno = rootView.findViewById(R.id.etFreshTripno);
        et_Full_Cylndr = rootView.findViewById(R.id.etFreshCylnrType);
        btnFreshSubmit = rootView.findViewById(R.id.btnFreshSubmit);

        et_FreshTripno.setTypeface(custom_font);
        et_Full_Cylndr.setTypeface(custom_font);
        btnFreshSubmit.setTypeface(custom_font);

        btnFreshSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tripNo = et_FreshTripno.getText().toString().trim();

                if (et_FreshTripno.getText().toString().trim().equalsIgnoreCase("")) {
                    et_FreshTripno.requestFocus();
                    et_FreshTripno.setError(getResources().getString(R.string.enter_trip_number));
                } else if (et_Full_Cylndr.getText().toString().trim().equalsIgnoreCase("")) {
                    et_Full_Cylndr.requestFocus();
                    et_Full_Cylndr.setError(getResources().getString(R.string.enter_cylnr_type));
                } else {

                    /*db_delivery.addRecords(new DB_ESS_DOMESTIC_DELIVERY_MODEL("emp_id", tripNo , getProductId() ,
                            Constants.getCurrentDateTime(), getSelectedDeliveryMan(),
                            "empty_received","sv","dbc","defective",
                            "return_full","receive_time","received_by","Mobile", "insert", "N"));
*/

                }
            }
        });

    }


    private void initReturnLayout(View rootview) {
        layout_return.setVisibility(View.VISIBLE);
        layout_fresh.setVisibility(View.GONE);

        et_returnTripno = rootview.findViewById(R.id.etReturnTripno);
        et_returnCylnrType = rootview.findViewById(R.id.etReturnCylnrType);
        et_returnEmptyClynr = rootview.findViewById(R.id.etReturnEmptyClynr);
        et_returnSV = rootview.findViewById(R.id.etReturnSV);
        et_returnDefective = rootview.findViewById(R.id.etReturnDefective);
        et_returnDBC = rootview.findViewById(R.id.etReturnDBC);
        et_returnFull = rootview.findViewById(R.id.etReturnFull);
        btnReturnSubmit = rootview.findViewById(R.id.btnReturnSubmit);

        et_returnTripno.setTypeface(custom_font);
        et_returnCylnrType.setTypeface(custom_font);
        et_returnEmptyClynr.setTypeface(custom_font);
        et_returnSV.setTypeface(custom_font);
        et_returnDefective.setTypeface(custom_font);
        et_returnDBC.setTypeface(custom_font);
        et_returnFull.setTypeface(custom_font);
        btnReturnSubmit.setTypeface(custom_font);


        btnReturnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_returnTripno.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnTripno.requestFocus();
                    et_returnTripno.setError(getResources().getString(R.string.enter_trip_number));
                } else if (et_returnCylnrType.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnCylnrType.requestFocus();
                    et_returnCylnrType.setError(getResources().getString(R.string.enter_cylnr_type));
                } else if (et_returnEmptyClynr.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnEmptyClynr.requestFocus();
                    et_returnEmptyClynr.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else if (et_returnSV.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnSV.requestFocus();
                    et_returnSV.setError(getResources().getString(R.string.enter_sv));
                } else if (et_returnDefective.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnDefective.requestFocus();
                    et_returnDefective.setError(getResources().getString(R.string.enter_defective));
                } else if (et_returnDBC.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnDBC.requestFocus();
                    et_returnDBC.setError(getResources().getString(R.string.enter_bc));
                } else if (et_returnFull.getText().toString().trim().equalsIgnoreCase("")) {
                    et_returnFull.requestFocus();
                    et_returnFull.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                } else {


                    progressBar.setVisibility(View.VISIBLE);
                    final JSONObject objectJSON = new JSONObject();
                    //String {"ESS_DOMESTIC_DELIVERY":[{"Given_Time":"2017-08-20%2001:02:03",
                    // "Emp_Id":123,"Trip_No":1,"typeOfQuery":"INSERT","Mode_of_entry":"Mobile"}]}

                    /*db_delivery.updateDomesticInReturn(new DB_ESS_DOMESTIC_DELIVERY_MODEL("emp_Id","trip_no",
                            "id_product","given_time","given_by",
                            "empty_received","sv","dbc","defective","return_full",
                            "receive_time","received_by","mode_of_entry", "Y"));*/

                }
            }
        });
    }


    private String setDeliveryMan(String selectedDeliveryMan) {
        SELECTED_DELIVERY_MAN = selectedDeliveryMan;
        return SELECTED_DELIVERY_MAN;
    }

    private String getDeliveryMan() {
        return SELECTED_DELIVERY_MAN;
    }

    public void setProductID(int productID) {
        this.productId = productID;
    }

    private String getProductId(){
        return String.valueOf(this.productId);
    }

}
