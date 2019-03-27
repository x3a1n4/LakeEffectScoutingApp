package ca.lakeeffect.scoutingapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Ajay on 9/25/2016.
 *
 * Pager Adapter for the input pane
 */
public class InputPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGENUM = 6;

    public DrivetrainPage drivetrainPage;
    public PregamePage pregamePage;
    public FieldUIPage autoPage;
    public FieldUIPage teleopPage;
    public PostgamePage postgamePage;
    public QualitativePage qualitativePage;

    //Instatiate pages
    public InputPagerAdapter(FragmentManager fm) {
        super(fm);
        drivetrainPage = new DrivetrainPage();
        pregamePage = new PregamePage();
        autoPage = new FieldUIPage();
        teleopPage = new FieldUIPage();
        postgamePage = new PostgamePage();
        qualitativePage = new QualitativePage();
    }
    
    //More instatiation
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                drivetrainPage = new DrivetrainPage();
                return drivetrainPage;
            case 1:
                autoPage = new FieldUIPage();
                autoPage.autoPage = true;
                return autoPage;
            case 2:
                teleopPage = new FieldUIPage();
                return teleopPage;
            case 3:
                postgamePage = new PostgamePage();
                return postgamePage;
            case 4:
                qualitativePage = new QualitativePage();
                return qualitativePage;
            case 5:
                pregamePage = new PregamePage();
                return pregamePage;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGENUM;
    }

    //Set page titles
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Drive Train";
            case 1:
                return "Autonomous Period";
            case 2:
                return "TeleOp Period";
            case 3:
                return "Post-Game";
            case 4:
                return "Qualitative";
            case 5:
                return "Pre-Game";
        }
        return "";
    }

    @Override
    public int getItemPosition(Object object){
        //Ignore sketchyness
        //How is this sketchy? -Xan
        return POSITION_NONE;
    }
}
