package fte.finalproject.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class TabFragmentStatePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
    private FragmentManager fm;
    private List<Fragment> list;

    public TabFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fm = fm;
        this.list = list;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
