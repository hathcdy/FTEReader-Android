package fte.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.RecomListObj;
import fte.finalproject.service.BookService;

public class RecomActivity extends AppCompatActivity {

    private List<BookObj> bookObjs;

    private RecyclerView recyclerView;

    private ImageView back;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom);

        // 获取同类书籍
        Bundle bundle = getIntent().getExtras();
        bookObjs = ((RecomListObj)bundle.getSerializable("list")).getBookObjs();

        // 获取相关控件
        back = findViewById(R.id.recom_back);
        recyclerView = findViewById(R.id.recom_recyclerview);

        // 内容填充
        MyRecyclerViewAdapter<BookObj> adapter = new MyRecyclerViewAdapter<BookObj>(RecomActivity.this, R.layout.item_book, bookObjs) {
            @Override
            public void convert(MyViewHolder holder, BookObj bookObj) {
                final ImageView imageView = holder.getView(R.id.item_book_cover);
                TextView bookName = holder.getView(R.id.item_book_name);
                TextView bookAuthor = holder.getView(R.id.item_book_author);
                TextView bookType = holder.getView(R.id.item_book_type);
                TextView bookIntro = holder.getView(R.id.item_book_intro);
                bookName.setText(bookObj.getTitle());
                bookType.setText(bookObj.getMajorCate());
                bookAuthor.setText(bookObj.getAuthor());
                String intro = bookObj.getLongIntro();
                if (intro.length() > 50) intro = intro.substring(0, 50);
                intro += "...";
                bookIntro.setText(intro);

                //通过网络获取书籍图标
                final String iconURL = BookService.StaticsUrl +  bookObj.getCover();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(iconURL);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10000);
                            if (connection.getResponseCode() == 200) {
                                InputStream inputStream = connection.getInputStream();
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }).start();
            }
        };
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //跳转到书籍详情界面
                Intent intent = new Intent(RecomActivity.this, BookDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookobj", bookObjs.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RecomActivity.this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
