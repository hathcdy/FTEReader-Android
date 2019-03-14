package fte.finalproject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fte.finalproject.Fragment.ReadPageFragment;
import fte.finalproject.Fragment.TabFragmentStatePagerAdapter;
import fte.finalproject.control.DatabaseControl;
import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.ChapterLinkObj;
import fte.finalproject.obj.ChapterLinks;
import fte.finalproject.obj.ChapterObj;
import fte.finalproject.obj.CptListObj;
import fte.finalproject.obj.UserStatusObj;
import fte.finalproject.service.BookService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Intent.ACTION_TIME_TICK;


public class ReadPageActivity extends AppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<>();        //存储所有页面Fragment
    private TabFragmentStatePagerAdapter fragmentAdapter;
    private ViewPager viewPager;

    private String bookid;                      // 书籍id
    private String bookname;                    // 书籍名
    private CptListObj cptListObj;            //章节列表
    List<ChapterLinkObj> chapterLinks;          // 章节查询链接List
    List<ChapterObj> chapterObjs;    // 章节内容OBJ队列
    private int currChapter;                   //当前阅读到的章节       zero-based
    List<List<String>> chaptersContent;        // 每一章每一页的内容
    int totalChapter;                           // 该书总共的章节数
    int cache_chapter_range_min;                // 当前缓冲存储的章节数范围下界 zero-based
    int cache_chapter_range_max;                // 当前缓冲存储的章节数范围上界 zero-based

    // 处理点击的坐标变量
    private float DownX;
    private float DownY;
    private float UpX;
    private float UpY;

    // 页面控件处理
    private RadioGroup rg_control;
    private RadioButton day_and_night_rb_control;
    private RadioButton horizontal_and_vertical_rb_control;
    private RadioButton setting_rb_control;
    //private RadioButton download_rb_control;
    private RadioButton catalog_rb_control;
    private ProgressBar progressBar;
    private RelativeLayout bottom_layout_control;
    private TextView battery_percent_control;
    private TextView time_control;
    private TextView read_page_process_control;
    private RelativeLayout whole_layout_control;

    // 屏幕宽高
    float SCREEN_HEIGHT;
    float SCREEN_WIDTH;

    // 功能栏是否显示
    boolean show_functional_button = false;

    // 活动上下文
    Context context;

    // 广播
    MyReceiver myReceiver;
    IntentFilter intentFilter;

    // 用户阅读状态
    UserStatusObj userStatusObj;
    boolean is_vertical_screen;
    int day_or_night_status;
    int textSize;

    // 目录弹窗
    MyRecyclerViewAdapter<String> adapter;
    List<String> myCategory;

    // 设置功能按钮是否可点击
    boolean clickable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_page);
        System.out.println("onCreate创建");

        //从传递的参数中获取章节相关信息
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        bookid = bundle.getString("bookid");
        bookname = bundle.getString("bookname");
        currChapter = bundle.getInt("currentChapter");
        //System.out.println("bookid: " + bookid + "  currentChapter: " + currChapter);


        // 获取页面控件
        init_page_control();

        // 设置用户阅读习惯状态与界面适配
        set_Reading_Status();

        // 获取页面宽高
        get_screen_info();

        // 注册底部信息栏的系统接收广播
        init_info_broadcast();

        // 设置功能按键
        set_functional_button();

        // 获取上下文
        context = this;
        // 全屏阅读，去除手机状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 初始化底部信息栏显示
        init_bottom_info_layout();

        //初始化 Fragment
        init_fragment();
    }

    // 从数据库中获取用户的阅读习惯
    private void set_Reading_Status() {
        // 获取用户状态(默认用户状态)
        userStatusObj = DatabaseControl.getInstance(this).get_User_Status_Obj(0);
        // 获取默认用户下的横竖屏状态
        int hor_or_ver_screen = userStatusObj.getHor_or_ver_screen();
        //System.out.println("从数据库中获取到的横竖屏状态： " + hor_or_ver_screen);
        if(hor_or_ver_screen == 1) {
            is_vertical_screen = true;
            System.out.println("新建Activity：数据库中是竖屏状态");
        }
        else {
            is_vertical_screen = false;
            System.out.println("新建Activity：数据库中是横屏状态");
            // 切换成横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        day_or_night_status = userStatusObj.getDay_or_night_status();
        if(day_or_night_status == 1) whole_layout_control.setBackgroundColor(getResources().getColor(R.color.nightBackGround));

        textSize = userStatusObj.getTextSize();
    }

    // 初始化底部信息栏显示
    private void init_bottom_info_layout() {
        // 设置当前时间
        // 设置时间格式
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        // 获取当前时间
        Date curdate = new Date(System.currentTimeMillis());
        // 按照格式将Date 时间转化成格式字符串
        String time = formatter.format(curdate);
        // UI设置显示
        time_control.setText(time);
    }

    // 注册底部信息栏的系统接收广播
    private void init_info_broadcast() {
        //注册广播接受者java代码
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);    // 电量变化广播
        intentFilter.addAction(ACTION_TIME_TICK);
        //创建广播接受者对象
        myReceiver = new MyReceiver();
        //注册receiver
        registerReceiver(myReceiver, intentFilter);
    }

    // 阅读帧初始化
    private void init_fragment() {
        // 设置等待进度条
        progressBar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        clickable = false;
        // 清空当前的FragmentList
        fragmentList.clear();
        System.out.println("上次看到的章节数为：（zero-based）" + currChapter);
        // 用线程进行帧初始化，避免进入阅读界面阻塞
        Thread init_fragment_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 判断是否联网了
                Boolean isNetworkConnected = isNetWorkConnected(context);
                // 未联网的响应处理
                if(!isNetworkConnected) {
                    Looper.prepare();
                    // 弹出Toast提示
                    Toast.makeText(ReadPageActivity.this, "网络未连接,书籍获取失败", Toast.LENGTH_SHORT).show();
                    ReadPageActivity.this.finish();
                    Looper.loop();
                    // ProgressBar等待框消失
                    progressBar.setVisibility(View.GONE);
                }
                // 已经联网，获取书籍信息进行阅读
                else {
                    //获取到:章节总页数totalPage、章节title、每页的内容content
                    Thread initContentThread  = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 获取所有章节信息
                            cptListObj = BookService.getBookService().getChaptersByBookId(bookid);
                            if(cptListObj != null) {
                                chapterLinks = cptListObj.getImixToc().getChapterLinks();
                                totalChapter = chapterLinks.size();
                                System.out.println("共有章节数目： " + totalChapter);
                            /*for(int i = 0; i < chapterLinks.size(); i++) {
                                System.out.println(chapterLinks.get(i).getTitle() + ": " + chapterLinks.get(i).getLink());
                            }*/
                                chaptersContent = new ArrayList<>(totalChapter);

                                // 缓存当前章节以及上下10章的数据
                                chapterObjs = new ArrayList<ChapterObj>(totalChapter) {
                                };
                                cache_chapter_range_min = currChapter - 10;
                                cache_chapter_range_max = currChapter + 10;
                                for (int i = cache_chapter_range_min; i <= cache_chapter_range_max; i++) {
                                    System.out.println("正在下载（zero-based）： i---" + i + "  min---" + cache_chapter_range_min + "---max---" + cache_chapter_range_max);
                                    //超出章节范围
                                    if (i > totalChapter - 1) {
                                        //cache_chapter_range_max--;
                                        cache_chapter_range_max = totalChapter - 1;
                                        break;
                                        //continue;
                                    }
                                    if (i < 0) {
                                        cache_chapter_range_min++;
                                        continue;
                                    }
                                    ChapterObj c = BookService.getBookService().getChapterByLink(chapterLinks.get(i).getLink());
                                    //System.out.println(chapterLinks.get(i).getLink());
                                    chapterObjs.add(i - cache_chapter_range_min, c);
                                    //System.out.println(i);
                                }
                                System.out.println(cache_chapter_range_min + "----" + cache_chapter_range_max);
                                //System.out.println(chapterLinks.size());

                                //currentChapterContent.append(c.getIchapter().getBody());
                                //System.out.println(c.getIchapter().getTitle() + "\n" + c.getIchapter().getBody());
                            }
                            else {
                                Looper.prepare();
                                // 弹出Toast提示
                                Toast.makeText(ReadPageActivity.this, "无法获取书籍源", Toast.LENGTH_SHORT).show();
                                ReadPageActivity.this.finish();
                                Looper.loop();
                            }
                        }
                    });
                    initContentThread.start();

                    try {
                        initContentThread.join();
                    } catch (InterruptedException e) {
                        Log.d("[Error] ", "线程获取信息失败");
                        e.printStackTrace();
                    }

                    // 将章节内容分页
                    // 512字节长度一页
                    /*currTotalPage = content.length()/pageLen + 1;
                    System.out.println("分成了" + currTotalPage + "页");
                    List<String> pages = getStrList(content, pageLen);
                    System.out.println("已经分割成： " + pages.size() + "页");
                    System.out.println(pages.get(0));*/

                    /*System.out.println("Test: ");
                    int len1  = "l".length();
                    int len2 = "好".length();
                    int len3 = ",".length();
                    int len4 = "，".length();
                    int len5 = "\n".length();
                    String test = "\u3000\u3000大漠。\r\n\u3000\u3000YES";
                    System.out.println(test);
                    int len6 = test.length();
                    System.out.println("长度：" + len1 + " " + len2 + " " + len3 + " " + len4 + " " + len5 + " " + len6);*/

                    // 根据内容适配各帧
                    int count =(cache_chapter_range_max-cache_chapter_range_min+1);
                    System.out.println(count);
                    for (int i = cache_chapter_range_min; i <= cache_chapter_range_max; ++i) {
                        // 解析章节内容
                        //String title = chapterObjs.get(0).getIchapter().getTitle(); // 这种获取Title的内容错误的（API问题）
                        String title = chapterLinks.get(i).getTitle();
                        String content;
                        if(chapterObjs.get(i-cache_chapter_range_min) == null) {
                            content = "章节获取失败了呢！客官";
                        }
                        else content = chapterObjs.get(i-cache_chapter_range_min).getIchapter().getBody();
                        // 为段首添加缩进
                        content = "\u3000\u3000" + content;
                        content = content.replaceAll("\n", "\n\u3000\u3000");
                        // 新建对应章节内容帧
                        ReadPageFragment fragment = new ReadPageFragment();
                        // 给帧传数据
                        Bundle bundle = new Bundle();
                        bundle.putString("title", title);
                        bundle.putString("content", content);
                        bundle.putInt("day_or_night_status", day_or_night_status);
                        bundle.putInt("textSize", textSize);
                        fragment.setArguments(bundle);
                        // 将新加的帧放入队列中
                        fragmentList.add(fragment);
                    }

                    viewPager.setOnPageChangeListener(new MyPagerChangeListener());
                    fragmentAdapter = new TabFragmentStatePagerAdapter(getSupportFragmentManager(), fragmentList);

                    // 用rxjava更新主线程
                    rxjava_update_page(0);

                }
            }
        });
        init_fragment_thread.start();

    }

    // 获取屏幕宽高等信息
    private void get_screen_info() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;
        /*String str = "屏幕宽高：" + SCREEN_WIDTH + ":" + SCREEN_HEIGHT;
        Toast.makeText(ReadPageActivity.this, str, Toast.LENGTH_SHORT).show();*/
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        // 将阅读到的当前章节存入数据库
        DatabaseControl.getInstance(this).updateProgress(currChapter, bookid);
        // 将当前设置的用户习惯存入数据库(报错)
        userStatusObj.setDay_or_night_status(day_or_night_status);
        userStatusObj.setHor_or_ver_screen((is_vertical_screen?1:0));
        userStatusObj.setTextSize(textSize);
        DatabaseControl.getInstance(this).updateStatus(0,userStatusObj);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy");
        System.out.println("横竖屏状况：" + ((is_vertical_screen) ? "竖屏":"横屏"));
        // todo:注销广播
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    // 屏幕点击处理
    // 处理屏幕滑动翻页和点击中部弹出功能按键底框
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!clickable) return false;
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            DownX = event.getX();
            DownY = event.getY();
        }
        else if (MotionEvent.ACTION_UP == event.getAction()) {
            UpX = event.getX();
            UpY = event.getY();
        }

        // 滑动事件
        if(Math.abs(UpX-DownX) > 10 || Math.abs(UpY-DownY)>10) {

        }
        // 点击事件
        else {
            System.out.println("点击位置：" + DownX + ":" + DownY);
            System.out.println("屏幕宽高：" + SCREEN_WIDTH + " " + SCREEN_HEIGHT);
            if (DownX > SCREEN_WIDTH / 3 && DownX < SCREEN_WIDTH * 2 / 3 && DownY > SCREEN_HEIGHT / 5 && DownY < SCREEN_HEIGHT * 4 / 5) {
                if (show_functional_button) {
                    rg_control.setVisibility(View.GONE);
                    bottom_layout_control.setVisibility(View.VISIBLE);
                    show_functional_button = false;
                } else {
                    bottom_layout_control.setVisibility(View.GONE);
                    rg_control.setVisibility(View.VISIBLE);
                    show_functional_button = true;
                }

            } /*else if (DownX <= SCREEN_WIDTH / 3) {
                // todo:翻页有bug
                Toast.makeText(ReadPageActivity.this, "向上翻页", Toast.LENGTH_SHORT).show();
                System.out.println("当前阅读到的章节： " + (currChapter+1));
                if(currChapter-1 >= cache_chapter_range_min)
                    viewPager.setCurrentItem((currChapter-1-cache_chapter_range_min), false);
            } else {
                // todo:翻页有bug
                Toast.makeText(ReadPageActivity.this, "向下翻页", Toast.LENGTH_SHORT).show();
                System.out.println("当前阅读到的章节：" + (currChapter+1));
                if(currChapter+1 <= cache_chapter_range_max)
                    viewPager.setCurrentItem((currChapter+1-cache_chapter_range_min), false);
            }*/
        }


        return super.dispatchTouchEvent(event);
    }

    // 设置功能按键
    // 图标样式&&点击处理
    private void set_functional_button() {
        // 设置底部功能按钮的大小
        Drawable drawable;
        // 根据当前阅读日间/夜间模式设置图标

        if(day_or_night_status == 0) {
            drawable = getResources().getDrawable(R.mipmap.nighttime);
            drawable.setBounds(0, 0, 70, 70);
            day_and_night_rb_control.setCompoundDrawables(null, drawable, null, null);
            day_and_night_rb_control.setText("夜间");
        }
        else {
            drawable = getResources().getDrawable(R.mipmap.daytime);
            drawable.setBounds(0, 0, 70, 70);
            day_and_night_rb_control.setCompoundDrawables(null, drawable, null, null);
            day_and_night_rb_control.setText("日间");
        }
        // 根据当前横竖屏状况设置图标
        if(is_vertical_screen) {
            drawable = getResources().getDrawable(R.mipmap.horizontal_screen);
            drawable.setBounds(0, 0, 70, 70);
            horizontal_and_vertical_rb_control.setCompoundDrawables(null, drawable, null,null);
            horizontal_and_vertical_rb_control.setText("横屏");
            horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
        }
        else {
            drawable = getResources().getDrawable(R.mipmap.vertical_screen);
            drawable.setBounds(0, 0, 70, 70);
            horizontal_and_vertical_rb_control.setCompoundDrawables(null, drawable, null,null);
            horizontal_and_vertical_rb_control.setText("竖屏");
            horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
        }
        drawable = getResources().getDrawable(R.mipmap.textsize);
        drawable.setBounds(0, 0, 70, 70);
        setting_rb_control.setCompoundDrawables(null, drawable,null, null);
        /*drawable = getResources().getDrawable(R.mipmap.download);
        drawable.setBounds(0, 0, 70, 70);
        download_rb_control.setCompoundDrawables(null, drawable,null, null);*/
        drawable = getResources().getDrawable(R.mipmap.catalog);
        drawable.setBounds(0, 0, 70, 70);
        catalog_rb_control.setCompoundDrawables(null, drawable,null, null);
        // 设置功能按钮的点击响应处理
        // 夜间/白日功能切换
        day_and_night_rb_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day_or_night_status == 0) {
                    whole_layout_control.setBackgroundColor(getResources().getColor(R.color.nightBackGround));
                    day_or_night_status = 1;
                    //userStatusObj.setDay_or_night_status(day_or_night_status);
                    //DatabaseControl.getInstance(context).updateStatus(0, userStatusObj);
                    changeFrameStyle();
                    day_and_night_rb_control.setTextColor(Color.BLACK);
                    Drawable drawable = getResources().getDrawable(R.mipmap.daytime);
                    drawable.setBounds(0, 0, 70, 70);
                    day_and_night_rb_control.setCompoundDrawables(null, drawable, null, null);
                    day_and_night_rb_control.setText("日间");
                }
                else {
                    whole_layout_control.setBackgroundColor(getResources().getColor(R.color.PapayaWhip));
                    day_or_night_status = 0;
                    //userStatusObj.setDay_or_night_status(day_or_night_status);
                    //DatabaseControl.getInstance(context).updateStatus(0, userStatusObj);
                    //init_fragment();
                    changeFrameStyle();
                    day_and_night_rb_control.setTextColor(Color.BLACK);
                    Drawable drawable = getResources().getDrawable(R.mipmap.nighttime);
                    drawable.setBounds(0, 0, 70, 70);
                    day_and_night_rb_control.setCompoundDrawables(null, drawable, null, null);
                    day_and_night_rb_control.setText("夜间");
                }
            }
        });

        // 横屏竖屏功能切换
        horizontal_and_vertical_rb_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当前竖屏状态
                if(is_vertical_screen) {
                    // 记录状态数据转变,更新默认用户
                    is_vertical_screen = false;
                    //userStatusObj.setHor_or_ver_screen(0);
                    horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
                    //DatabaseControl.getInstance(context).updateStatus(0,0);
                    //DatabaseControl.getInstance(context).updateStatus(0, userStatusObj);
                    // 切换成横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    Drawable drawable = getResources().getDrawable(R.mipmap.vertical_screen);
                    drawable.setBounds(0, 0, 70, 70);
                    horizontal_and_vertical_rb_control.setCompoundDrawables(null, drawable, null,null);
                    horizontal_and_vertical_rb_control.setText("竖屏");
                    horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
                }
                // 当前横屏状态
                else {
                    // 记录状态数据转变
                    is_vertical_screen = true;
                    //userStatusObj.setHor_or_ver_screen(1);
                    horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
                    //DatabaseControl.getInstance(context).updateStatus(0,1);
                    //DatabaseControl.getInstance(context).updateStatus(0, userStatusObj);
                    System.out.println("改成竖屏");
                    System.out.println(DatabaseControl.getInstance(context).get_Hor_Or_Ver_Screen_Status(0));
                    // 切换成竖屏状态
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Drawable drawable = getResources().getDrawable(R.mipmap.horizontal_screen);
                    drawable.setBounds(0, 0, 70, 70);
                    horizontal_and_vertical_rb_control.setCompoundDrawables(null, drawable, null,null);
                    horizontal_and_vertical_rb_control.setText("横屏");
                    horizontal_and_vertical_rb_control.setTextColor(Color.BLACK);
                }
            }
        });
        // 字体样式和大小设置功能
        setting_rb_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出一个字体样式大小设置框
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_text_setting);
                // 字体不变红色
                setting_rb_control.setTextColor(Color.BLACK);

                final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int)(SCREEN_WIDTH * 2 / 3);
                /*params.height = (int)(SCREEN_HEIGHT * 2 / 3);*/
                dialog.getWindow().setAttributes(params);
                ImageView textSize_plus_control = dialog.findViewById(R.id.dialog_text_setting_plus_imageview);
                final ImageView textSize_minus_control = dialog.findViewById(R.id.dialog_text_setting_minus_imageview);
                final TextView textSize_textView = dialog.findViewById(R.id.dialog_text_setting_textSize);
                textSize_textView.setText(Integer.toString(textSize));
                textSize_minus_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textSize > 10) textSize-=2;
                        textSize_textView.setText(Integer.toString(textSize));
                        changeFrameStyle();
                    }
                });
                textSize_plus_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(textSize < 50) textSize+=2;
                        textSize_textView.setText(Integer.toString(textSize));
                        changeFrameStyle();
                    }
                });
                // 设置 dialog 属性并显示
                dialog.setCancelable(true);
                dialog.show();

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        changeFrameStyle();
                    }
                });
            }
        });
        // 下载功能
        /*download_rb_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReadPageActivity.this, "文本下载功能还没实现呢！客官", Toast.LENGTH_LONG).show();
            }
        });*/
        // 目录功能
        catalog_rb_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出一个目录选择框
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.catalog_dialog);
                // 隐藏功能按钮控件框
                rg_control.setVisibility(View.GONE);
                show_functional_button = false;
                bottom_layout_control.setVisibility(View.VISIBLE);
                // 字体不变红色
                catalog_rb_control.setTextColor(Color.BLACK);
                // 设置dialog标题为书名
                TextView title = dialog.findViewById(R.id.catalog_title);
                title.setText(bookname);
                // 书籍目录RecyclerView
                RecyclerView dialog_catalog = dialog.findViewById(R.id.catalog_recylerView);
                // 书籍目录数据初始化
                myCategory = new ArrayList<>();
                for(int i = 0; i < chapterLinks.size(); i++)
                    myCategory.add(chapterLinks.get(i).getTitle());

                // 设置 Adapter
                adapter = new MyRecyclerViewAdapter<String>(context, R.layout.item_catalog, myCategory) {
                    @Override
                    public void convert(MyViewHolder holder, String s) {
                        TextView title = holder.getView(R.id.catalog_title);
                        title.setText(s);
                    }
                };
                // 设置点击目录跳转到相应章节
                adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        // dialog目录框消失
                        dialog.dismiss();
                        // 设置等待进度条
                        viewPager.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        clickable = false;
                        // 清空当前的FragmentList
                        fragmentList.clear();
                        // 设置跳转章节数
                        currChapter = position;
                        // 加载帧
                        init_fragment();
                        // 更新UI
                        //rxjava_update_page(2);
                    }

                    @Override
                    public void onLongClick(int position) {

                    }
                });

                final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int)(SCREEN_WIDTH * 2 / 3);
                params.height = (int)(SCREEN_HEIGHT * 2 / 3);
                dialog.getWindow().setAttributes(params);

                // 适配 Adapter
                dialog_catalog.setAdapter(adapter);

                // 设置 RecyclerView 布局
                dialog_catalog.setLayoutManager(new LinearLayoutManager(context));

                // 设置 dialog 属性并显示
                dialog.setCancelable(true);
                dialog.show();
            }
        });
    }

    // 获取页面控件
    private void init_page_control() {
        rg_control = findViewById(R.id.read_page_bottom_rg);
        day_and_night_rb_control = findViewById(R.id.read_page_day_and_night_rb);
        horizontal_and_vertical_rb_control = findViewById(R.id.read_page_horizontal_and_vertical_rb);
        setting_rb_control = findViewById(R.id.read_page_setting_rb);
        //download_rb_control = findViewById(R.id.read_page_download_rb);
        catalog_rb_control = findViewById(R.id.read_page_catalog_rb);
        viewPager = findViewById(R.id.read_page_viewPager);
        progressBar = findViewById(R.id.read_page_progressbar);
        bottom_layout_control = findViewById(R.id.activity_read_page_bottom_layout);
        battery_percent_control = findViewById(R.id.activity_read_page_battery_percent);
        time_control = findViewById(R.id.activity_read_page_time);
        read_page_process_control = findViewById(R.id.activity_read_page_process);
        whole_layout_control = findViewById(R.id.read_page_whole_layout);
    }

    //设置一个ViewPager的监听事件，左右滑动ViewPager时进行处理
    //当滑动到当前缓存的倒数第M章的时候，进行网络资源访问，获取新的N章的资源
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        int from;
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            from = arg0;
        }

        @Override
        public void onPageSelected(final int arg0) {
            Log.d("跳转", arg0 + ", " + from);

            int cache_left = 5;         // 设置当缓存剩余多少章节时，重新开始进行资源获取
            final int cache_num = 10;         // 设置一次获取资源的章节数
            // 帧界面向右滑动了一次
            if (arg0 == from + 1) {
                // 当前章节数+1
                currChapter++;
                // 设置进度显示
                read_page_process_control.setText(Integer.toString(currChapter+1) + "/" + Integer.toString(totalChapter));
                // 滑动到当前缓存剩余量不多时，当前再访问剩余量设置是五章节(不包括当前章节)
                if(arg0 == (cache_chapter_range_max-cache_chapter_range_min) - cache_left) {

                    // 已经缓存到网络上的最后一章了，没有更新
                    if(cache_chapter_range_max == totalChapter) {
                        //Toast.makeText(ReadPageActivity.this, "已经是最后一章了！客官", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // 隐藏ViewPager避免添加帧的时候滑动ViewPager报错
                        // 等待后台适配阅读帧
                        viewPager.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        clickable = false;
                        //使用子线程进行缓存更新
                        Thread update_cache_thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 再使用子线程进行网络访问，获取下面N个章节的内容（当前N设置为10）
                                /*final int newChapterNum = arg0 + 1;*/
                                Thread getNewChapterThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isNetWorkConnected(context)) {
                                            for (int i = 1; i <= cache_num; i++) {
                                                int newChapterNum = cache_chapter_range_max + i;
                                                // 非法章节数(已超出网络给出的章节数)
                                                if (newChapterNum >= totalChapter) continue;
                                                // 合法章节则获取新章节
                                                ChapterObj c = BookService.getBookService().getChapterByLink(chapterLinks.get(newChapterNum).getLink());
                                                System.out.println("正在下载（zero-based）： newChapterNum---" + newChapterNum + "  min---" + cache_chapter_range_min + "---max---" + cache_chapter_range_max);
                                                System.out.println("获取章节： " + newChapterNum + "------" + chapterLinks.get(newChapterNum).getLink());
                                                chapterObjs.add(newChapterNum - cache_chapter_range_min, c);
                                            }
                                        }
                                        else {
                                            Log.e("ERROR", "网络连接状况：未连接");
                                        }
                                    }
                                });
                                getNewChapterThread.start();

                                // 等待网络访问子线程完成
                                try {
                                    getNewChapterThread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("子线程更新完毕");
                                // 新增N个帧
                                for(int i = 1; i <= cache_num; i++) {
                                    int newChapterNum = cache_chapter_range_max + 1;
                                    // 已经超出总章节数
                                    if(newChapterNum >= totalChapter) continue;
                                    // 网络连接故障，要获取的章节未缓存成功，不添加新帧
                                    if(newChapterNum - cache_chapter_range_min >= chapterObjs.size()) break;
                                    // 合法的章节数
                                    cache_chapter_range_max++;
                                    // 解析章节内容
                                    //String title = chapterObjs.get(0).getIchapter().getTitle(); // 这种获取Title的内容错误的（API问题）
                                    String title = chapterLinks.get(newChapterNum).getTitle();
                                    String content;
                                    if(chapterObjs.get(newChapterNum-cache_chapter_range_min) == null) {
                                        content = "章节获取失败了呢！客官";
                                    }
                                    else content = chapterObjs.get(newChapterNum-cache_chapter_range_min).getIchapter().getBody();
                                    // 为段首添加缩进
                                    content = "\u3000\u3000" + content;
                                    content = content.replaceAll("\n", "\n\u3000\u3000");
                                    // 新建对应章节内容帧
                                    ReadPageFragment fragment = new ReadPageFragment();
                                    // 给帧传数据
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", title);
                                    bundle.putString("content", content);
                                    bundle.putInt("day_or_night_status", day_or_night_status);
                                    bundle.putInt("textSize", textSize);
                                    fragment.setArguments(bundle);
                                    // 将新加的帧放入队列中
                                    fragmentList.add(fragment);
                                }
                                // 使用rxjava进行主界面UI更新
                                rxjava_update_page(1);
                                System.out.println("更新章节: ");
                            }
                        });
                        update_cache_thread.start();
                    }

                }
            }
            // 向左滑动一页
            else if (arg0 == from) {
                // 当前章节数-1
                currChapter--;
                // 设置进度显示
                read_page_process_control.setText(Integer.toString(currChapter+1) + "/" + Integer.toString(totalChapter));
                // 滑动到当前缓存剩余量不多时，当前再访问剩余量设置是五章节(不包括当前章节)
                System.out.println("arg0-currChapter-min-max: " + arg0 + " " + currChapter + " " + cache_chapter_range_min + " " + cache_chapter_range_max);
                if((currChapter-cache_chapter_range_min) == cache_left) {
                    // 已经是网络上的第一章了
                    if(cache_chapter_range_min == 0) {
                        //Toast.makeText(ReadPageActivity.this, "已经是第一章了！客官", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // 隐藏ViewPager避免添加帧的时候滑动ViewPager报错
                        // 等待后台适配阅读帧
                        viewPager.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        clickable = false;
                        //使用子线程进行缓存更新
                        Thread update_cache_thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 再使用子线程进行网络访问，获取下面N个章节的内容（当前N设置为10）
                                /*final int newChapterNum = arg0 + 1;*/
                                Thread getNewChapterThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(isNetWorkConnected(context)) {
                                            for (int i = 1; i <= cache_num; i++) {
                                                int newChapterNum = cache_chapter_range_min - i;
                                                // 非法章节数(已经到第一章了)
                                                if (newChapterNum < 0) continue;
                                                // 合法章节则获取新章节
                                                ChapterObj c = BookService.getBookService().getChapterByLink(chapterLinks.get(newChapterNum).getLink());
                                                System.out.println(chapterLinks.get(newChapterNum).getLink());
                                                // 在队列头插入章节信息
                                                chapterObjs.add(0, c);
                                            }
                                        }
                                    }
                                });
                                getNewChapterThread.start();

                                // 等待网络访问子线程完成
                                try {
                                    getNewChapterThread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                // 新增N个帧
                                for(int i = 1; i <= cache_num; i++) {
                                    int newChapterNum = cache_chapter_range_min - 1;
                                    // 比第一章还小，非法章节数
                                    if(newChapterNum < 0) continue;
                                    // 因网络故障未缓存到所要章节
                                    if(cache_chapter_range_max - cache_chapter_range_min + 1 >= chapterObjs.size()) break;
                                    // 合法的章节数
                                    cache_chapter_range_min--;
                                    // 解析章节内容
                                    //String title = chapterObjs.get(0).getIchapter().getTitle(); // 这种获取Title的内容错误的（API问题）
                                    String title = chapterLinks.get(newChapterNum).getTitle();
                                    String content;
                                    if(chapterObjs.get(newChapterNum-cache_chapter_range_min) == null) {
                                        content = "章节获取失败了呢！客官";
                                    }
                                    else content = chapterObjs.get(newChapterNum-cache_chapter_range_min).getIchapter().getBody();
                                    // 为段首添加缩进
                                    content = "\u3000\u3000" + content;
                                    content = content.replaceAll("\n", "\n\u3000\u3000");
                                    // 新建对应章节内容帧
                                    ReadPageFragment fragment = new ReadPageFragment();
                                    // 给帧传数据
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", title);
                                    bundle.putString("content", content);
                                    bundle.putInt("day_or_night_status", day_or_night_status);
                                    bundle.putInt("textSize", textSize);
                                    fragment.setArguments(bundle);
                                    // 将新加的帧放入队列中
                                    fragmentList.add(0,fragment);
                                }
                                // 使用rxjava进行主界面UI更新
                                rxjava_update_page(2);
                                System.out.println("更新章节: ");
                            }
                        });
                        update_cache_thread.start();
                    }

                }
            }
        }
    }

    // 辅助函数：判断网络是否连接
    public boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    // RXJAVA2 更新主界面UI
    private void rxjava_update_page(final int type) {
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(type);
                e.onComplete();
            }

        });

        CompositeDisposable mCompositeDisposable = new CompositeDisposable();

        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer value) {
                Log.d("BackgroundActivity", "onNext");
                // type = 0 界面初始化更新
                if(type == 0) {
                    // 初始化适配阅读帧
                    fragmentAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(fragmentAdapter);
                    // 跳到上次阅读到的章节
                    viewPager.setCurrentItem(currChapter-cache_chapter_range_min);
                    // 适配完毕，取消ProgressBar, 隐藏功能按键，显示底部信息栏
                    progressBar.setVisibility(View.GONE);
                    rg_control.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    clickable = true;
                    read_page_process_control.setText(Integer.toString(currChapter+1) + "/" + Integer.toString(totalChapter));
                    bottom_layout_control.setVisibility(View.VISIBLE);
                }
                // type == 1 界面获取下N章节更新
                else if(type == 1) {
                    fragmentAdapter.notifyDataSetChanged();
                    // 适配完毕，取消ProgressBar, 隐藏功能按键
                    progressBar.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    clickable = true;
                }
                // type == 2 界面获取上N章节更新
                else if(type == 2) {
                    // 适配Adapter
                    fragmentAdapter.notifyDataSetChanged();
                    // 跳回到刚才阅读到的章节
                    viewPager.setCurrentItem((currChapter-cache_chapter_range_min), false);
                    // 适配完毕，取消ProgressBar, 隐藏功能按键
                    progressBar.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    clickable = true;
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("BackgroundActivity", "onError=" + e);
            }

            @Override
            public void onComplete() {
                Log.d("BackgroundActivity", "onComplete");
            }
        };

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);

    }

    // 辅助函数 : 将字符串按指定长度分割
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        List<String> res = new ArrayList<>();
        for(int i = 0; i < size - 1; i++) {
            String s = inputString.substring(i*length, (i+1)*length-1);
            res.add(s);
        }
        String s = inputString.substring((size-1)*length);
        res.add(s);
        return res;
    }

    // 注册获取系统广播
    // 广播获取系统电量和时间
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 判断它是否是为电量变化的Broadcast Action
            // 电量变化广播
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);
                //把它转成百分比
                int percent = level*100/scale;
                battery_percent_control.setText(Integer.toString(percent));
            }
            else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                // 设置时间格式
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                // 获取当前时间
                Date curdate = new Date(System.currentTimeMillis());
                // 按照格式将Date 时间转化成格式字符串
                String time = formatter.format(curdate);
                // UI设置显示
                time_control.setText(time);
            }
        }

    }


    // 当使用功能按键切换阅读界面阅读习惯时
    // 不必要重新进行网络访问
    // 直接改变阅读帧的样式
    public void changeFrameStyle() {
        System.out.println("---------[changeFrameStyle]-----------");
        // 设置等待进度条
        progressBar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        // 清空当前的FragmentList
        fragmentList.clear();
        for(int i = cache_chapter_range_min; i<= cache_chapter_range_max; i++) {
            // 解析章节内容
            //String title = chapterObjs.get(0).getIchapter().getTitle(); // 这种获取Title的内容错误的（API问题）
            String title = chapterLinks.get(i).getTitle();
            String content;
            if(chapterObjs.get(i-cache_chapter_range_min) == null) {
                content = "章节获取失败了呢！客官";
            }
            else content = chapterObjs.get(i-cache_chapter_range_min).getIchapter().getBody();
            // 为段首添加缩进
            content = "\u3000\u3000" + content;
            content = content.replaceAll("\n", "\n\u3000\u3000");
            // 新建对应章节内容帧
            ReadPageFragment fragment = new ReadPageFragment();
            // 给帧传数据
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("content", content);
            bundle.putInt("day_or_night_status", day_or_night_status);
            bundle.putInt("textSize", textSize);
            fragment.setArguments(bundle);
            // 将新加的帧放入队列中
            fragmentList.add(fragment);
        }
        rxjava_update_page(2);
    }


    // 重载onConfigurationChanged函数处理横竖屏切换
    // 使得不必重新回调Activity整个生命周期
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //text_screen.append("\n 当前屏幕为横屏");
            System.out.println("当前屏幕为横屏");
        } else {
            //text_screen.append("\n 当前屏幕为竖屏");
            System.out.println("当前屏幕为竖屏");
        }
        if(is_vertical_screen == true) {

            System.out.println("数据库中是竖屏");
        } else {
            System.out.println("数据库中是横屏");
        }
        // 重新设置屏幕宽高
        get_screen_info();
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged");
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置横屏
    }

}
