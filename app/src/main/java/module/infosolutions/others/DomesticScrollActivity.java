package module.infosolutions.others;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infosolutions.evita.R;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;


public class DomesticScrollActivity extends AppCompatActivity {


    private ArrayList<String> listNames = new ArrayList();
    private SpinnerDialog dialog;
    private TextView tvSelectedUser;
    private LinearLayout layout_form, more_views_layout;
    private Button btn_Submit_Form;
    private TextInputLayout input_layout_trip_no, input_layout_full;
    private EditText input_trip_no, input_full;
    private FloatingActionButton fabLoad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domestic_scroll);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        loadToolbar();

        initViewIds();
        loadAllUsers();
        btnClickHandler();
        userValidation();
    }



    private void loadToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Ubuntu-M.ttf");
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(custom_font);
        mTitle.setText(R.string.godown);
        setSupportActionBar(toolbar);

    }




    private void loadAllUsers()
    {
        for (int i=0; i<100; i++) { listNames.add("Evita User "+(i+1)); }
    }


    private void initViewIds(){

        tvSelectedUser = findViewById(R.id.tvSelectedUser);

        layout_form = findViewById(R.id.layout_form);
        more_views_layout = findViewById(R.id.more_views_layout);
        btn_Submit_Form = findViewById(R.id.btn_Submit_Form);
        input_layout_trip_no = findViewById(R.id.input_layout_trip_no);
        input_layout_full = findViewById(R.id.input_layout_full);
        input_trip_no = findViewById(R.id.input_trip_no);
        input_full = findViewById(R.id.input_full);
        fabLoad = findViewById(R.id.fab);


    }


    private void btnClickHandler(){

        dialog = new SpinnerDialog(DomesticScrollActivity.this, listNames,"Select Items");
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(DomesticScrollActivity.this, "Seleted Item: "+item, Toast.LENGTH_SHORT).show();
                String message = item;

                tvSelectedUser.setText(item);
                tvSelectedUser.setVisibility(View.VISIBLE);
                layout_form.setVisibility(View.VISIBLE);


                if (message.equalsIgnoreCase("Evita User 12")){
                    more_views_layout.setVisibility(View.VISIBLE);
                }
            }
        });



        btn_Submit_Form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userValidation();
            }
        });
        fabLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_form.setVisibility(View.GONE);
                tvSelectedUser.setVisibility(View.GONE);
                more_views_layout.setVisibility(View.GONE);
                dialog.showSpinerDialog();
            }
        });

    }


    private void userValidation(){


        if (input_trip_no.getText().toString().trim().equalsIgnoreCase("")){
            input_trip_no.requestFocus();
            input_layout_trip_no.setError("Trip Number can't Leave Blank");

        }else if (input_full.getText().toString().trim().equalsIgnoreCase("")){
            input_full.requestFocus();
            input_layout_full.setError("Full Quantity can't Leave Blank");

        }else {

            String message = input_trip_no.getText().toString().trim();
            Toast.makeText(this, "Trip No: "+message+"\nFull : " +
                    ""+input_full.getText().toString(), Toast.LENGTH_SHORT).show();

        }

    }




}
