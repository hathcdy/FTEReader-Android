package fte.finalproject.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fte.finalproject.BookDetailActivity;
import fte.finalproject.R;
import fte.finalproject.myRecyclerview.CateRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.AllRankingObj;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.CategoryObj;
import fte.finalproject.obj.SingleRankingObj;
import fte.finalproject.service.BookService;

//具体分类和榜单的Fragment
public class DetailCategoryFragment extends Fragment {
    private boolean isRanking;      //排行榜/具体分类
    private boolean isMale;         //男生/女生
    private String title;           //榜单名/类型名
    private String type;            //具体榜单/具体类型

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BookObj> bookObjList = new ArrayList<>();
    private CateRecyclerViewAdapter recyclerViewAdapter;

    //具体榜单的id
    private String rankingid = "";

    private int total;                  //书籍总数
    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 10;

    Handler handler = new Handler();
    BookService bookService = BookService.getBookService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isRanking = getArguments().getBoolean("isRanking");
        this.isMale = getArguments().getBoolean("isMale");
        if (isRanking == false) {
            Log.d("type in args:", getArguments().getString("type"));
            switch (getArguments().getString("type")) {
                case "热门":
                    this.type = "hot";
                    break;
                case "新书":
                    this.type = "new";
                    break;
                case "好评":
                    this.type = "reputation";
                    break;
                case "完结":
                    this.type = "over";
                    break;
            }
        }
        else this.type = getArguments().getString("type");
        this.title = getArguments().getString("title");

        initBookList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_category, null);

        progressBar = view.findViewById(R.id.detail_category_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.detail_category_swipeRefresh);

        //设置RecyclerView
        recyclerView = view.findViewById(R.id.detail_category_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter = new CateRecyclerViewAdapter(getBookList(0, PAGE_COUNT), getActivity(), getBookList(0, PAGE_COUNT).size() > 0 ? true : false, isRanking);
        recyclerViewAdapter.setOnItemClickListener(new CateRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转到书籍详情界面
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookobj", bookObjList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

        //设置下拉显示的动画颜色
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE);
        //下拉刷新的回调事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //数据刷新
                initBookList();
            }
        });

        //设置滑动监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 在newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果没有隐藏footView，那么最后一个条目的位置就比我们的getItemCount少1
                    if (recyclerViewAdapter.isFadeTips() == false && lastVisibleItem + 1 == recyclerViewAdapter.getItemCount()) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(recyclerViewAdapter.getLastPosition(), recyclerViewAdapter.getLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }

                    // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                    if (recyclerViewAdapter.isFadeTips() == true && lastVisibleItem + 2 == recyclerViewAdapter.getItemCount()) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 然后调用updateRecyclerview方法更新RecyclerView
                                updateRecyclerView(recyclerViewAdapter.getLastPosition(), recyclerViewAdapter.getLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        return view;
    }

    void initBookList() {
        bookObjList.clear();
        //检查网络连接
        ConnectivityManager connect = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connect.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            Toast.makeText(getActivity(), "网络连接状况：未连接", Toast.LENGTH_LONG).show();
            return;
        }
        if (isRanking) getRankingBookList();
        else getCateBookList();
    }

    //获取排行榜书单
    void getRankingBookList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AllRankingObj allRankingObj = bookService.getAllRankingObj();
                if (allRankingObj == null || allRankingObj.isOk() == false) {
                    Looper.prepare();
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    Log.d("error", "获取全部排行榜失败");
                    return;
                }
                //获取具体榜单的id
                if (isMale) {   //男生
                    for (AllRankingObj.subClass subClass : allRankingObj.getMaleList()) {
                        if (subClass.getShortTitle().equals(title)) {
                            if (title.equals("热搜榜")) rankingid = subClass.getId();
                            else {
                                if (type.equals("周榜")) rankingid = subClass.getId();
                                else if (type.equals("月榜")) rankingid = subClass.getMonthRank();
                                else if (type.equals("总榜")) rankingid = subClass.getTotalRank();
                                else {
                                    System.exit(1);
                                    Log.d("error", "榜单名错误！");
                                }
                            }
                            break;
                        }
                    }
                }
                else {          //女生
                    for (AllRankingObj.subClass subClass : allRankingObj.getFemaleList()) {
                        if (subClass.getShortTitle().equals(title)) {
                            if (title.equals("热搜榜")) rankingid = subClass.getId();
                            else {
                                if (type.equals("周榜")) rankingid = subClass.getId();
                                else if (type.equals("月榜")) rankingid = subClass.getMonthRank();
                                else if (type.equals("总榜")) rankingid = subClass.getTotalRank();
                                else {
                                    System.exit(1);
                                    Log.d("error", "榜单名不符！");
                                }
                            }
                            break;
                        }
                    }
                }
                //得到id后再获取具体榜单的书籍信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SingleRankingObj singleRankingObj = bookService.getSingleRankingObj(rankingid);
                        if (singleRankingObj == null || singleRankingObj.isOk() == false) {
                            Looper.prepare();
                            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_LONG).show();
                            Looper.loop();
                            Log.d("error", "获取单一排行榜失败");
                            return;
                        }
                        List<BookObj> objList = singleRankingObj.getRanking().getBookList();
                        total = singleRankingObj.getRanking().getTotal();
                        for (int i = 0; i < objList.size(); ++i) {
                            BookObj bookObj = objList.get(i);
                            String intro = bookObj.getShortIntro();
                            if (intro.length() > 50) intro = intro.substring(0, 50);
                            intro += "...";
                            bookObj.setShortIntro(intro);
                            bookObjList.add(bookObj);
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateRecyclerView(0, PAGE_COUNT);
                                progressBar.setVisibility(View.GONE);
                                //数据加载完毕时取消动画
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getActivity(), "数据刷新完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        }).start();
    }

    //获取具体分类书单
    void getCateBookList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String gender = (isMale == true) ? "male" : "female";
                Log.d("type:", "" + type);
                final CategoryObj categoryObj = bookService.getBooksByCategoty(type, title, 0, 30000, gender);
                if (categoryObj == null || categoryObj.isOk() == false) {
                    Looper.prepare();
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    Log.d("error", "获取主题书单列表失败");
                    return;
                }
                total = categoryObj.getTotal();
                for (BookObj bookObj : categoryObj.getBooks()) {
                    if (bookObj.getShortIntro().length() > 50){
                        String intro = bookObj.getShortIntro();
                        intro = intro.substring(0, 50);
                        intro += "...";
                        bookObj.setShortIntro(intro);
                    }
                    bookObjList.add(bookObj);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getActivity(), "" + bookObjList.size() + " | " + total + " | " + categoryObj.getBooks().size(), Toast.LENGTH_SHORT).show();
                        updateRecyclerView(0, PAGE_COUNT);
                        progressBar.setVisibility(View.GONE);
                        //数据加载完毕时取消动画
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "数据刷新完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<BookObj> newDatas = getBookList(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            recyclerViewAdapter.updateList(newDatas, true);
        } else {
            recyclerViewAdapter.updateList(null, false);
        }
    }

    List<BookObj> getBookList(int from, int to) {
        List<BookObj> newList = new ArrayList<>();
        for (int i = from; i < to && i < bookObjList.size(); ++i) {
            newList.add(bookObjList.get(i));
        }
        return newList;
    }
}
