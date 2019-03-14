package fte.finalproject;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.SearchResultObj;
import fte.finalproject.service.BookService;

import static fte.finalproject.control.DatabaseControl.getInstance;
import static fte.finalproject.service.BookService.getBookService;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView cancelView;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text6;
    private ImageView deleteView;
    private ImageView freshView;
    private ListView historyList;
    private ListView fuzzyList;
    private ConstraintLayout initialLayout;
    private LinearLayout searchLayout;
    private RecyclerView resultList;
    private List<String> histories;
    private List<String> tempFuzzy;
    private List<SearchResultObj.book> results;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private boolean isSubmit;
    private boolean flag;
    public Handler handler = new Handler();
    private String[] hotBooks = {"一品娇宠","剑来","逆天邪神","神医嫡女","官梯",
            "最强狂兵","无敌剑域","一世倾城","天骄战纪","元尊",
            "天行","修罗武神","永夜君王","家有王妃初长成","神级奶爸",
            "神医毒妃","战神狂飙","逆天邪神","神医嫡女","江山美色",
            "圣墟","极品透视学生","正道潜龙","斗罗大陆","雪中悍刀行",
            "枭臣","将夜","校花的贴身高手","大刁民","偷香高手",};


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        isSubmit = false;
        flag = true; //用于同步问题的布尔变量
        //初始化列表
        histories = getInstance(getBaseContext()).getAllHistory();
        tempFuzzy = new ArrayList<>();
        results = new ArrayList<>();
        //获取控件
        freshView = findViewById(R.id.search_fresh_image);
        cancelView = findViewById(R.id.search_cancel_text);
        searchView = findViewById(R.id.search_search_searchView);
        deleteView = findViewById(R.id.search_delete_image);
        historyList = findViewById(R.id.search_history_list);
        fuzzyList = findViewById(R.id.search_fuzzy_list);
        resultList = findViewById(R.id.search_result_list);
        initialLayout = findViewById(R.id.search_initial_layout);
        searchLayout = findViewById(R.id.search_afters_layout);
        text1 = findViewById(R.id.search_pop_text1);
        text2 = findViewById(R.id.search_pop_text2);
        text3 = findViewById(R.id.search_pop_text3);
        text4 = findViewById(R.id.search_pop_text4);
        text5 = findViewById(R.id.search_pop_text5);
        text6 = findViewById(R.id.search_pop_text6);

        //设置历史列表adapter
        final ArrayAdapter<String> historyAdapter = new ArrayAdapter<>(this,R.layout.item_listview,histories);
        historyList.setAdapter(historyAdapter);
        //设置结果列表adapter
        recyclerViewAdapter = new MyRecyclerViewAdapter<SearchResultObj.book>(SearchActivity.this,R.layout.item_book,results) {
            @Override
            public void convert(MyViewHolder holder, final SearchResultObj.book book) {
                TextView name = holder.getView(R.id.item_book_name);
                name.setText(book.getTitle());
                TextView author = holder.getView(R.id.item_book_author);
                author.setText(book.getAuthor());
                TextView major = holder.getView(R.id.item_book_type);
                major.setText(book.getCat());
                TextView intro = holder.getView(R.id.item_book_intro);
                String introString = book.getShortIntro();
                if (introString.length() > 50){
                    introString = introString.substring(0,49)+"……";
                }
                intro.setText(introString);
                final ImageView cover = holder.getView(R.id.item_book_cover);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isNetWorkConnected(MainActivity.getContext())) {
                                URL url = new URL(BookService.StaticsUrl + book.getCover());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(10000);
                                if (connection.getResponseCode() == 200) {
                                    InputStream inputStream = connection.getInputStream();
                                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            cover.setImageBitmap(bitmap);
                                        }
                                    });
                                }
                            }
                            else{
                                Looper.prepare();
                                Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }).start();
            }
        };
        recyclerViewAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                final Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                final Bundle bundle = new Bundle();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetWorkConnected(MainActivity.getContext())) {
                            final BookObj t = getBookService().getBookById(results.get(position).get_id());
                            if (t != null)
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bundle.putSerializable("bookobj", t);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        resultList.setAdapter(recyclerViewAdapter);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        //设置模糊关联列表adapter
        final ArrayAdapter<String> fuzzyAdapter = new ArrayAdapter<>(this,R.layout.item_listview2,tempFuzzy);
        fuzzyList.setAdapter(fuzzyAdapter);


        //设置热门搜索函数
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text1.getText().toString();
                recordClick(s);
            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text2.getText().toString();
                recordClick(s);
            }
        });
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text3.getText().toString();
                recordClick(s);
            }
        });
        text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text4.getText().toString();
                recordClick(s);
            }
        });
        text5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text5.getText().toString();
                recordClick(s);
            }
        });
        text6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text6.getText().toString();
                recordClick(s);
            }
        });


        //设置搜索函数
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                isSubmit = true;
                //添加历史
                if (!histories.contains(s)){
                    histories.add(s);
                    getInstance(getBaseContext()).addSearchHistory(s);
                }
                //设置可见
                searchLayout.setVisibility(View.VISIBLE);
                fuzzyList.setVisibility(View.GONE);
                resultList.setVisibility(View.VISIBLE);
                //填充数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetWorkConnected(MainActivity.getContext())) {
                            SearchResultObj tt = getBookService().getSearchResultObj(s, 0, 8);
                            results.clear();
                            if (tt != null) {
                                List<SearchResultObj.book> t = tt.getBookList();
                                results.addAll(t);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(SearchActivity.this,"搜索不到相关内容",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                recyclerViewAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                //设置可见
                if (!isSubmit) {
                    if (s.equals("")) {
                        initialLayout.setVisibility(View.VISIBLE);
                        searchLayout.setVisibility(View.GONE);
                    }
                    else {
                        initialLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
                        fuzzyList.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.GONE);
                    }
                }
                else {
                    if (s.equals("")) {
                        fuzzyList.setVisibility(View.GONE);
                        resultList.setVisibility(View.VISIBLE);
                    }
                    else {
                        fuzzyList.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.GONE);
                    }
                }
                //填充数据
                if (!s.equals("")&&flag) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flag = false;
                            if (isNetWorkConnected(MainActivity.getContext())) {
                                SearchResultObj tt = getBookService().getSearchResultObj(s, 0, 8);
                                List<String> t = new ArrayList<>();
                                if (tt != null) {
                                    List<SearchResultObj.book> tss = tt.getBookList();
                                    int size = tss.size();
                                    for (int i = 0; i < size; i++)
                                        t.add(tss.get(i).getTitle());
                                }
                                tempFuzzy.clear();
                                if (t.size() > 0)
                                    tempFuzzy.addAll(t);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fuzzyAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            else {
                                Looper.prepare();
                                Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            flag = true;
                        }
                    }).start();
                    fuzzyAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        //设置历史删除函数
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance(getBaseContext()).deleteHistory();
                histories.clear();
                historyAdapter.notifyDataSetChanged();
            }
        });

        //设置历史点击函数
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String s = histories.get(position);
                recordClick(s);
            }
        });

        //模糊搜索点击函数
        fuzzyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String s = tempFuzzy.get(position);
                isSubmit = true;
                //添加历史
                if (!histories.contains(s)){
                    histories.add(s);
                    getInstance(getBaseContext()).addSearchHistory(s);
                }
                //设置可见
                initialLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                fuzzyList.setVisibility(View.GONE);
                resultList.setVisibility(View.VISIBLE);
                //填充数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetWorkConnected(MainActivity.getContext())) {
                            SearchResultObj tt = getBookService().getSearchResultObj(s, 0, 8);
                            List<SearchResultObj.book> t = tt.getBookList();
                            results.clear();
                            if (t != null)
                                results.addAll(t);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
                recyclerViewAdapter.notifyDataSetChanged();
                searchView.setQuery(s,true);
            }
        });
        //取消按钮点击事件
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //更换热门图书函数
        freshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freshPopBook();
            }
        });
    }
    public void recordClick(final String s) {
        isSubmit = true;
        //设置可见
        initialLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        fuzzyList.setVisibility(View.GONE);
        resultList.setVisibility(View.VISIBLE);
        //添加历史
        if (!histories.contains(s)){
            histories.add(s);
            getInstance(getBaseContext()).addSearchHistory(s);
        }
        //填充数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isNetWorkConnected(MainActivity.getContext())) {
                    SearchResultObj tt = getBookService().getSearchResultObj(s, 0, 8);
                    results.clear();
                    if (tt != null) {
                        List<SearchResultObj.book> t = tt.getBookList();
                        results.addAll(t);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(SearchActivity.this,"搜索不到相关内容",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
                else {
                    Looper.prepare();
                    Toast.makeText(SearchActivity.this,"网络似乎出现了点问题",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
        recyclerViewAdapter.notifyDataSetChanged();
        searchView.setQuery(s,true);
    }

    public void freshPopBook() {
        Random ra =new Random();
        int t = ra.nextInt(29);
        text1.setText(hotBooks[t]);
        t = (t+1)%30;
        text2.setText(hotBooks[t]);
        t = (t+1)%30;
        text3.setText(hotBooks[t]);
        t = (t+1)%30;
        text4.setText(hotBooks[t]);
        t = (t+1)%30;
        text5.setText(hotBooks[t]);
        t = (t+1)%30;
        text6.setText(hotBooks[t]);
    }
}
