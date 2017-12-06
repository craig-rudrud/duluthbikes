package com.example.sam.duluthbikes;

/**
 * Created by jacob on 11/16/17.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jacob on 11/12/17.
 */

public class LeaderboardFragment extends Fragment {

    View myView;
    String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_leaderboard, container, false);

        TabLayout tabLayout = (TabLayout) myView.findViewById(R.id.tabsLeaderboard);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.localLeaderboard));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.globalLeaderboard));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) myView.findViewById(R.id.pageLeaderboard);
        LdrBoardPageAdapter adapter = new LdrBoardPageAdapter(getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return myView;
    }
}