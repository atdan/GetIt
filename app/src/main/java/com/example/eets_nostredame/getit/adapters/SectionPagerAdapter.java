package com.example.eets_nostredame.getit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eets_Nostredame on 20/03/2018.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter{

    public static final String TAG = "SectionPagerAdapter";
    private final List<Fragment> fragmentList = new ArrayList<>();


    public SectionPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);

    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
