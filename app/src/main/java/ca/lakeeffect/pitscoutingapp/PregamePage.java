package ca.lakeeffect.pitscoutingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Ajay on 9/25/2016.
 */
public class PregamePage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.pregame_page, container, false);

        view.setTag("page6");

        Spinner spinner = view.findViewById(R.id.autoStartLocation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.startPosition, R.layout.spinner);
        spinner.setAdapter(adapter);

        spinner = view.findViewById(R.id.autoStartPlatform);
        adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.startPlatform, R.layout.spinner);
        spinner.setAdapter(adapter);

        final CheckBox cargo = (CheckBox) view.findViewById(R.id.startingObjectsCargo);

        final CheckBox hatch = (CheckBox) view.findViewById(R.id.startingObjectsHatch);

        cargo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    hatch.setChecked(false);
                }
            }
        });

        hatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    cargo.setChecked(false);
                }
            }
        });

        return view;

    }
}
