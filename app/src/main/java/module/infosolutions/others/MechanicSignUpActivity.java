package module.infosolutions.others;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.infosolutions.ui.MainActivity;
import com.infosolutions.evita.R;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;


public class MechanicSignUpActivity extends
        AppCompatActivity implements ResponseListener {

    private EditText  input_consumer_no, input_consumer_name, input_address, input_contact_no;
    private TextInputLayout input_layout_consumer_No, input_layout_consumer_name, input_layout_address, input_layout_contact_no ;
    private Button btn_login, btnComplaintType;
    private ProgressBar progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mechanic_signup);
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        mTitle.setText(R.string.signup_mechanical_title);
        setSupportActionBar(toolbar);

        initIds();
    }


    private void initIds()
    {
        progressView = findViewById(R.id.progress_bar);
        input_layout_consumer_No = findViewById(R.id.input_layout_consumer_No);
        input_layout_consumer_name = findViewById(R.id.input_layout_consumer_name);
        input_layout_address = findViewById(R.id.input_layout_address);
        input_layout_contact_no = findViewById(R.id.input_layout_contact_no);


        input_consumer_no = findViewById(R.id.input_consumer_no);
        input_consumer_name = findViewById(R.id.input_consumer_name);
        input_address = findViewById(R.id.input_address);
        input_contact_no = findViewById(R.id.input_contact_no);

        btn_login = findViewById(R.id.btn_login);
        btnComplaintType = findViewById(R.id.btnComplaintType);


        VolleySingleton.getInstance(getApplicationContext())
                .addResponseListener(VolleySingleton.CallType.USER_LOGIN, this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MechNotificationActivity.class);
                startActivity(intent);
            }
        });

    }





    private void showProgressBar() {
        progressView.setVisibility(View.GONE);
    }
    private void dismissProgressBar() {
        progressView.setVisibility(View.GONE);
    }

    /*private boolean validateEmail() {

        String email = input_consumer_no.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email))
        {
            inputLayoutUserId.setError(getString(R.string.err_msg_email));
            requestFocus(inputUserId);
            return false;
        } else {
            inputLayoutUserId.setErrorEnabled(false);
        }

        return true;
    }*/

/*    private boolean validatePassword()
    {
        if (inputPassword.getText().toString().trim().isEmpty()) {

            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }*/

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response) {
        dismissProgressBar();
        Log.d("response:", response);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(VolleySingleton.CallType type, VolleyError error) {
        dismissProgressBar();
        Toast.makeText(this, "Server is not responding", Toast.LENGTH_SHORT).show();
    }

    /*private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_userId:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }*/

}
