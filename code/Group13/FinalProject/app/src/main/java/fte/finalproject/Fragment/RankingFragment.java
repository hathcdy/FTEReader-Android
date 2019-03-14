package fte.finalproject.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.CategoryActivity;
import fte.finalproject.MainActivity;
import fte.finalproject.R;
import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.myRecyclerview.RankRecyObj;

//排行榜界面
public class RankingFragment extends Fragment {
    private boolean isMale;             //男生/女生
    private int color1 = Color.parseColor("#F5D6D6");//最热榜
    private int color2 = Color.parseColor("#D8F8C2");//热搜榜
    private int color3 = Color.parseColor("#F9EBB0");//潜力榜
    private int color4 = Color.parseColor("#DBB5F4");//留存榜
    private int color5 = Color.parseColor("#CEFDFD");//完结榜

    private RecyclerView recyclerView;
    private List<String> list = new ArrayList<>();
    private MyRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.isMale = bundle.getBoolean("isMale");
        if (isMale) {
            list.add("最热榜");
            list.add("热搜榜");
            list.add("潜力榜");
            list.add("留存榜");
            list.add("完结榜");
        }
        else {
            list.add("热搜榜");
            list.add("留存榜");
            list.add("最热榜");
            list.add("潜力榜");
            list.add("完结榜");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, null);
        recyclerView = view.findViewById(R.id.ranking_recycler);
        //设置recyclerView的显示
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter<String>(getActivity(), R.layout.item_ranking, list) {
            @Override
            public void convert(MyViewHolder holder, String type) {
                Bitmap bitmap = null;
                int color = color1;
                switch (type) {
                    case "最热榜":
                        if (isMale) bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.male_icon1, null);
                        else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.female_icon1, null);
                        color = color1;
                        break;
                    case "热搜榜":
                        if (isMale) bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.male_icon2, null);
                        else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.female_icon2, null);
                        color = color2;
                        break;
                    case "潜力榜":
                        if (isMale) bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.male_icon3, null);
                        else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.female_icon3, null);
                        color = color3;
                        break;
                    case "留存榜":
                        if (isMale) bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.male_icon4, null);
                        else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.female_icon4, null);
                        color = color4;
                        break;
                    case "完结榜":
                        if (isMale) bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.male_icon5, null);
                        else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.female_icon5, null);
                        color = color5;
                        break;
                }
                ImageView imageView = holder.getView(R.id.ranking_item_image);
                imageView.setImageBitmap(bitmap);
                TextView textView = holder.getView(R.id.ranking_item_text);
                textView.setText(type);
                RelativeLayout layout = holder.getView(R.id.ranking_item_right);
                layout.setBackgroundColor(color);
            }
        };
        //设置RecyclerView的点击响应事件
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转到对应榜单界面
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRanking", true);
                bundle.putBoolean("isMale", isMale);
                bundle.putString("title", list.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
}
