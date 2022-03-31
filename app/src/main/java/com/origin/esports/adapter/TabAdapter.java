package com.origin.esports.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList();
    private final List<String> mFragmentTitleList = new ArrayList();

    TabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public Fragment getItem(int i) {
        return (Fragment) mFragmentList.get(i);
    }

    public void addFragment(Fragment fragment, String str) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(str);
    }

    @Nullable
    public CharSequence getPageTitle(int i) {
        return (CharSequence) mFragmentTitleList.get(i);
    }

    public int getCount() {
        return mFragmentList.size();
    }
}
