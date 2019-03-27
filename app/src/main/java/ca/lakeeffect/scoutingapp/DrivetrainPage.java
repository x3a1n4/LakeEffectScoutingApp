package ca.lakeeffect.scoutingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Ajay on 9/25/2016.
 */
public class DrivetrainPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.drivetrain_page, container, false);

        view.setTag("page1");

        Spinner motorTypes = view.findViewById(R.id.motorTypes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.motorTypes, R.layout.spinner);
        motorTypes.setAdapter(adapter);

        final GridLayout drivetrainPageGrid = view.findViewById(R.id.drivetrainPageGrid);

        final TextView motorTitle = view.findViewById(R.id.motorTitle);
        final ca.lakeeffect.scoutingapp.Counter motorCount = view.findViewById(R.id.motorCount);


        motorTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                System.out.println(pos);
                System.out.println(motorCount.getLayoutParams());
                System.out.println("Position and layout params");

                if(pos != 0){
                    drivetrainPageGrid.setRowCount(drivetrainPageGrid.getRowCount() + 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText drivetrainDescription = view.findViewById(R.id.drivetrainDescription);

        drivetrainDescription.addTextChangedListener(new TextWatcher() {
            @Override
            //This makes it so that the width doesn't change and will automatically create new lines
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(drivetrainDescription.getWidth());
                drivetrainDescription.setWidth(drivetrainDescription.getWidth());
                System.out.println("Above is the width");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;

    }
}
