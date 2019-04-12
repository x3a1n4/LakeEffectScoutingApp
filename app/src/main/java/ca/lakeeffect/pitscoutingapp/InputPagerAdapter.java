package ca.lakeeffect.pitscoutingapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Ajay on 9/25/2016.
 *
 * Pager Adapter for the input pane
 */
public class InputPagerAdapter extends FragmentStatePagerAdapter {

    final int PAGENUM = 11;

    public DrivetrainPage drivetrainPage;
    public CargoHatchPage cargoHatchPage;
    public SandstormPage sandstormPage;
    public ClimbingPage climbingPage;
    public StrategyPage strategyPage;
    public PregamePage pregamePage;
    public FieldUIPage autoPage;
    public FieldUIPage teleopPage;
    public PostgamePage postgamePage;
    public QualitativePage qualitativePage;

    //Instatiate pages
    public InputPagerAdapter(FragmentManager fm) {
        super(fm);
        drivetrainPage = new DrivetrainPage();
        cargoHatchPage = new CargoHatchPage();
        sandstormPage = new SandstormPage();
        climbingPage = new ClimbingPage();
        strategyPage = new StrategyPage();
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
                cargoHatchPage = new CargoHatchPage();
                return cargoHatchPage;
            case 2:
                cargoHatchPage = new CargoHatchPage();
                return cargoHatchPage;
            case 3:
                sandstormPage = new SandstormPage();
                return sandstormPage;
            case 4:
                climbingPage = new ClimbingPage();
                return climbingPage;
            case 5:
                strategyPage = new StrategyPage();
                return strategyPage;
            case 6:
                autoPage = new FieldUIPage();
                autoPage.autoPage = true;
                return autoPage;
            case 7:
                teleopPage = new FieldUIPage();
                return teleopPage;
            case 8:
                postgamePage = new PostgamePage();
                return postgamePage;
            case 9:
                qualitativePage = new QualitativePage();
                return qualitativePage;
            case 10:
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
                return "Cargo Mechanism";
            case 2:
                return "Hatch Mechanism";
            case 3:
                return "Sandstorm Period";
            case 4:
                return "Climbing";
            case 5:
                return "Strategy";
            case 6:
                return "Autonomous Period";
            case 7:
                return "TeleOp Period";
            case 8:
                return "Post-Game";
            case 9:
                return "Qualitative";
            case 10:
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
