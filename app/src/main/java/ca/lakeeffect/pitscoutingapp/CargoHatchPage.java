package ca.lakeeffect.pitscoutingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ajay on 9/25/2016.
 */
public class CargoHatchPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.cargo_hatch_page, container, false);

        view.setTag("page2");
        view.setTag("page3");

        final SeekBar[] seekBars = {
                view.findViewById(R.id.lowerRocket),
                view.findViewById(R.id.middleRocket),
                view.findViewById(R.id.highRocket),
                view.findViewById(R.id.cargoShip)
        };

        final TextView[] percentages = {
                view.findViewById(R.id.lowerRocketPercentage),
                view.findViewById(R.id.middleRocketPercentage),
                view.findViewById(R.id.highRocketPercentage),
                view.findViewById(R.id.cargoShipPercentage)
        };

        for(int i=0; i<seekBars.length; i++){
            final int cur = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int pos, boolean b) {
                    int i = cur;
                    if(pos < 5){
                        seekBar.setProgress(0);
                        percentages[i].setText("Can't place here");
                    }else{
                        percentages[i].setText(pos + "%");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }


        final RadioButton reachesOverBumberNo =  view.findViewById(R.id.reachesOverBumberNo);

        final RadioButton reachesOverBumberYes =  view.findViewById(R.id.reachesOverBumberYes);

        reachesOverBumberNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    reachesOverBumberYes.setChecked(false);
                }
            }
        });

        reachesOverBumberYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    reachesOverBumberNo.setChecked(false);
                }
            }
        });

        return view;
    }
}
