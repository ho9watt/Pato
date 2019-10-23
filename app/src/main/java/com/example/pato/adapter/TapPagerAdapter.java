package com.example.pato.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import androidx.legacy.app.FragmentStatePagerAdapter;

import com.example.pato.fragment.BoardListFragment;
import com.example.pato.fragment.ContestFragment;
import com.example.pato.fragment.MyFragment;
import com.example.pato.fragment.PatchNoteListFragment;

public class TapPagerAdapter extends FragmentStatePagerAdapter {

    private static int PAGE_COUNT = 4;

    public TapPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return BoardListFragment.newInstance();
            case 1:
                return PatchNoteListFragment.newInstance();
            case 2:
                return ContestFragment.newInstance();
            case 3:
                return MyFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "커뮤니티";
            case 1:
                return "패치노트";
            case 2:
                return "대회정보";
            case 3:
                return "내 정보";
        }
        return null;
    }



}
