package com.example.sam.duluthbikes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class LdrBoardPageAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;

    public LdrBoardPageAdapter(FragmentManager fragmentManager, int numberOfTabs) {
        super(fragmentManager);
        this.mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //case local leaderboard
                LeaderboardTab localLeaderboard = new LeaderboardTab();
                localLeaderboard.setType(ModelViewPresenterComponents.LOCAL);
                return localLeaderboard;

            case 1: //case global leaderboard
                LeaderboardTab globalLeaderboard = new LeaderboardTab();
                globalLeaderboard.setType(ModelViewPresenterComponents.GLOBAL);
                return globalLeaderboard;

            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mNumberOfTabs;
    }

}
