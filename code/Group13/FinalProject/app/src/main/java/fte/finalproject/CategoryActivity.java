package fte.finalproject;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.Fragment.DetailCategoryFragment;
import fte.finalproject.Fragment.TabFragmentPagerAdapter;

//这个Activity可用于榜单和具体分类
public class CategoryActivity extends AppCompatActivity {
    private boolean isRanking;          //排行榜/分类
    private boolean isMale;             //男生/女生

    private TextView title;
    private ImageView backImage;

    private RadioGroup RG;
    private RadioButton RB1;
    private RadioButton RB2;
    private RadioButton RB3;
    private RadioButton RB4;

    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private TabFragmentPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //获取传递的参数
        Bundle bundle = this.getIntent().getExtras();
        isRanking = bundle.getBoolean("isRanking");
        isMale = bundle.getBoolean("isMale");

        //初始化控件
        title = findViewById(R.id.category_activity_title);
        backImage = findViewById(R.id.category_activity_back);
        RG = findViewById(R.id.category_activity_RG);
        RB1 = findViewById(R.id.category_activity_RB1);
        RB2 = findViewById(R.id.category_activity_RB2);
        RB3 = findViewById(R.id.category_activity_RB3);
        RB4 = findViewById(R.id.category_activity_RB4);
        viewPager = findViewById(R.id.category_activity_viewPager);

        //设置显示内容
        title.setText(bundle.getString("title"));
        if (isRanking) {
            RB1.setText("周榜");
            RB2.setText("月榜");
            RB3.setText("总榜");
            RB4.setVisibility(View.GONE);
        }
        else {
            RB1.setText("热门");
            RB2.setText("新书");
            RB3.setText("好评");
            RB4.setVisibility(View.VISIBLE);
        }

        //设置返回事件
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //添加Fragment
        if (isRanking) {
            //排行榜的Fragment
            for (int i = 0; i < 3; ++i) {
                DetailCategoryFragment fragment = new DetailCategoryFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", bundle.getString("title"));
                bundle1.putBoolean("isRanking", isRanking);
                bundle1.putBoolean("isMale", isMale);
                if (i == 0) bundle1.putString("type", "周榜");
                else if (i == 1) bundle1.putString("type", "月榜");
                else bundle1.putString("type", "总榜");
                fragment.setArguments(bundle1);
                fragmentList.add(fragment);
            }
        }
        else {
            //具体分类的Fragment
            for (int i = 0; i < 4; ++i) {
                DetailCategoryFragment fragment = new DetailCategoryFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("title", bundle.getString("title"));
                bundle1.putBoolean("isRanking", isRanking);
                bundle1.putBoolean("isMale", isMale);
                if (i == 0) bundle1.putString("type", "热门");
                else if (i == 1) bundle1.putString("type", "新书");
                else if (i == 2) bundle1.putString("type", "好评");
                else bundle1.putString("type", "完结");
                fragment.setArguments(bundle1);
                fragmentList.add(fragment);
            }
        }
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
        pagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        if (isRanking) viewPager.setOffscreenPageLimit(2);
        else viewPager.setOffscreenPageLimit(3);

        //设置RadioGroup响应事件
        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.category_activity_RB1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.category_activity_RB2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.category_activity_RB3:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.category_activity_RB4:
                        viewPager.setCurrentItem(3);
                        break;
                }
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
                    RB1.setChecked(true);
                    break;
                case 1:
                    RB2.setChecked(true);
                    break;
                case 2:
                    RB3.setChecked(true);
                    break;
                case 3:
                    RB4.setChecked(true);
                    break;
            }
        }
    }
}
