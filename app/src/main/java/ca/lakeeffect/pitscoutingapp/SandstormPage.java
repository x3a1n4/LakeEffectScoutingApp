package ca.lakeeffect.pitscoutingapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ajay on 9/25/2016.
 */
public class SandstormPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.sandstorm_page, container, false);

        view.setTag("page4");

        CheckBox[] checkboxes = {
                view.findViewById(R.id.autoSandstormButton),
                view.findViewById(R.id.driveSandstormButton),
                view.findViewById(R.id.highPlatformSandstorm),
                view.findViewById(R.id.lowPlatformSandstorm),
                view.findViewById(R.id.cargoPreloadSandstorm),
                view.findViewById(R.id.hatchPreloadSandstorm)
        };

        for(int i=0; i<checkboxes.length; i+=2){
            final CheckBox first = checkboxes[i];
            final CheckBox second = checkboxes[i+1];
            first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        second.setChecked(false);
                    }
                }
            });
            second.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        first.setChecked(false);
                    }
                }
            });
        }

        final GridLayout autoGrid = view.findViewById(R.id.autoGrid);
        final Button addAuto = view.findViewById(R.id.addAuto);
        final Context context = this.getContext();

        addAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childCount = autoGrid.getChildCount();

                int autoNum = (childCount - 1) / 2 + 1;
                TextView autoTitle = new TextView(context);
                autoTitle.setText("Auto " + autoNum + ":");
                autoTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_small)); //TODO: find out why I need to divide by 3
                autoTitle.setPadding(0, 10, 10, 10);
                autoTitle.setTypeface(null, Typeface.BOLD);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setGravity(Gravity.CENTER_VERTICAL);
                params.setMargins(10, 0, 0, 0);
                autoTitle.setLayoutParams(params);

                autoGrid.addView(autoTitle, childCount);

                EditText autoDesc = new EditText(context);
                autoDesc.setHint("Describe auto");
                //TypeValue.px or something
                autoDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_small)); //TODO: find out why I need to divide by 3
                params = new GridLayout.LayoutParams();
                params.setGravity(Gravity.FILL_HORIZONTAL);
                autoDesc.setLayoutParams(params);

                childCount = autoGrid.getChildCount();
                autoGrid.addView(autoDesc, childCount);


                autoDesc.setWidth(autoDesc.getWidth());
            }
        });



        return view;

    }
}
