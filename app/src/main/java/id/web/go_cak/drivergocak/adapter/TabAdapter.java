package id.web.go_cak.drivergocak.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import id.web.go_cak.drivergocak.fragment.CompleteFragment;
import id.web.go_cak.drivergocak.fragment.IncompleteFragment;

public class TabAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{"INCOMPLETE", "COMPLETE"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = IncompleteFragment.newInstance();
        switch (position) {
            case 0:
                fragment = IncompleteFragment.newInstance();
                break;
            case 1:
                fragment = CompleteFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}