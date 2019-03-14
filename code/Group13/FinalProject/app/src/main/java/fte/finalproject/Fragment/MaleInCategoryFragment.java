package fte.finalproject.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.CategoryActivity;
import fte.finalproject.R;
import fte.finalproject.ReadPageActivity;
import fte.finalproject.myRecyclerview.CategoryRecyObj;
import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.CategoryObj;
import fte.finalproject.obj.ClassificationObj1;
import fte.finalproject.obj.ClassificationObj2;
import fte.finalproject.obj.ShelfBookObj;
import fte.finalproject.service.BookService;

import static android.app.ProgressDialog.show;

//分类(男生/女生)界面
public class MaleInCategoryFragment extends Fragment {
    // Fragment 的 视图
    View view;

    // Fragment内的RecyclerView
    RecyclerView recyclerView;              // recyclerview
    List<CategoryRecyObj> myCategories;              // recyclerview中的书籍数据
    MyRecyclerViewAdapter<CategoryRecyObj> adapter;     // recyclerview 的 adapter

    // 判断是女性分类还是男性分类
    boolean isMale = true;

    // 分类数据OBJ
    private ClassificationObj1 classificationObj1;
    private ClassificationObj2 classificationObj2;

    // bookservice 类 （用单例模式获取）
    private BookService bookService;

    // 一级分类(写死)
    public static String[] maleCategoriesName = {"玄幻", "奇幻", "武侠", "仙侠", "都市", "职场", "历史", "军事", "游戏", "竞技", "科幻", "灵异", "同人", "轻小说"};
    public static String[] femaleCategoriesName = {"古代言情", "现代言情", "青春校园", "纯爱", "玄幻奇幻", "武侠仙侠", "科幻", "游戏竞技", "悬疑灵异", "同人", "女尊", "莉莉"};
    String[] maleCategoriesBookCount = {"(56万本)", "(6.3万本)", "(4.8万本)", "(15万本)", "(38万本)", "(1.8万本)", "(7.9万本)", "(1.6万本)", "(8.9万本)", "(6648本)", "(13万本)", "(5.3万本)", "(4.6万本)", "(9761本)"};
    String[] femaleCategoriesBookCount = {"(57万本)", "(71万本)", "(15万本)", "(13万本)", "(15万本)", "(7.8万本)", "(1.8万本)", "(6553本)", "(2.0万本)", "(12万本)", "(2.2万本)", "(2.6万本)"};



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取传给Fragment的参数
        Bundle bundle = getArguments();
        this.isMale = bundle.getBoolean("isMale");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 获取 Fragment 视图
        view = inflater.inflate(R.layout.fragment_male_in_category, null);

        // 获取分类数据
        getMyCategories();

        // 设置 RecyclerView
        setRecyclerView();

