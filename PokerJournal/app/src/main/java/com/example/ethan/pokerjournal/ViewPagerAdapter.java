package com.example.ethan.pokerjournal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

// Instantiates Fragment Pages
public class ViewPagerAdapter extends FragmentPagerAdapter
{

    // Instantiate Fragment Lists (titleList holds Titles of Each Fragment)
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    // Implement ViewPageAdapter
    public ViewPagerAdapter(FragmentManager fm) {super(fm);}

    @Override
    // Get Fragment Item
    public Fragment getItem(int position) { return fragmentList.get(position); }

    @Override
    // Get Fragment List Size
    public int getCount() { return fragmentList.size(); }

    // Add a Fragment & Title for the Fragment
    public void addFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    // Get Fragment Page's Title
    public CharSequence getPageTitle(int position) { return titleList.get(position); }
}
