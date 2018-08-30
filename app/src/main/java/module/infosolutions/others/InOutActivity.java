package module.infosolutions.others;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.infosolutions.evita.R;
import com.infosolutions.ui.user.tvdetails.TVDetailsActivity;

import at.markushi.ui.CircleButton;



public class InOutActivity extends AppCompatActivity {

    private CircleButton cardViewIN;
    private CircleButton cardViewOUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out);
        loadToolbar();
        cardViewIN = findViewById(R.id.btnIN);
        cardViewOUT = findViewById(R.id.btnOUT);

        btnClickListner();
    }



    private void loadToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.inword_outword);
        setSupportActionBar(toolbar);

    }



    private void btnClickListner() {

        cardViewIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });


        cardViewOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TVDetailsActivity.class);
                startActivity(intent);
            }
        });


    }


}
