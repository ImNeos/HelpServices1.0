package com.isma.soli.ad.Util;



import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter


{

    private final List<Fragment> lstFragment = new ArrayList<>();
    private final List<String> lstTitles = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm, int behavior)
    {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @Override
    public Fragment getItem(int i) {
        return lstFragment.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lstTitles.get(position);
    }

    @Override
    public int getCount() {
        return  lstTitles.size();
    }


    public void AddFragment (Fragment fragment , String title)
    {
        lstFragment.add(fragment);
        lstTitles.add(title);
    }
}
