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
import android.widget.Toast;

import com.infosolutions.evita.R;

import java.util.ArrayList;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * Created by shailesh on 10/8/17.
 */

public class CommercialFragment extends Fragment {

    private LinearLayout layout_fresh;
    private LinearLayout layout_return;
    private EditText et_FreshTripno, et_FreshCylnrType;
    private EditText et_returnTripno, et_returnCylnrType, et_returnEmptyClynr;
    private EditText et_returnSV,et_returnDefective,et_returnDBC,et_returnFull;
    private Button btnFreshSubmit,  btnReturnSubmit, btnDeliveryMan;
    private String SELECTED_DELIVERY_MAN;

    public CommercialFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_commercial,container,false);
        layout_fresh = rootView.findViewById(R.id.layout_fresh);
        layout_return = rootView.findViewById(R.id.layout_return);
        btnDeliveryMan = rootView.findViewById(R.id.btnDeliveryMan);

        switchShowHideLayout(rootView);
        return rootView;
    }


    private void switchShowHideLayout(final View rootView){


        SegmentedButtonGroup segmentedButtonGroup = rootView.findViewById(R.id.segmentedButtonGroup);
        segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(getContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();

                if (position == 0){
                    initReturnLayout(rootView);
                }else {
                    initFreshLayout(rootView);
                }

            }
        });


    }


    private void initFreshLayout(View rootview)
    {
        layout_fresh.setVisibility(View.VISIBLE);
        layout_return.setVisibility(View.GONE);

        et_FreshTripno = rootview.findViewById(R.id.etFreshTripno);
        et_FreshCylnrType = rootview.findViewById(R.id.etFreshCylnrType);
        btnFreshSubmit = rootview.findViewById(R.id.btnFreshSubmit);

        loadDeliveryMan();

        btnFreshSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeliveryMan();
                if (et_FreshTripno.getText().toString().trim().equalsIgnoreCase("")){
                    et_FreshTripno.requestFocus();
                    et_FreshTripno.setError(getResources().getString(R.string.enter_trip_number));
                } else if (et_FreshCylnrType.getText().toString().trim().equalsIgnoreCase("")){
                    et_FreshCylnrType.requestFocus();
                    et_FreshCylnrType.setError(getResources().getString(R.string.enter_cylnr_type));
                }else {

                    Toast.makeText(getContext(), "Call Network", Toast.LENGTH_SHORT).show();
                    //Go For Network Call
                    /**
                     * apiRequest();
                     * */
                }
            }
        });

    }




    private void initReturnLayout(View rootview)
    {
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


        loadDeliveryMan();
        btnReturnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeliveryMan();
                if (et_returnTripno.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnTripno.requestFocus();
                    et_returnTripno.setError(getResources().getString(R.string.enter_trip_number));
                } else if (et_returnCylnrType.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnCylnrType.requestFocus();
                    et_returnCylnrType.setError(getResources().getString(R.string.enter_cylnr_type));
                }else if (et_returnEmptyClynr.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnEmptyClynr.requestFocus();
                    et_returnEmptyClynr.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                }else if (et_returnSV.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnSV.requestFocus();
                    et_returnSV.setError(getResources().getString(R.string.enter_sv));
                }else if (et_returnDefective.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnDefective.requestFocus();
                    et_returnDefective.setError(getResources().getString(R.string.enter_defective));
                }else if (et_returnDBC.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnDBC.requestFocus();
                    et_returnDBC.setError(getResources().getString(R.string.enter_bc));
                }else if (et_returnFull.getText().toString().trim().equalsIgnoreCase("")){
                    et_returnFull.requestFocus();
                    et_returnFull.setError(getResources().getString(R.string.enter_return_empty_cylndr));
                }else {
                    Toast.makeText(getContext(), "Call Network", Toast.LENGTH_SHORT).show();
                    //Go For Network Call  apiRequest();
                }
            }
        });
    }



    private void loadDeliveryMan(){

        btnDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> listNames = new ArrayList();
                for (int i=0; i<5; i++) { listNames.add("Delivery Man: "+(i+1)); }

                final SpinnerDialog dialog = new SpinnerDialog(getActivity(), listNames,"Select Delivery Man");
                dialog.showSpinerDialog();

                dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String selectedUser, int position) {
                        SELECTED_DELIVERY_MAN = "";
                        Toast.makeText(getContext(), "Selected Item: "+selectedUser, Toast.LENGTH_SHORT).show();
                        setDeliveryMan(selectedUser);
                        //tvSelectedUser.setText(item);
                        //tvSelectedUser.setVisibility(View.VISIBLE);
                        //layout_form.setVisibility(View.VISIBLE);

                    }
                });
            }
        });

    }



    private String setDeliveryMan(String selectedDeliveryMan){
        SELECTED_DELIVERY_MAN = selectedDeliveryMan;
        return SELECTED_DELIVERY_MAN;
    }

    private String getDeliveryMan(){
        return SELECTED_DELIVERY_MAN;
    }

}
