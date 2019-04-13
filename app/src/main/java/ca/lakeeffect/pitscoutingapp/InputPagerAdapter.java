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

    final int PAGENUM = 6;

    public DrivetrainPage drivetrainPage;
    public CargoHatchPage cargoPage;
    public CargoHatchPage hatchPage;
    public SandstormPage sandstormPage;
    public ClimbingPage climbingPage;
    public StrategyPage strategyPage;

    //Instatiate pages
    public InputPagerAdapter(FragmentManager fm) {
        super(fm);
        drivetrainPage = new DrivetrainPage();
        cargoPage = new CargoHatchPage();
        hatchPage = new CargoHatchPage();
        sandstormPage = new SandstormPage();
        climbingPage = new ClimbingPage();
        strategyPage = new StrategyPage();
    }
    
    //More instatiation
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                drivetrainPage = new DrivetrainPage();
                return drivetrainPage;
            case 1:
                cargoPage = new CargoHatchPage();
                return cargoPage;
            case 2:
                hatchPage = new CargoHatchPage();
                return hatchPage;
            case 3:
                sandstormPage = new SandstormPage();
                return sandstormPage;
            case 4:
                climbingPage = new ClimbingPage();
                return climbingPage;
            case 5:
                strategyPage = new StrategyPage();
                return strategyPage;
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
