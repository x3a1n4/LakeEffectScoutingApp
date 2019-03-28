package ca.lakeeffect.scoutingapp;

import android.content.Context;
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

                /*
                // Used to save exist password element count.
                int existPasswordRowNumber = 0;

                // Get all GridLayout child count.
                int childCount = gridLayout.getChildCount();

                // Loop all the GridLayout child.
                for(int i=0;i<childCount;i++)
                {
                    // If one child is EditText type.
                    Object child = gridLayout.getChildAt(i);
                    if(child instanceof EditText)
                    {
                        // Get it's input type.
                        EditText editText = (EditText)child;
                        int inputType = editText.getInputType();

                        // If the edit text input type is password. The real value is 129 which is InputType.TYPE_TEXT_VARIATION_PASSWORD + 1.
                        if(inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD + 1)
                        {
                            // Exist password row number plus 1.
                            existPasswordRowNumber++;
                        }
                    }
                }

                // Get application context.
                Context context = getApplicationContext();

                // Create a text view component.
                TextView passwordTextView = new TextView(context);
                // Set password label text value.
                passwordTextView.setText("Password " + existPasswordRowNumber + ":");

                // Create a edit text component.
                EditText passwordEditText = new EditText(context);
                // Set edit text input type to password.
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD + 1);
                // Set edit text size to 10 ems.
                passwordEditText.setEms(10);

                // Insert password label at last third position.
                gridLayout.addView(passwordTextView, childCount - 2);

                // Recalculate gridlayout child count again.
                childCount = gridLayout.getChildCount();
                // Insert password input box at last third position again.
                gridLayout.addView(passwordEditText, childCount - 2);

                FROM file:///C:/Users/Xan/AppData/Local/Microsoft/Windows/INetCache/IE/I3LIY8HA/Android_Gridlayout_Example_Programmatically[1].mhtml
                */
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
