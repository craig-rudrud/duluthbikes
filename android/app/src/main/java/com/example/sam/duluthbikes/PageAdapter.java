package com.example.sam.duluthbikes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sam.duluthbikes.fragments.HeatTabFragment;
import com.example.sam.duluthbikes.fragments.PolyTabFragment;
import com.example.sam.duluthbikes.fragments.UserTabFragment;

/**
 * Created by Sam on 4/20/2017.
 */

public class PageAdapter  extends FragmentStatePagerAdapter {
    int mNumberOfTabs;

    public PageAdapter(FragmentManager fragmentManager, int numberOfTabs) {
        super(fragmentManager);
        this.mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                UserTabFragment userTabFragment = new UserTabFragment();
                return userTabFragment;
            case 1:
                HeatTabFragment heatTabFragment = new HeatTabFragment();
                return heatTabFragment;
            case 2:
                PolyTabFragment polyTabFragment = new PolyTabFragment();
                return polyTabFragment;
            case 3:
                LeaderboardFragment ldrbrdFragment = new LeaderboardFragment();
                return ldrbrdFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mNumberOfTabs;
    }
}
