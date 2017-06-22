package com.example.ethan.pokerjournal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Basic Variables
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    // Uses Fragment Manager
    public ViewPagerAdapter(FragmentManager fm) {super(fm);}

    // Delegates Position of Listings
    @Override
    public Fragment getItem(int position) { return fragmentList.get(position); }

    // Adapts According to List Size
    @Override
    public int getCount() { return fragmentList.size(); }

    // Adds a New Fragment to Poker App
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    // Displays Page Title
    @Override
    public CharSequence getPageTitle(int position) { return titleList.get(position); }
}
