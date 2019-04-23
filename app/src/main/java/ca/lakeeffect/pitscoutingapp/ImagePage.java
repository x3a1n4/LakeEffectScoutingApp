package ca.lakeeffect.pitscoutingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ajay on 9/25/2016.
 */
public class ImagePage extends Fragment{

    private Button takePictureButton;
    private Uri file;
    private GridLayout imageGrid;
    private static Integer picNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        View view = inflator.inflate(R.layout.image_page, container, false);

        view.setTag("page6");

        //reset num of pics
        picNum = 0;

        takePictureButton = view.findViewById(R.id.addPhoto);
        imageGrid = view.findViewById(R.id.imageGrid);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //From https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi
                if(takePictureButton.isEnabled()){
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    takePicture(view);
                }

            }
        });

        return view;


    }

    //From https://androidkennel.org/android-camera-access-tutorial/

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "#PitScoutingData");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("#PitScoutingData", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file;
        if(picNum == 0){
            file = new File(mediaStorageDir.getPath() + File.separator +
                    MainActivity.robotNum + ".PNG");
        }else{
            file = new File(mediaStorageDir.getPath() + File.separator +
                    MainActivity.robotNum + "_" + picNum + ".PNG");
        }

        picNum++;
        return file;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                ImageView imageView = new ImageView(this.getContext());
                imageView.setImageURI(file);

                //add image
                imageGrid.addView(imageView);

                //disable button
                takePictureButton.setEnabled(false);
            }
        }
    }

}
