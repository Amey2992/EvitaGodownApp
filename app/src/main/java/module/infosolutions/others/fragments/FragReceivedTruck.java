package module.infosolutions.others.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;

import java.util.ArrayList;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;


/**
 * Created by shailesh on 8/8/17.
 */

public class FragReceivedTruck extends Fragment {

    private LinearLayout form_layout;
    private Typeface custom_font;
    private TextView viewMessage;
    private EditText etInvoiceNumber;
    private EditText etNoOfCylnr, etCylnrType;
    private Button btnSubmit, btnLoadDeliveryMan;
    private Constants constants;
    private int TAG_OPERATION_MODE_SELECTED ;

    public FragReceivedTruck() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_received_truck,container,false);

        //initUI(rootView);
        return rootView;
    }

    /*private void initUI(View rootView) {

        custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Ubuntu-M.ttf");
        viewMessage = (TextView) rootView.findViewById(R.id.tvMessage);
        viewMessage.setText(getResources().getString(R.string.received_truck));
        form_layout = (LinearLayout) rootView.findViewById(R.id.layout_input);
        //btnLoadDeliveryMan = (Button) rootView.findViewById(R.id.btnLoadDeliveryMan);
        btnLoadDeliveryMan.setTypeface(custom_font);
        viewMessage.setTypeface(custom_font);
        constants= new Constants();

        if (constants.getStockFlagCount() > 0)
        {
            form_layout.setVisibility(View.VISIBLE);
        }else {
            viewMessage.setText(" Could not operate Received Truck");
            form_layout.setVisibility(View.GONE);
        }


        etInvoiceNumber = (EditText) rootView.findViewById(R.id.etInvoiceNumber);
        etNoOfCylnr = (EditText) rootView.findViewById(R.id.etNoOfCylnr);
        etCylnrType = (EditText) rootView.findViewById(R.id.etCylnrType);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        etInvoiceNumber.setTypeface(custom_font);
        etNoOfCylnr.setTypeface(custom_font);
        etCylnrType.setTypeface(custom_font);
        btnSubmit.setTypeface(custom_font);


        // Set Selected Change Listener
        SegmentedButtonGroup segmentedButtonGroup = (SegmentedButtonGroup) rootView.findViewById(R.id.segmentedButtonGroup);
        segmentedButtonGroup.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(getContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();

                if (position == 0){
                    TAG_OPERATION_MODE_SELECTED = 0;
                }else {
                    TAG_OPERATION_MODE_SELECTED = 1;
                }
            }
        });


        //Load Deliveryman
        btnLoadDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> listNames = new ArrayList();
                for (int i=0; i<5; i++) { listNames.add("MH-62 A 133"+(i+1)); }

                final SpinnerDialog dialog = new SpinnerDialog(getActivity(), listNames,"Select Items");
                dialog.showSpinerDialog();

                dialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        Toast.makeText(getContext(), "Selected Item: "+item, Toast.LENGTH_SHORT).show();

                        //tvSelectedUser.setText(item);
                        //tvSelectedUser.setVisibility(View.VISIBLE);
                        //layout_form.setVisibility(View.VISIBLE);

                    }
                });
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }*/






}
