package fte.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.LocaleData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fte.finalproject.control.DatabaseControl;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.CategoryObj;
import fte.finalproject.obj.ChapterLinkObj;
import fte.finalproject.obj.RecomListObj;
import fte.finalproject.obj.ShelfBookObj;
import fte.finalproject.service.BookService;

public class BookDetailActivity extends AppCompatActivity {

    RadioButton addButton;
    RadioButton readButton;
//    RadioButton downloadButton;
    Button moreButton;
    Button recom1;
    Button recom2;
    Button recom3;

    ImageView back;
    TextView pageTitle;
    ImageView bookCover;
    TextView bookTitle;
    TextView bookInfo;
    TextView updateTime;
    TextView follower;
    TextView retentionRatio;
    TextView bookIntro;
    RadioGroup recomRG;

    // 书籍对象
    private BookObj bookObj;

    // 同类书籍
    private CategoryObj categoryObj;

    private List<BookObj> bookObjs = new ArrayList<>();

    private Bitmap cover;

    private List<ChapterLinkObj> linkList;

    private StringBuilder stringBuilder = new StringBuilder();

    Handler mHandler = new Handler();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-mm'T'HH:MM:SS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // 获取控件
        back = findViewById(R.id.detail_back);
        pageTitle = findViewById(R.id.detail_title);
        addButton = findViewById(R.id.detail_bottom_add);
        readButton = findViewById(R.id.detail_bottom_read);
//        downloadButton = findViewById(R.id.detail_bottom_download);
        moreButton = findViewById(R.id.detail_more);
        bookCover = findViewById(R.id.detail_cover);
        bookTitle = findViewById(R.id.detail_bookTitle);
        bookInfo = findViewById(R.id.detail_TV);
        updateTime = findViewById(R.id.detail_update);
        follower = findViewById(R.id.detail_follower2);
        retentionRatio = findViewById(R.id.detail_retentionRatio2);
        bookIntro = findViewById(R.id.detail_longIntro);
        recomRG = findViewById(R.id.detail_recomRG);
        recom1 = findViewById(R.id.detail_recom1);
        recom2 = findViewById(R.id.detail_recom2);
        recom3 = findViewById(R.id.detail_recom3);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        bookObj = (BookObj)bundle.getSerializable("bookobj");

