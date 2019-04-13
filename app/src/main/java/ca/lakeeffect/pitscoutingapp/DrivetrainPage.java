package ca.lakeeffect.pitscoutingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ajay on 9/25/2016.
 */
public class DrivetrainPage extends Fragment {

    static List<TextView> motorTextViews = new ArrayList<>();
    static List<EditText> motorEditTexts = new ArrayList<>();
    static List<Counter> motorAmounts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.drivetrain_page, container, false);

        view.setTag("page1");

        final Spinner motorChooser = view.findViewById(R.id.motorChooser);

        final List<String> motorTypes = Arrays.asList(getResources().getStringArray(R.array.motorTypes));

        final Context context = this.getContext();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                motorTypes);
        motorChooser.setAdapter(adapter);

        System.out.println(motorTypes);
        System.out.println("list of strings");

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.motorTypes, R.layout.spinner);
        //motorChooser.setAdapter(adapter);

        final GridLayout drivetrainPageGrid = view.findViewById(R.id.drivetrainPageGrid);

        motorChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = parent.getSelectedItem().toString();

                System.out.println(pos);
                System.out.println(item);
                System.out.println(MainActivity.retrieveAllItems(motorChooser));
                System.out.println("Position and layout params");

                //if it isn't the first item in the grid or "Other"
                if(pos != 0){
                    //add a row to the gridview
                    drivetrainPageGrid.setRowCount(drivetrainPageGrid.getRowCount() + 1);

                    //find how many children the gridlayout has
                    int childCount = drivetrainPageGrid.getChildCount();

                    //if it's the last item in the spinner or the other item
                    if(pos == MainActivity.retrieveAllItems(motorChooser).size() - 1){
                        // Create an editText view component.
                        EditText editText = new EditText(context);
                        editText.setHint("Motor type");
                        editText.setTextSize(getResources().getDimension(R.dimen.text_small) / 3); //TODO: find out why I need to divide by 3

                        //add it to the lists
                        motorEditTexts.add(editText);
                        //and keep the lists even
                        TextView filler = new TextView(context);
                        filler.setText("Bee");
                        motorTextViews.add(filler);

                        //Add it at the end
                        drivetrainPageGrid.addView(editText, childCount);

                        //Now set the selected item to "Choose one"
                        parent.setSelection(0);
                    }else{
                        //get all the items and put them in a list
                        List<String> newMotorChooserList = MainActivity.retrieveAllItems(motorChooser);
                        //and remove the selected item
                        newMotorChooserList.remove(pos);
                        //then set the spinner to that
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                context,
                                android.R.layout.simple_list_item_1,
                                newMotorChooserList);
                        motorChooser.setAdapter(adapter);

                        //Now create a text view component.
                        TextView textView = new TextView(context);
                        textView.setText(item + ": ");
                        textView.setTextSize(getResources().getDimension(R.dimen.text_small) / 3); //TODO: find out why I need to divide by 3

                        //from https://stackoverflow.com/questions/8049620/how-to-set-layout-gravity-programmatically
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.setGravity(Gravity.CENTER_VERTICAL);
                        textView.setLayoutParams(params);
                        System.out.println(getResources().getDimension(R.dimen.text_small));
                        System.out.println("Text size");

                        //add it to the lists
                        motorEditTexts.add(new EditText(context));
                        //and keep them even
                        motorTextViews.add(textView);

                        //Add it at the end
                        drivetrainPageGrid.addView(textView, childCount);
                    }

                    //Recalculate gridlayout child count again.
                    childCount = drivetrainPageGrid.getChildCount();
                    //Add the counter
                    Counter counter = new Counter(context, null);
                    counter.max = 999999;

                    //add to list of counters
                    motorAmounts.add(counter);

                    drivetrainPageGrid.addView(counter, childCount);
                    drivetrainPageGrid.invalidate();
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

                FROM https://www.dev2qa.com/android-gridlayout-example-programmatically/
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

    public static List<Integer> getMotors(){
        List<Integer> motors = new ArrayList<>();
        for (Counter counter: motorAmounts) {
            motors.add(counter.count);
        }
        return motors;
    }

    public static List<String> getMotorNames(){
        List<String> motors = new ArrayList<>();
        for(int i = 0; i < motorTextViews.size(); i++){
            if(motorTextViews.get(i).getText() == "Bee"){
                motors.add(motorEditTexts.get(i).getText().toString());
            }else{
                motors.add(motorTextViews.get(i).getText().toString());
            }
        }
        return motors;
    }
}
