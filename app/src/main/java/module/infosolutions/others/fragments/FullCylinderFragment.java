package module.infosolutions.others.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.daasuu.cat.CountAnimationTextView;
import com.infosolutions.evita.R;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * Created by shailesh on 13/7/17.
 */

public class FullCylinderFragment extends Fragment {

    private SpinnerDialog dialog;
    private ArrayList<String> listNames = new ArrayList();
    private Button buttonLoadUsers;
    private CountAnimationTextView mCountAnimationTextView;

    public FullCylinderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fullcylinder, container, false);
        buttonLoadUsers = rootView.findViewById(R.id.buttonLoadData);
        mCountAnimationTextView = rootView.findViewById(R.id.count_animation_textView);

        btnClickHandler();
        return rootView;
    }




    private void loadAllUsers()
    {
        for (int i=0; i<100; i++) { listNames.add("Evita User "+(i+1)); }
    }



    private void btnClickHandler(){

        loadAllUsers();
        dialog = new SpinnerDialog(getActivity(), listNames,"Select Items");
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(getActivity(), "Selected Item: "+item, Toast.LENGTH_SHORT).show();
            }
        });


        buttonLoadUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountAnimationTextView. setAnimationDuration(5000)
                        .countAnimation(0, 9999999);
                dialog.showSpinerDialog();
            }
        });


    }

}