        // 获取封面图片
        final String iconURL = BookService.StaticsUrl +  bookObj.getCover();
        final Matrix largeMatrix = new Matrix();
        final Matrix littleMatrix = new Matrix();
        largeMatrix.postScale((float)2, (float)2);
<<<<<<< HEAD
        littleMatrix.postScale((float)0.4, (float)0.4);
        if (!isNetWorkConnected(BookDetailActivity.this)) {
            Toast.makeText(BookDetailActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
        } else {
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
                            cover = BitmapFactory.decodeStream(inputStream);
                            if (cover != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("width", String.valueOf(cover.getWidth()));
                                        Log.d("height", String.valueOf(cover.getHeight()));
=======
        littleMatrix.postScale((float)0.5, (float)0.5);
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
                        cover = BitmapFactory.decodeStream(inputStream);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("width", String.valueOf(cover.getWidth()));
                                Log.d("height", String.valueOf(cover.getHeight()));
>>>>>>> eaf08877886fb2f3c115f195a6d60728959191ee
                                /*if (cover.getWidth() <= 150 && cover.getHeight() <= 200) {
                                    bookCover.setImageBitmap(Bitmap.createBitmap(cover, 0, 0, cover.getWidth(), cover.getHeight(), largeMatrix, true));
                                } else if (cover.getWidth() > 300 && cover.getHeight() > 400) {
                                    bookCover.setImageBitmap(Bitmap.createBitmap(cover, 0, 0, cover.getWidth(), cover.getHeight(), littleMatrix, true));
                                } else {
                                    bookCover.setImageBitmap(cover);
                                }*/
<<<<<<< HEAD
                                        bookCover.setImageBitmap(cover);
                                    }
                                });
=======
                                bookCover.setImageBitmap(cover);
>>>>>>> eaf08877886fb2f3c115f195a6d60728959191ee
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }).start();
        }

        if (!isNetWorkConnected(BookDetailActivity.this)) {
            Toast.makeText(BookDetailActivity.this, "未连接网络", Toast.LENGTH_SHORT).show();
        } else {
            // 获取书籍相关信息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bookObj = BookService.getBookService().getBookById(bookObj.getId());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            bookTitle.setText(bookObj.getTitle());
                            pageTitle.setText(bookObj.getTitle());
                            int wordNum = bookObj.getWordCount() / 10000;
                            bookInfo.setText(bookObj.getAuthor() + " | " + bookObj.getMinorCate() + " | " + String.valueOf(wordNum) + "万字");
                            String updateStr = "";
                            try {
                                Date date = format.parse(bookObj.getUpdated());
                                Date now = new Date(System.currentTimeMillis());
                                long period = now.getTime() - date.getTime();
                                Log.d("period", String.valueOf(period));
                                if (period / 86400000 < 1) {
                                    updateStr = "上次更新: 今天";
                                } else if (period / 86400000 > 1) {
                                    updateStr = "上次更新: " + String.valueOf(period / 86400000) + "天前";
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            updateTime.setText(updateStr);
                            // int followerNum = bookObj.getLatelyFollower() / 10000;
                            follower.setText(String.valueOf(bookObj.getLatelyFollower()) + "人");
                            retentionRatio.setText(bookObj.getRetentionRatio() + "%");
                            String intro = bookObj.getLongIntro();
                            if (intro.length() > 80) intro = intro.substring(0, 80);
                            intro += "...";
                            bookIntro.setText(intro);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    categoryObj = BookService.getBookService().getBooksByCategoty("reputation", bookObj.getMajorCate(), 0, 10, bookObj.getGender()[0]);
                                    Log.d("size", String.valueOf(categoryObj.getBooks().size()));
                                    for (int i = 0; i < categoryObj.getBooks().size(); i++) {
                                        final int j = i;
                                        final BookObj temp = BookService.getBookService().getBookById(categoryObj.getBooks().get(j).getId());
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                bookObjs.add(temp);
                                                if (j < 3) {
                                                    final String iconURL = BookService.StaticsUrl +  temp.getCover();
                                                    final Button button = (Button)recomRG.getChildAt(j);
                                                    button.setText(bookObjs.get(j).getTitle());
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
                                                                            Drawable drawable = null;
                                                                            drawable = new BitmapDrawable(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), littleMatrix, true));
                                                                            drawable.setBounds(0, 0, 270, 360);
                                                                            button.setCompoundDrawables(null, drawable, null, null);
                                                                        }
                                                                    });
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();
                                                }
                                            }
                                        });


                                    }
                                }
                            }).start();
                        }
                    });
                }
            }).start();
        }

        recom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookobj", bookObjs.get(0));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookobj", bookObjs.get(1));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookobj", bookObjs.get(2));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (DatabaseControl.getInstance(BookDetailActivity.this).judgeBookExist(bookObj.getId())) {
            // 已存在
            setButtonToDelete();
        } else {
            // 不存在
            setButtonToAdd();
        }

        // 阅读
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, ReadPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bookid", bookObj.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 下载
        /*downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < linkList.size(); i++) {
                            ChapterObj chapterObj = BookService.getBookService().getChapterByLink(linkList.get(i).getLink());
                            stringBuilder.append(chapterObj.getIchapter().getTitle());
                            stringBuilder.append("\n");
                            stringBuilder.append(chapterObj.getIchapter().getBody());
                        }

                    }
                }).start();*//*
                Toast.makeText(BookDetailActivity.this, "功能开发中，敬请期待", Toast.LENGTH_SHORT).show();
            }
        });*/

        // 查看更多同类书籍
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, RecomActivity.class);
                Bundle bundle = new Bundle();
                RecomListObj recomListObj = new RecomListObj(bookObjs);
                bundle.putSerializable("list", recomListObj);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setButtonToAdd() {
        addButton.setText("加入书架");
        addButton.setTextColor(getResources().getColor(R.color.colorRed));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseControl.getInstance(BookDetailActivity.this).addShelfBook(new ShelfBookObj(bookObj.getId(), bookObj.getTitle(), cover, bookObj.getCover(),0, "online", 0, bookObj.getLongIntro(), bookObj.getAuthor(), bookObj.getMajorCate()));
                Toast.makeText(BookDetailActivity.this, "已添加《" + bookObj.getTitle() + "》", Toast.LENGTH_SHORT).show();
                setButtonToDelete();
            }
        });
    }

    private void setButtonToDelete() {
        addButton.setText("移除书架");
        addButton.setTextColor(getResources().getColor(R.color.colorGrey));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseControl.getInstance(BookDetailActivity.this).deleteShelfBook(bookObj.getId());
                Toast.makeText(BookDetailActivity.this, "已移除《" + bookObj.getTitle() + "》", Toast.LENGTH_SHORT).show();
                setButtonToAdd();
            }
        });
    }

    // 辅助函数：判断网络是否连接
    private boolean isNetWorkConnected(Context context) {
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
