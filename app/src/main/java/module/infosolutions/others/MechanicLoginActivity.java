package module.infosolutions.others;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.infosolutions.network.VolleySingleton;

import java.util.HashMap;
import java.util.Map;


public class MechanicLoginActivity extends AppCompatActivity implements ResponseListener {

    private EditText  inputUserId, inputPassword;
    private TextInputLayout inputLayoutUserId, inputLayoutPassword;
    private Button btnLogin;
    private ProgressBar progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mechanic_login);
        if (Build.VERSION.SDK_INT >= 21){ getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));}

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.mechanic_login);
        setSupportActionBar(toolbar);

        initIds();
    }


    private void initIds(){

        inputLayoutUserId = findViewById(R.id.input_layout_userId);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputUserId = findViewById(R.id.input_userId);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        progressView = findViewById(R.id.progress_bar);


        //Delete This Block//
        inputUserId.setText("evita@gmail.com");
        inputPassword.setText("12345");

        VolleySingleton.getInstance(getApplicationContext()).
                addResponseListener(VolleySingleton.CallType.USER_LOGIN, this);
        inputUserId.addTextChangedListener(new MyTextWatcher(inputUserId));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        final Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), MechanicSignUpActivity.class);
               startActivity(intent);
            }
        });

    }


    private void submitForm() {

        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        showProgressBar();

        String textusername = inputUserId.getText().toString().trim();
        String textpassword = inputPassword.getText().toString().trim();

        progressView.setVisibility(View.VISIBLE);
        Map<String, String> mapKeyValuePair = new HashMap<>();
        mapKeyValuePair.put("X_API_KEY","123456");
        mapKeyValuePair.put("email",textusername);
        mapKeyValuePair.put("password",textpassword);

        VolleySingleton.getInstance(getApplicationContext()).
                networkRequestAPI(VolleySingleton.CallType.USER_LOGIN, Constants.EVITA_API_URL, textusername, textpassword);

    }


    private void showProgressBar() {
        progressView.setVisibility(View.GONE);
    }

    private void dismissProgressBar() {
        progressView.setVisibility(View.GONE);
    }


    private boolean validateEmail() {
        String email = inputUserId.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutUserId.setError(getString(R.string.err_msg_email));
            requestFocus(inputUserId);
            return false;
        } else {
            inputLayoutUserId.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword()
    {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onSuccess(VolleySingleton.CallType type, String response)
    {
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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable)
        {
            switch (view.getId()) {

                case R.id.input_userId:
                    validateEmail();
                    break;

                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

}
