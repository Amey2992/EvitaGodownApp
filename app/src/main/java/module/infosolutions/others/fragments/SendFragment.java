package module.infosolutions.others.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infosolutions.evita.R;

import ren.qinc.numberbutton.NumberButton;


public class SendFragment extends Fragment {


    public SendFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_truck,container,false);
        TextView view14Kg = rootView.findViewById(R.id.tv14Kg);
        TextView view19Kg = rootView.findViewById(R.id.tv19Kg);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Ubuntu-M.ttf");
        view14Kg.setTypeface(custom_font);
        view19Kg.setTypeface(custom_font);
        numberButton(rootView);
        return rootView;
    }



    private void numberButton(View rootView){
        //Click Listner
        NumberButton numberButton14KgSound = rootView.findViewById(R.id.nb14Saound);
        numberButton14KgSound.setBuyMax(20).setInventory(50).setCurrentNumber(0).setOnWarnListener(new NumberButton.OnWarnListener(){
            @Override
            public void onWarningForInventory(int inventory) {

            }

            @Override
            public void onWarningForBuyMax(int max) {

            }
        });

        NumberButton numberButton14KgDeffective = rootView.findViewById(R.id.nb14Deffective);
        numberButton14KgDeffective.setBuyMax(20).setInventory(50).setCurrentNumber(0).setOnWarnListener(new NumberButton.OnWarnListener(){
            @Override
            public void onWarningForInventory(int inventory) {

            }

            @Override
            public void onWarningForBuyMax(int max) {

            }
        });



        NumberButton numberButton19KgSound = rootView.findViewById(R.id.nb19Sound);
        numberButton19KgSound.setBuyMax(20).setInventory(50).setCurrentNumber(0).setOnWarnListener(new NumberButton.OnWarnListener(){
            @Override
            public void onWarningForInventory(int inventory) {

            }

            @Override
            public void onWarningForBuyMax(int max) {

            }
        });


        NumberButton numberButton19KgDeffective = rootView.findViewById(R.id.nb19Deffective);
        numberButton19KgDeffective.setBuyMax(20).setInventory(50).setCurrentNumber(0).setOnWarnListener(new NumberButton.OnWarnListener(){
            @Override
            public void onWarningForInventory(int inventory) {

            }

            @Override
            public void onWarningForBuyMax(int max) {

            }
        });
    }

}
