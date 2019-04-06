package ca.lakeeffect.scoutingapp;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ajay on 9/25/2016.
 */
public class ClimbingPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.climbing_page, container, false);

        view.setTag("page5");

        final CheckBox[] sidePreferences = {
                view.findViewById(R.id.leftSide),
                view.findViewById(R.id.bothSides),
                view.findViewById(R.id.rightSide)
        };

        for(int i = 0; i < sidePreferences.length; i++){
            final int curr = i;
            sidePreferences[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        for(int i = 0; i < sidePreferences.length; i++){
                            if(i != curr){
                                sidePreferences[i].setChecked(false);
                            }
                        }
                    }
                }
            });
        }

        final TextView[] consistencies = {
                view.findViewById(R.id.middlePlatformConsistency),
                view.findViewById(R.id.highPlatformConsistency)
        };

        SeekBar[] seekBars = {
                view.findViewById(R.id.middlePlatform),
                view.findViewById(R.id.highPlatform)
        };

        for(int i = 0; i<seekBars.length; i++){
            final int curr = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    final TextView textView = consistencies[curr];
                    if(progress < 5){
                        seekBar.setProgress(0);
                        textView.setText("Can't climb here");
                    }else{
                        textView.setText("Consistency (" + progress + "%) :");
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

        return view;

    }
}
