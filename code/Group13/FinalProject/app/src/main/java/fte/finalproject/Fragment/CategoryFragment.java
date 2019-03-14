package fte.finalproject.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.R;

//分类的界面
public class CategoryFragment extends Fragment {
    private RadioGroup RG;
    private RadioButton maleRB;
    private RadioButton femaleRB;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private TabFragmentPagerAdapter fragmentPagerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, null);
        //初始化控件
        RG = view.findViewById(R.id.category_RG);
        viewPager = view.findViewById(R.id.category_viewPager);
        maleRB = view.findViewById(R.id.category_RB_male);
        femaleRB = view.findViewById(R.id.category_RB_female);

        //初始化Fragment
        fragmentList.clear();
        MaleInCategoryFragment maleFragment = new MaleInCategoryFragment();
        MaleInCategoryFragment femaleFragment = new MaleInCategoryFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean("isMale", true);
        maleFragment.setArguments(bundle1);
        fragmentList.add(maleFragment);
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("isMale", false);
        femaleFragment.setArguments(bundle2);
        fragmentList.add(femaleFragment);

        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
        fragmentPagerAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(1);

        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.category_RB_male:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.category_RB_female:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });

        return view;
    }

    //设置一个ViewPager的监听事件，左右滑动ViewPager时进行处理
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    maleRB.setChecked(true);
                    break;
                case 1:
                    femaleRB.setChecked(true);
                    break;
            }
        }
    }

}