        // Inflate the layout for this fragment
        return view;
    }




    // 设置 RecyclerView
    private void setRecyclerView() {
        // 获取页面的 RecyclerView 控件
        recyclerView = view.findViewById(R.id.fragment_male_in_category_recyclerview);

        // 设置 RecyclerView 的布局方式
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // 设置 Adapter 配置
        adapter = new MyRecyclerViewAdapter<CategoryRecyObj>(getActivity(), R.layout.item_category, myCategories) {
            @Override
            public void convert(MyViewHolder holder, CategoryRecyObj categoryRecyObj) {
                TextView categoryName = holder.getView(R.id.item_category_name);
                categoryName.setText(categoryRecyObj.getCategoryName());
                TextView categoryBookCount = holder.getView(R.id.item_category_count);
                categoryBookCount.setText(categoryRecyObj.getBookCount());
            }
        };

        // 每个主类按钮的点击响应处理
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转具体分类界面
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRanking", false);
                bundle.putBoolean("isMale", isMale);
                if (isMale) bundle.putString("title", maleCategoriesName[position]);
                else bundle.putString("title", femaleCategoriesName[position]);
                intent.putExtras(bundle);
                startActivity(intent);

                /*System.out.println("onClick");

                // 分类信息尚未获取，调用BookService去获取
                if(classificationObj2 == null) {
                    boolean isNetWorkConnected = isNetWorkConnected(getActivity());
                    // 有网络
                    if(isNetWorkConnected) {
                        System.out.println("网络连接状况：已连接");
                        // 调用子线程进行访问，获取一级、二级分类信息
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                classificationObj2 = bookService.getClassification2();
                                Looper.prepare();
                                if(classificationObj2 == null) {
                                    Toast.makeText(getActivity(), "还是获取失败啊啊啊啊", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            }
                        });
                        thread.start();

                    }
                    // 无网络
                    else {
                        System.out.println("网络连接状况：未连接");
                        Toast.makeText(getActivity(), "网络连接状况：未连接", Toast.LENGTH_SHORT).show();
                    }
                }
                // 已经获取过分类信息
                else {
                    // 获取点击到的一级分类以及对应的二级分类数据
                    String major;
                    String[] mins;
                    if(isMale) {
                        major = maleCategoriesName[position];
                        mins = classificationObj2.getMaleList().get(position).getMins();
                    }
                    else {
                        major = femaleCategoriesName[position];
                        mins = classificationObj2.getFemaleList().get(position).getMins();
                    }
                    // 获取失败
                    if(major == null || mins == null) {
                        Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                    }
                    // 获取数据成功
                    else {
                        //Toast.makeText(getActivity(), major + "二级分类框弹出", Toast.LENGTH_SHORT).show();
                        // 弹出一个二级分类框
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_category);
                        // 二级分类RecyclerView
                        RecyclerView dialog_recyclerview = dialog.findViewById(R.id.dialog_category_recyclerview);
                        // 二级分类数据初始化
                        List<CategoryRecyObj> dialog_categories = new ArrayList<>();
                        for(int i = 0; i < mins.length; i++)
                            dialog_categories.add(new CategoryRecyObj(mins[i], ""));

                        // 设置 Adapter
                        MyRecyclerViewAdapter<CategoryRecyObj> dialog_adapter = new MyRecyclerViewAdapter<CategoryRecyObj>(getActivity(), R.layout.item_category, dialog_categories) {
                            @Override
                            public void convert(MyViewHolder holder, CategoryRecyObj categoryRecyObj) {
                                TextView categoryName = holder.getView(R.id.item_category_name);
                                categoryName.setText(categoryRecyObj.getCategoryName());
                                TextView categoryBookCount = holder.getView(R.id.item_category_count);
                                categoryBookCount.setVisibility(View.GONE);
                            }
                        };
                        // 适配 Adapter
                        dialog_recyclerview.setAdapter(dialog_adapter);
                        // 设置 RecyclerView 布局
                        dialog_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                        // 设置 dialog 属性并显示
                        dialog.setCancelable(true);
                        dialog.show();
                    }



                }*/
            }

            @Override
            public void onLongClick(int position) {

            }
        });

        // 给 RecyclerView 适配 Adapter
        recyclerView.setAdapter(adapter);

    }

    // 获取分类数据
    private void getMyCategories() {
        boolean isNetWorkConnected = isNetWorkConnected(getActivity());
        bookService = BookService.getBookService();
        if(isNetWorkConnected) {
            System.out.println("网络连接状况：已连接");
            // 调用子线程进行访问，获取一级、二级分类信息
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    classificationObj2 = bookService.getClassification2();
                    Looper.prepare();
                    if(classificationObj2 == null) {
                        Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                }
            });
            thread.start();
        }
        else {
            System.out.println("网络连接状况：未连接");
            Toast.makeText(getActivity(), "网络连接状况：未连接", Toast.LENGTH_SHORT).show();
        }
        myCategories = new ArrayList<>();
        // 男生向小说分类
        if(isMale) {
            for(int i = 0; i < maleCategoriesName.length; i++) {
                CategoryRecyObj c = new CategoryRecyObj(maleCategoriesName[i], maleCategoriesBookCount[i]);
                myCategories.add(c);
            }
        }
        // 女生向小说分类
        else {
            for(int i = 0; i < femaleCategoriesName.length; i++) {
                CategoryRecyObj c = new CategoryRecyObj(femaleCategoriesName[i], femaleCategoriesBookCount[i]);
                myCategories.add(c);
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

}
