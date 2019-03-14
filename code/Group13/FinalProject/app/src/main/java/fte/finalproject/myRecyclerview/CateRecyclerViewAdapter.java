package fte.finalproject.myRecyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fte.finalproject.R;
import fte.finalproject.obj.BookObj;
import fte.finalproject.service.BookService;

//用于排行榜、分类界面的RecyclerView的Adapter
public class CateRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<BookObj> data;
    private boolean isRanking;
    private OnItemClickListener onItemClickListener;

    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示

    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler


    public CateRecyclerViewAdapter(List<BookObj> data, Context context, boolean hasMore, boolean isRanking) {
        this.data = data;
        this.context = context;
        this.hasMore = hasMore;
        this.isRanking = isRanking;
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //根据不同的ViewType绑定不同的布局文件
        if (viewType == normalType) {
            MyViewHolder holder = MyViewHolder.get(context, viewGroup, R.layout.item_book);
            holder.setNormalTypeType(true);
            return holder;
        }
        else {
            MyViewHolder holder = MyViewHolder.get(context, viewGroup, R.layout.recycler_footer_view);
            holder.setNormalTypeType(false);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (data.isEmpty()) return;
        if (holder.isNormalType() == true) {//是正常的布局
            BookObj bookObj = data.get(position);
            if (isRanking && !data.isEmpty()) {
                ImageView rankingImg = holder.getView(R.id.item_book_rankingImg);
                if (position == 0) {//排行榜第一名
                    rankingImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.first, null));
                    rankingImg.setVisibility(View.VISIBLE);
                } else if (position == 1) {//排行榜第二名
                    rankingImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.second, null));
                    rankingImg.setVisibility(View.VISIBLE);
                } else if (position == 2) {//排行榜第三名
                    rankingImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.third, null));
                    rankingImg.setVisibility(View.VISIBLE);
                }
                else rankingImg.setVisibility(View.GONE);
            }
            final ImageView imageView = holder.getView(R.id.item_book_cover);
            TextView bookName = holder.getView(R.id.item_book_name);
            TextView bookAuthor = holder.getView(R.id.item_book_author);
            TextView bookType = holder.getView(R.id.item_book_type);
            TextView bookIntro = holder.getView(R.id.item_book_intro);
            TextView followers = holder.getView(R.id.item_book_followers);
            TextView retention = holder.getView(R.id.item_book_retention);
            int num = bookObj.getLatelyFollower();
            if (num < 10000) {
                followers.setText(bookObj.getLatelyFollower() + "人追");
            }
            else {
                num /= 10000;
                followers.setText(num + "万人追");
            }
            followers.setVisibility(View.VISIBLE);
            retention.setText(bookObj.getRetentionRatio() + "%留存率");
            retention.setVisibility(View.VISIBLE);
            bookName.setText(bookObj.getTitle());
            bookType.setText(bookObj.getMajorCate());
            bookAuthor.setText(bookObj.getAuthor());
            bookIntro.setText(bookObj.getShortIntro());

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
                }}).start();
        }
        else {//是提示加载信息的布局
            final ProgressBar progressBar = holder.getView(R.id.footer_view_progessBar);
            final TextView tips = holder.getView(R.id.footer_view_text);
            tips.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore == true) {
                // 不隐藏footView提示
                fadeTips = false;
                if (data.size() > 0) {
                    // 如果查询数据发现增加之后，就显示正在加载更多数据
                    tips.setText("正在加载更多数据...");
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                if (data.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    tips.setText("没有更多数据了");
                    // 隐藏提示条
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏提示条
                            tips.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            // 将fadeTips设置true
                            fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
        //设置监听器
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    //获取列表中数据源的最后一个位置
    public int getLastPosition() {
        return data.size();
    }

    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    //下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        data = new ArrayList<>();
    }

    //更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<BookObj> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            data.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }
}
