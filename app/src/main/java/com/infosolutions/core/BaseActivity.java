package com.infosolutions.core;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;

import com.infosolutions.customviews.EvitaProgressDialog;
import com.infosolutions.customviews.EvitaSnackbar;
import com.infosolutions.evita.R;
import com.infosolutions.network.Constants;
import com.infosolutions.network.ResponseListener;
import com.irozon.sneaker.Sneaker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

/**
 * Created by Noman Khan on 12/09/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements ResponseListener {

    public BaseActivity context;
    private EvitaProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        if (getLayoutId() != 0) setContentView(getLayoutId());
        ButterKnife.bind(this);
        injectDependency();
    }


    public abstract void injectDependency();

    public abstract int getLayoutId();

    public void showProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    return;
                }
            }
            dialog = new EvitaProgressDialog(getContext());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
        }

    }

    public void hideDialog(){
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.hide();
        }
    }

    public void showDialog(){
        if (dialog != null) {
            if (!dialog.isShowing())
                dialog.show();
        }
    }

    public boolean isProgressDialogShown() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this);
    }

    public void showToast(String message) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.custom_toast,(ViewGroup) findViewById(R.id.layout_toast));
        TextView tvToastMsg = layout.findViewById(R.id.toastView);
        tvToastMsg.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
    }


    public void showSnackBar(String message) {
        View viewById = findViewById(android.R.id.content);
        if (viewById != null){
            EvitaSnackbar.make(viewById, message, EvitaSnackbar.LENGTH_LONG).show();
        }
    }


    public void showSuccessToast(Activity mContext, String mTitle, String message){
        Sneaker.with(mContext).setTitle(mTitle).setMessage(message).sneakSuccess();
    }

    public void showErrorToast(Activity mContext, String mTitle, String message){
        Sneaker.with(mContext).setTitle(mTitle).setMessage(message).setHeight(ViewGroup.LayoutParams.WRAP_CONTENT).sneakError();
    }

    public void showWarningToast(Activity mContext, String mTitle, String message){
        Sneaker.with(mContext).setTitle(mTitle).setMessage(message).sneakWarning();
    }


    public void focusOnView(final ScrollView scrollView, final EditText editText){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, editText.getTop());
            }
        });
    }


    protected void showAlert(){

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Entry");
        alertDialog.setMessage(getResources().getString(R.string.proceed_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SAVE",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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


  protected int getTextFromET(EditText editText){
        return parseInt(editText.getText().toString().trim());
    }

    protected String getTextString(EditText editText){
        return editText.getText().toString().trim();
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



    public String getDateTime() {

        SimpleDateFormat simpleDateFormat = null;
        Date date = null;
        String dt = null;
        try {
            simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            dt = simpleDateFormat.format(Calendar.getInstance().getTime());
            Log.i("FORDATE",simpleDateFormat.format(Calendar.getInstance().getTime()));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return  dt;
    }


    public String  getPreferences(String sharedPrefKey){

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        String defaultValue = getApplicationContext().getResources().getString(R.string.sp_default_value);
        String resultModule = sharedPref.getString( sharedPrefKey, defaultValue);
        return resultModule;
    }



    protected void savePreferences(String sharedPrefKey, String sharedPrefValue) {

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(sharedPrefKey, sharedPrefValue);
        editor.commit();
    }

    protected int getGoDownId(){
        String godown_Id = getPreferences(Constants.KEY_GODOWN);
        return Integer.parseInt(godown_Id);
    }

    protected int getApplicationUserId(){
        String userId = getPreferences(Constants.KEY_USER_ID);
        return Integer.parseInt(userId);
    }


    protected String getDeviceId(){
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public Context getContext() {
        return this;
    }

    public RecyclerView.LayoutManager getGridLayoutManager() {
        return new GridLayoutManager(this, 3);
    }

    public RecyclerView.LayoutManager getStaggeredGridLayout() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    public RecyclerView.LayoutManager getLinearLayoutManagerHorizontal() {
        return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    }

}
