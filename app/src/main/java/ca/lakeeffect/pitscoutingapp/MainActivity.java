package ca.lakeeffect.pitscoutingapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends ListeningActitivty {
    TextView robotNumText; //robotnum and matchNumber

    int robotNum = 2708;
    int robotWeight;
    String robotUnits;

    //only used if the schedule is being overridden
    String scoutName = "";
    //view that contains the scout name if the schedule is overridden
    EditText overriddenScoutName;


    InputPagerAdapter pagerAdapter;
    ViewPager viewPager;

    boolean connected;

    String savedLabels = null; //generated at the beginning

    //the last time submit has been pressed
    //used to see if "are you still here" messages should be placed
    public static long lastSubmit = -1;

    //toast displayed in the alert panel for errors when typing certain match numbers
    Toast matchNumAlertToast = null;

    //if the schedule has been overridden
    boolean overrideSchedule;

    //if the scout has confirmed that the robot has no starting object
    boolean noStartingObject;

    //the id of the user currently scouting. This decides when they must switch on and off from scouting
    int userID = -1;
    int matchNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //set version code
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //check what theme is selected and set it as the theme
        SharedPreferences prefs1 = getSharedPreferences("theme", MODE_PRIVATE);
        switch (prefs1.getInt("theme", 0)) {
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                setTheme(R.style.AppThemeLight);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call alert (asking scout name and robot number)
        alert(false);

        loadUnsentData();

        //set device name
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        ((TextView) findViewById(R.id.deviceNameLayout).findViewById(R.id.deviceName)).setText(ba.getName()); //if this method ends up not working refer to https://stackoverflow.com/a/6662271/1985387

        //set pending messages number on ui
        ((TextView) findViewById(R.id.numberOfPendingMessagesLayout).findViewById(R.id.numberOfPendingMessages)).setText(unsentData.size() + "");

        //setup scrolling viewpager
        viewPager = findViewById(R.id.scrollingview);
        pagerAdapter = new InputPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.PAGENUM);

        robotNumText = findViewById(R.id.robotNum);

        robotNumText.setText("Robot: " + robotNum);

        loadSchedule();

        //update the UI if necessary
        if (userIDSpinner != null) {
            updateUserIDSpinner();
        }
    }

    public void restartListenerThread(){
        stopListenerThread();

        startListenerThread();
    }

    public void stopListenerThread() {
        if (listenerThread != null) {
            if(listenerThread.connectionThreadThreadClass != null){
                try {
                    listenerThread.connectionThreadThreadClass.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else{
                if(listenerThreadThreadClass != null) {
                    try {
                        listenerThreadThreadClass.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startListenerThread() {
        if (savedLabels == null){
            savedLabels = getData(true)[1];
            SharedPreferences prefs = getSharedPreferences("savedLabels", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("savedLabels", savedLabels);
            editor.putInt("versionNumber", BuildConfig.VERSION_CODE);
            editor.apply();
        }

        //start listening
        if (listenerThread == null) {
            listenerThread = new ListenerThread(this);
            listenerThreadThreadClass = new Thread(listenerThread);
            listenerThreadThreadClass.start();
        }
    }

    StringBuilder data;
    StringBuilder labels;

    public void addItems(StringBuilder input, List<String> append){
        for(String string : append){
            input.append(string + ",");
        }
    }

    public void addIntItems(StringBuilder input, List<Integer> ints){
        for(Integer integer : ints){
            input.append(integer + ",");
        }
    }

    public String[] getData(boolean bypassChecks) {
        if (!bypassChecks) {
            /*
            Example

            if (((Spinner) pagerAdapter.pregamePage.getView().findViewById(R.id.autoStartPlatform)).getSelectedItem().toString().equals("Choose One")) {
                runOnUiThread(new Thread() {
                    public void run() {
                        new Toast(MainActivity.this).makeText(MainActivity.this, "You forgot to specify which platform it started on!", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            */


            /*
            Another example

            //check if the robot is starting with hatch or cargo
            if (!noStartingObject && !((CheckBox) pagerAdapter.pregamePage.getView().findViewById(R.id.startingObjectsHatch)).isChecked()
                    && !((CheckBox) pagerAdapter.pregamePage.getView().findViewById(R.id.startingObjectsCargo)).isChecked()) {
                //double check the user meant this
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("No starting Object?")
                        .setMessage("Are you sure the robot started with no object?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                noStartingObject = true;
                                pagerAdapter.qualitativePage.getView().findViewById(R.id.submit).performClick();
                            }
                        })
                        .setNegativeButton("No, let me go change that", null)
                        .create()
                        .show();
                return null;
            }
            */
        }

        data = new StringBuilder();
        labels = new StringBuilder();

        data.append(robotNum + ",");
        labels.append("Robot,");

        labels.append("Date and Time,");
        DateFormat dateFormat = new SimpleDateFormat("dd HH;mm;ss");
        Date date = new Date();
        data.append(dateFormat.format(date) + ",");

        labels.append("Weight,");
        data.append(robotWeight + ",");

        labels.append("Units,");
        data.append(robotUnits + ",");

        PercentRelativeLayout layout;

        //Drivetrain page
        layout = pagerAdapter.drivetrainPage.getView().findViewById(R.id.drivetrainPageLayout);
        enterLayout(layout);

        addItems(labels, DrivetrainPage.getMotorNames());
        addIntItems(data, DrivetrainPage.getMotors());

        //Cargo page
        layout = pagerAdapter.cargoPage.getView().findViewById(R.id.cargoHatchPageLayout);
        enterLayout(layout);

        //Hatch page
        layout = pagerAdapter.hatchPage.getView().findViewById(R.id.cargoHatchPageLayout);
        enterLayout(layout);

        //Sandstorm Page
        layout = pagerAdapter.sandstormPage.getView().findViewById(R.id.sandstormPageLayout);
        enterLayout(layout);

        for(int i = 1; i<SandstormPage.getAutos().size() + 1; i++){
            labels.append("Auto " + i + ",");
        }
        addItems(data, SandstormPage.getAutos());

        //Climb Page
        layout = pagerAdapter.climbingPage.getView().findViewById(R.id.climbingPageLayout);
        enterLayout(layout);

        //Strategy Page
        layout = pagerAdapter.strategyPage.getView().findViewById(R.id.strategyPageLayout);
        enterLayout(layout);

        labels.append("Scout Name,");
        if (userID >= 0) {
            data.append(schedules.get(userID).userName);
        } else {
            data.append(scoutName);
        }

        //Add UUID
        labels.append("UUID,\n");
        data.append("," + UUID.randomUUID() + "\n");

        System.out.println(labels.toString());
        System.out.println(data.toString());
        byte[] dataBytes = data.toString().getBytes(Charset.forName("UTF-8"));
        byte[] labelsBytes = labels.toString().getBytes(Charset.forName("UTF-8"));
        String dataBase64 = Base64.encodeToString(dataBytes, Base64.DEFAULT);
        String labelsBase64 = Base64.encodeToString(labelsBytes, Base64.DEFAULT);

        String[] out = {dataBase64, labelsBase64};

        return out;
    }

    void enterLayout(ViewGroup top) {
        //Iterate over all child layouts
        for (int i = 0; i < top.getChildCount(); i++) {
            View v = top.getChildAt(i);
            if(!getName(v).equals("NULL")){
                if (v instanceof EditText) {
                    data.append(clean(((EditText) v).getText().toString()) + ",");
                    labels.append(getName(v) + ",");
                }
                if (v instanceof CheckBox) {
                    data.append(((CheckBox) v).isChecked() + ",");
                    labels.append(getName(v) + ",");
                }
                if (v instanceof Counter) {
                    data.append(((Counter) v).count + ",");
                    labels.append(getName(v) + ",");
                }
                if (v instanceof HigherCounter) {
                    data.append(((HigherCounter) v).count + ",");
                    labels.append(getName(v) + ",");
                }
                if (v instanceof SeekBar) {
                    data.append(((SeekBar) v).getProgress() + ",");
//                    System.out.println(getName(v));
                    labels.append(getName(v) + ",");
                }
                if (v instanceof Spinner) {
                    data.append(((Spinner) v).getSelectedItem().toString() + ",");
                    System.out.println(((Spinner) v).getSelectedItem().toString() + ",");
                    labels.append(getName(v) + ",");
                }
                if (v instanceof RadioButton) {
                    data.append(((RadioButton) v).isChecked() + ",");
                    labels.append(getName(v) + ",");
                }
            }
            //If the child is a layout, enter it
            else if (v instanceof ViewGroup) {
                enterLayout((ViewGroup) v);
            }
        }
    }

    //Caps => spaces then letter
    //First letter capital

    String getName(View v) {
        if (v == null || v.getId()==-1) return "NULL";
        String id = getResources().getResourceEntryName(v.getId());
        String out = id.substring(0, 1).toUpperCase() + id.substring(1);
        for (int i = 1; i < out.length(); i++) {
            if (Character.isUpperCase(out.charAt(i))) {
                out = out.substring(0, i) + " " + out.substring(i);
                i++;
            }
        }
        return out;
    }


    public boolean saveData() {
        File sdCard = Environment.getExternalStorageDirectory();
//        File dir = new File (sdCard.getPath() + "/ScoutingData/");

        File file = new File(sdCard.getPath() + "/#ScoutingData/" + robotNum + ".csv");

        try {

            boolean newfile = false;
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                newfile = true;
            }

            FileOutputStream f = new FileOutputStream(file, true);

            OutputStreamWriter out = new OutputStreamWriter(f);
            String[] data = getData(false);
            if (data == null) {
                return false;
            }

            //save to file
            if (newfile) out.append(new String(Base64.decode(data[1], Base64.DEFAULT), Charset.forName("UTF-8")));
            out.append(new String(Base64.decode(data[0], Base64.DEFAULT), Charset.forName("UTF-8")));

            String fulldata = "";
            fulldata = robotNum + ":" + data[0];


            //encode to base64
            fulldata = Base64.encodeToString(fulldata.getBytes(Charset.forName("UTF-8")), Base64.DEFAULT);

            //add to pending messages
            unsentData.add(fulldata);
            //add to sharedprefs
            SharedPreferences prefs = getSharedPreferences("pendingMessages", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("message" + prefs.getInt("messageAmount", 0), fulldata);
            editor.putInt("messageAmount", prefs.getInt("messageAmount", 0) + 1);
            editor.apply();

            //set pending messages number on ui
            ((TextView) findViewById(R.id.numberOfPendingMessagesLayout).findViewById(R.id.numberOfPendingMessages)).setText(unsentData.size() + "");

            out.close();

            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void waitForConfirmation(final StringBuilder labels, final StringBuilder data) {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    byte[] bytes = new byte[1000];
                    try {
                        if (!connected) {
                            unsentData.add(robotNum + ":" + labels.toString() + ":" + data.toString());
                            SharedPreferences prefs = getSharedPreferences("pendingMessages", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("message" + prefs.getInt("messageAmount", 0), robotNum + ":" + labels.toString() + ":" + data.toString());
                            editor.putInt("messageAmount", prefs.getInt("messageAmount", 0) + 1);
                            editor.apply();
                            return;
                        }
                        int amount = listenerThread.in.read(bytes);
                        if (new String(bytes, Charset.forName("UTF-8")).equals("done")) {
                            return;
                        }
                        if (!connected) {
                            unsentData.add(robotNum + ":" + labels.toString() + ":" + data.toString());
                            SharedPreferences prefs = getSharedPreferences("pendingMessages", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("message" + prefs.getInt("messageAmount", 0), robotNum + ":" + labels.toString() + ":" + data.toString());
                            editor.putInt("messageAmount", prefs.getInt("messageAmount", 0) + 1);
                            editor.apply();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    Toast.makeText(MainActivity.this, "The app has to save items to the external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            System.out.println("Showing menu");
            PopupMenu menu = new PopupMenu(MainActivity.this, findViewById(R.id.deviceNameLayout), Gravity.CENTER_HORIZONTAL);
            menu.getMenuInflater().inflate(R.menu.more_options, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.reset) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Confirm")
                                .setMessage("Continuing will reset current data.")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        reset(false);

                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                    if (item.getItemId() == R.id.changeNum) {
                        alert(false);
                    }
                    if (item.getItemId() == R.id.resetPendingMessages) {
                        for(int i = 0; i< unsentData.size(); i++){
                            unsentData.remove(i);
                        }

                        SharedPreferences prefs = getSharedPreferences("pendingMessages", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("messageAmount", 0);
                        editor.apply();

                        //set pending messages number on ui
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.numberOfPendingMessagesLayout).findViewById(R.id.numberOfPendingMessages)).setText(unsentData.size() + "");
                            }
                        });
                    }

                    if (item.getItemId() == R.id.changeTheme) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Confirm")
                                .setMessage("Continuing will reset current data.")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }

                    if (item.getItemId() == R.id.restartBluetooth) {
                        restartListenerThread();
                    }

                    if (item.getItemId() == R.id.stopBluetooth) {
                        stopListenerThread();
                    }

                    Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            menu.show();
        }
        return;
    }

    public void reset(boolean incrementMatchNumber) {
        //setup scrolling viewpager
        alert(incrementMatchNumber);

        viewPager.setAdapter(pagerAdapter);

        PercentRelativeLayout layout;

        //Drivetrain page
        layout = pagerAdapter.drivetrainPage.getView().findViewById(R.id.drivetrainPageLayout);
        clearData(layout);

        //Cargo hatch page
        layout = pagerAdapter.cargoPage.getView().findViewById(R.id.cargoHatchPageLayout);
        clearData(layout);

        //Cargo hatch page
        layout = pagerAdapter.hatchPage.getView().findViewById(R.id.cargoHatchPageLayout);
        clearData(layout);

        //Sandstorm page
        layout = pagerAdapter.sandstormPage.getView().findViewById(R.id.sandstormPageLayout);
        clearData(layout);

        //Climb page
        layout = pagerAdapter.climbingPage.getView().findViewById(R.id.climbingPageLayout);
        clearData(layout);

        //Strategy page
        layout = pagerAdapter.strategyPage.getView().findViewById(R.id.strategyPageLayout);
        clearData(layout);

        noStartingObject = false;
    }

    public void clearData(ViewGroup top) {
        for (int i = 0; i < top.getChildCount(); i++) {
            View v = top.getChildAt(i);
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            }
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
                v.jumpDrawablesToCurrentState();
            }
            if (v instanceof RadioGroup) {
                ((RadioGroup) v).clearCheck();
            }
            if (v instanceof RatingBar) {
                ((RatingBar) v).setRating(0);
            }
            if (v instanceof SeekBar) {
                SeekBar seekBar = ((SeekBar) v);
                seekBar.setProgress(seekBar.getMax()/2);
            }
            if (v instanceof Spinner) {
                ((Spinner) v).setSelection(0);
            }
            if (v instanceof ViewGroup) {
                clearData((ViewGroup) v);
            }
        }
    }

    public void alert(final boolean incrementMatchNumber) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog)
                .setTitle("Enter Info")
                .setPositiveButton(android.R.string.yes, null)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                //get date details
                final int year = Calendar.getInstance().get(Calendar.YEAR);
                final int month = Calendar.getInstance().get(Calendar.MONTH);
                final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                //setup spinners (Drop downs)
                final GridLayout gridLayout = ((AlertDialog) dialog).findViewById(R.id.dialogGridLayout);

                final Spinner units = ((AlertDialog) dialog).findViewById(R.id.units);
                ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.units, R.layout.spinner);
                units.setAdapter(unitAdapter);

                //List user names available
                userIDSpinner = gridLayout.findViewById(R.id.userID);

                //set a listener to make sure to adjust other fields based on it will change as well
                userIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        dialogScheduleDataChange(userIDSpinner, dialog);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                updateUserIDSpinner();

                //set to previous value
                SharedPreferences prefs = getSharedPreferences("userID", MODE_PRIVATE);
                userIDSpinner.setSelection(prefs.getInt("userID", -1) + 1);

                //start bluetooth, all views are probably ready now
                startListenerThread();

                overriddenScoutName = new EditText(MainActivity.this);
                overriddenScoutName.setHint("Scout Name");
                //Add column span property
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(0, 2);
                overriddenScoutName.setLayoutParams(params);

                //if the schedule has already been overridden, set it to overridden
                if (overrideSchedule) {
                    gridLayout.findViewById(R.id.robotNumber).setEnabled(true);

                    gridLayout.findViewById(R.id.userID).setEnabled(false);
                    ((Spinner) gridLayout.findViewById(R.id.userID)).setSelection(0);
                    gridLayout.addView(overriddenScoutName, 4);
                }

                if (BuildConfig.DEBUG) {
                    ((EditText) gridLayout.findViewById(R.id.robotNumber)).setText("2809");
                    overriddenScoutName.setText("Debug Scout Name Build " + BuildConfig.VERSION_NAME);
                }

                //make it so that you can override the schedule if you need to
                gridLayout.findViewById(R.id.robotNumber).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Override schedule")
                                .setMessage("Would you like to override the schedule and manually choose a robot to scout. ONLY do this if you " +
                                        "are testing or being given instruction to do this.\n\n" +
                                        "WARNING: This could be dangerous!\n\n" +
                                        "Note: This will still get automatically set if you change the match number, it will just " +
                                        "allow you to edit it.")
                                .setPositiveButton("I would like to manually choose a robot number", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        gridLayout.findViewById(R.id.robotNumber).setEnabled(true);

                                        gridLayout.findViewById(R.id.userID).setEnabled(false);
                                        ((Spinner) gridLayout.findViewById(R.id.userID)).setSelection(0);

                                        gridLayout.addView(overriddenScoutName, 4);

                                        overrideSchedule = true;
                                    }
                                })
                                .setNegativeButton("No, keep using the schedule", null)
                                .create()
                                .show();
                        return false;
                    }
                });

                //once they hit ok
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickOkButton(dialog, false);
                    }
                });

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //reset userIDSpinner to null since the alert has been closed
                userIDSpinner = null;
            }
        });

        dialog.show();
    }

    //When the user ID is changed by the userID spinner changing or when the match number is changed
    public void dialogScheduleDataChange(Spinner userIDSpinner, DialogInterface dialog) {
        int newUserID = userIDSpinner.getSelectedItemPosition() - 1;

        //it's moved to the default, no need to change anything
        if(newUserID == -1) {
            return;
        }

        //cancel previous error toast if there is one
        if (matchNumAlertToast != null) matchNumAlertToast.cancel();

        //has it been 15 minutes
        if (newUserID != userID && (lastSubmit == -1 || System.currentTimeMillis() - lastSubmit > 900000) && userID != -1) {
            //make a confirmation message here
            AlertDialog confirmationDialog = new AlertDialog.Builder(this)
                    .setTitle("Are you still " + schedules.get(MainActivity.this.userID).userName + "?")
                    .setMessage("If you are not " + schedules.get(MainActivity.this.userID).userName + ", make sure to change the user.\n\n" +
                            "Make sure the match number is accurate as well")
                    .setPositiveButton(android.R.string.yes, null)
                    .setCancelable(true)
                    .create();

            lastSubmit = System.currentTimeMillis();
        }

        //change other buttons on the dialog box accordingly
        View gridLayout = ((AlertDialog) dialog).findViewById(R.id.dialogGridLayout);


        //set userID
        userID = newUserID;

        EditText robotNumInput = gridLayout.findViewById(R.id.robotNumber);
    }

    //when the ok button on the alert is pressed
    public void onClickOkButton(DialogInterface dialog, boolean overrideRobotNumberCheck){
        //get date details
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH);
        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);

        View gridLayout = ((AlertDialog) dialog).findViewById(R.id.dialogGridLayout);

        EditText robotNumInput = gridLayout.findViewById(R.id.robotNumber);
        EditText robotWeightInput = gridLayout.findViewById(R.id.robotWeight);

        //spinners
        Spinner userID = gridLayout.findViewById(R.id.userID);
        Spinner robotWeightUnits = gridLayout.findViewById(R.id.units);

        //buttons
        Button addPhoto = gridLayout.findViewById(R.id.addPhoto);

        //gridLayouts
        GridLayout photoGrid = gridLayout.findViewById(R.id.photoGrid);

        try {
            robotNum = Integer.parseInt(robotNumInput.getText().toString());
            robotWeight = Integer.parseInt(robotWeightInput.getText().toString());
            robotUnits = robotWeightUnits.getSelectedItem().toString();

            SharedPreferences prefs = getSharedPreferences("userID", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userID", userID.getSelectedItemPosition() - 1);
            editor.apply();

            scoutName = overriddenScoutName.getText().toString();

            if (userID.getSelectedItemPosition() == 0 && !overrideSchedule) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Please choose a user",
                                Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

        } catch (NumberFormatException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Invalid Data! Are any fields blank?",
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        robotNumText = findViewById(R.id.robotNum);
        robotNumText.setText("Robot: " + robotNum );

        updateMatchesLeft();

        dialog.dismiss();

    }

    //updates the view showing the matches left until this scout is off
    public void updateMatchesLeft() {
        int nextMatchOff = getNextMatchOff();
        int matchesLeft = nextMatchOff - matchNumber;

        if (matchesLeftText != null) {
            if (nextMatchOff == -1) {
                matchesLeftText.setText("Never");
            } else {
                matchesLeftText.setText(matchesLeft + "");
            }
        }
    }

    //short form functions from the functions in ListeningActivity
    public int getNextMatchOff() {
        return getNextMatchOff(matchNumber, userID);
    }

    public int getNextMatchOn() {
        return getNextMatchOn(matchNumber, userID);
    }

    public static boolean arrayContains(String[] array, String search){
        for(String string : array){
            //hi
            if(string.equals(search)){
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(int[] array, int search){
        for(int num : array){
            if(num == search){
                return true;
            }
        }
        return false;
    }

    public static String clean(String input){
        return input.replace("|", "||").replace(",", "|c")
                .replace("\n", "|n").replace("\"", "|q").replace(":", ";")
                .replace("{", "|ob").replace("}", "|cb");
    }

    //From https://stackoverflow.com/questions/32127374/android-how-to-get-all-items-in-a-spinner
    public static List<String> retrieveAllItems(Spinner theSpinner) {
        Adapter adapter = theSpinner.getAdapter();
        int n = adapter.getCount();
        List<String> strings = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) {
            String string = (String) adapter.getItem(i);
            strings.add(string);
        }
        return strings;
    }

     public static void startNotificationAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent (context, PendingNotification.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Date date = Calendar.getInstance().getTime();
        System.out.println(date.toString());
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*60*20, pending);
    }

}
