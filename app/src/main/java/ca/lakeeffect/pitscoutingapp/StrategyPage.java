package ca.lakeeffect.pitscoutingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ajay on 9/25/2016.
 */
public class StrategyPage extends Fragment implements View.OnClickListener{

    Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {

        View view = inflator.inflate(R.layout.strategy_page, container, false);

        view.setTag("page6");

        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        return view;

    }

    public void onClick(View v) {
        //If the submit button is pressed
        if (v == submit) {
            //save the time
            MainActivity.lastSubmit = System.currentTimeMillis();

            //Confirm Dialog
            MainActivity.startNotificationAlarm(getContext());
            new AlertDialog.Builder(getActivity())
                    .setTitle("Submitting")
                    .setMessage("Are you sure you would like to submit?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(),
                                    "Keep scouting then...", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Save the data
                            if (((MainActivity) getActivity()).saveData()) {
                                Toast.makeText(getActivity(),
                                        "Saved", Toast.LENGTH_LONG).show();
                                //Reset the inputs
                                ((MainActivity) getActivity()).reset(true);
                            }

                        }
                    })
                    .create()
                    .show();
        }
    }
}
