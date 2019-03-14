package fte.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.Fragment.BookShelfFragment;
import fte.finalproject.Fragment.CategoryFragment;
import fte.finalproject.Fragment.RankingFragment;
import fte.finalproject.Fragment.TabFragmentStatePagerAdapter;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.ChapterLinkObj;
import fte.finalproject.obj.ChapterLinks;
import fte.finalproject.obj.ChapterObj;
import fte.finalproject.obj.CptListObj;
import fte.finalproject.obj.FuzzySearchResultObj;
import fte.finalproject.obj.SearchResultObj;
import fte.finalproject.obj.ShelfBookObj;
import fte.finalproject.service.BookService;

import static fte.finalproject.control.DatabaseControl.getInstance;

//总体界面，包含书架、排行榜、分类
public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private TextView title;
    private ImageView search;

    private RadioGroup bottomRG;
    private RadioButton bookshelfRB;
    private RadioButton rankingRB;
    private RadioButton categoryRB;

    private RadioGroup topRG;
    private RadioButton maleRB;
    private RadioButton femaleRB;
    private static Context context = null;

    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private TabFragmentStatePagerAdapter fragmentPagerAdapter;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();
        //初始化控件
        radioGroup = findViewById(R.id.main_top_RG);
        title = findViewById(R.id.main_title);
        search = findViewById(R.id.main_search);
        bottomRG = findViewById(R.id.main_bottomRG);
        bookshelfRB = findViewById(R.id.main_bottom_bookshelf);
        rankingRB = findViewById(R.id.main_bottom_ranking);
        categoryRB = findViewById(R.id.main_bottom_category);
        topRG = findViewById(R.id.main_top_RG);
        maleRB = findViewById(R.id.main_top_male);
        femaleRB = findViewById(R.id.main_top_female);
        viewPager = findViewById(R.id.main_viewPager);

        //初始化Fragment
        BookShelfFragment bookShelfFragment = new BookShelfFragment();
        RankingFragment rankingFragment = new RankingFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMale", true);
        rankingFragment.setArguments(bundle);
        CategoryFragment categoryFragment = new CategoryFragment();
        fragmentList.add(bookShelfFragment);
        fragmentList.add(rankingFragment);
        fragmentList.add(categoryFragment);

        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
        fragmentPagerAdapter = new TabFragmentStatePagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);

        //设置底部按钮图标大小
        Drawable drawable = getResources().getDrawable(R.mipmap.bookshelf_red);
        drawable.setBounds(0, 0, 70, 70);
        bookshelfRB.setCompoundDrawables(null, drawable , null,null);
        drawable = getResources().getDrawable(R.mipmap.ranking);
        drawable.setBounds(0, 0, 70, 70);
        rankingRB.setCompoundDrawables(null, drawable, null,null);
        drawable = getResources().getDrawable(R.mipmap.category);
        drawable.setBounds(0, 0, 70, 70);
        categoryRB.setCompoundDrawables(null, drawable,null, null);

        //设置顶部按钮图标大小
        drawable = getResources().getDrawable(R.mipmap.male_blue);
        drawable.setBounds(0, 0, 70, 70);
        maleRB.setCompoundDrawables(null, drawable, null, null);
        drawable = getResources().getDrawable(R.mipmap.female_black);
        drawable.setBounds(0, 0, 70, 70);
        femaleRB.setCompoundDrawables(null, drawable, null, null);

        //处理顶部RG事件
        topRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_top_male:
                        Drawable drawable = getResources().getDrawable(R.mipmap.male_blue);
                        drawable.setBounds(0, 0, 70, 70);
                        maleRB.setCompoundDrawables(null, drawable, null, null);
                        drawable = getResources().getDrawable(R.mipmap.female_black);
                        drawable.setBounds(0, 0, 70, 70);
                        femaleRB.setCompoundDrawables(null, drawable, null, null);
                        RankingFragment rankingFragment1 = new RankingFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isMale", true);
                        rankingFragment1.setArguments(bundle);
                        fragmentList.set(1, rankingFragment1);
                        fragmentPagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.main_top_female:
                        drawable = getResources().getDrawable(R.mipmap.male_black);
                        drawable.setBounds(0, 0, 70, 70);
                        maleRB.setCompoundDrawables(null, drawable, null, null);
                        drawable = getResources().getDrawable(R.mipmap.female_red);
                        drawable.setBounds(0, 0, 70, 70);
                        femaleRB.setCompoundDrawables(null, drawable, null, null);
                        RankingFragment rankingFragment2 = new RankingFragment();
                        bundle = new Bundle();
                        bundle.putBoolean("isMale", false);
                        rankingFragment2.setArguments(bundle);
                        fragmentList.set(1, rankingFragment2);
                        fragmentPagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });

        //处理底部RG事件
        bottomRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.main_bottom_bookshelf:
                        index = 0;
                        Drawable drawable = getResources().getDrawable(R.mipmap.bookshelf_red);
                        drawable.setBounds(0, 0, 70, 70);
                        bookshelfRB.setCompoundDrawables(null, drawable , null,null);
                        drawable = getResources().getDrawable(R.mipmap.ranking);
                        drawable.setBounds(0, 0, 70, 70);
                        rankingRB.setCompoundDrawables(null, drawable, null,null);
                        drawable = getResources().getDrawable(R.mipmap.category);
                        drawable.setBounds(0, 0, 70, 70);
                        categoryRB.setCompoundDrawables(null, drawable,null, null);
                        break;
                    case R.id.main_bottom_ranking:
                        index = 1;
                        drawable = getResources().getDrawable(R.mipmap.bookshelf);
                        drawable.setBounds(0, 0, 70, 70);
                        bookshelfRB.setCompoundDrawables(null, drawable , null,null);
                        drawable = getResources().getDrawable(R.mipmap.ranking_red);
                        drawable.setBounds(0, 0, 70, 70);
                        rankingRB.setCompoundDrawables(null, drawable, null,null);
                        drawable = getResources().getDrawable(R.mipmap.category);
                        drawable.setBounds(0, 0, 70, 70);
                        categoryRB.setCompoundDrawables(null, drawable,null, null);
                        break;
                    case R.id.main_bottom_category:
                        index = 2;
                        drawable = getResources().getDrawable(R.mipmap.bookshelf);
                        drawable.setBounds(0, 0, 70, 70);
                        bookshelfRB.setCompoundDrawables(null, drawable , null,null);
                        drawable = getResources().getDrawable(R.mipmap.ranking);
                        drawable.setBounds(0, 0, 70, 70);
                        rankingRB.setCompoundDrawables(null, drawable, null,null);
                        drawable = getResources().getDrawable(R.mipmap.category_red);
                        drawable.setBounds(0, 0, 70, 70);
                        categoryRB.setCompoundDrawables(null, drawable,null, null);
                        break;
                }
                viewPager.setCurrentItem(index);
            }
        });

        //搜索按钮点击事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

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
                    title.setText("书架");
                    radioGroup.setVisibility(View.GONE);
                    bookshelfRB.setChecked(true);
                    break;
                case 1:
                    title.setText("排行榜");
                    radioGroup.setVisibility(View.VISIBLE);
                    rankingRB.setChecked(true);
                    break;
                case 2:
                    title.setText("分类");
                    radioGroup.setVisibility(View.GONE);
                    categoryRB.setChecked(true);
                    break;
            }
        }
    }
}
