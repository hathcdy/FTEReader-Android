package fte.finalproject.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fte.finalproject.R;
import fte.finalproject.ReadPageActivity;
import fte.finalproject.control.DatabaseControl;
import fte.finalproject.myRecyclerview.MyRecyclerViewAdapter;
import fte.finalproject.myRecyclerview.MyViewHolder;
import fte.finalproject.obj.ShelfBookObj;

//书架界面
public class BookShelfFragment extends Fragment {
    // Fragment 的 视图
    View view;

    // Fragment内的RecyclerView
    RecyclerView recyclerView;      // recyclerview
    List<ShelfBookObj> myBooks;            // recyclerview中的书籍数据
    MyRecyclerViewAdapter<ShelfBookObj> adapter;   // recyclerview 的 adapter

    // book-item 项的各个控件
    private ImageView book_cover_imageview_control;
    private TextView book_name_textview_control;
    private TextView book_author_textview_control;
    private TextView book_type_textview_control;
    private TextView book_intro_textview_control;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 获取 Fragment 的视图
        view = inflater.inflate(R.layout.fragment_book_shelf, null);

        // 获取书籍数据
        getMyBooks();

        // 设置页面的 RecyclerView
        setRecyclerView();

        // 返回视图
        return view;
    }

    @Override
    public void onResume() {
        getMyBooks();
        setRecyclerView();
        //Toast.makeText(getActivity(), "进入Fragment" + " | " + myBooks.size(), Toast.LENGTH_SHORT).show();
//        System.out.println("onResume: " + myBooks.get(0).getReadChapter());
        //System.out.println("横竖屏状态：MainActivity-BookShelfFragment: " + DatabaseControl.getInstance(getActivity()).get_Hor_Or_Ver_Screen_Status(0));
        super.onResume();
    }

    // 获取书籍数据
    private void getMyBooks() {
        //Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.mipmap.bookcover)).getBitmap();
        /*ShelfBookObj test = new ShelfBookObj("5816b415b06d1d32157790b1", "圣墟", bitmap, 0, "testAddress", 0, "testDescription");
        DatabaseControl.getInstance(getActivity()).addShelfBook(test);*/
        myBooks = DatabaseControl.getInstance(getActivity()).getAllShelfBook();
        //System.out.println("getBookSize: " + myBooks.size());
    }

    // 设置页面的 RecyclerView
    private void setRecyclerView() {
        // 获取页面的 RecyclerView 控件
        recyclerView = view.findViewById(R.id.fragment_book_shelf_recyclerview);

        // 设置 RecyclerView 的布局方式
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 设置 RecyclerAdapter
        adapter = new MyRecyclerViewAdapter<ShelfBookObj>(getActivity(), R.layout.item_book, myBooks) {
            @Override
            public void convert(MyViewHolder holder, ShelfBookObj shelfBookObj) {
                // 书名
                TextView bookName = holder.getView(R.id.item_book_name);
                bookName.setText(shelfBookObj.getName());
                // 封面
                ImageView imageView = holder.getView(R.id.item_book_cover);
                imageView.setImageBitmap(shelfBookObj.getIcon());
                // 作者
                TextView author = holder.getView(R.id.item_book_author);
                author.setText(shelfBookObj.getAuthor());
                // 类型
                TextView major = holder.getView(R.id.item_book_type);
                major.setText(shelfBookObj.getMajor());
                // 简介
                TextView intro = holder.getView(R.id.item_book_intro);
                String str = shelfBookObj.getDescription();
                if (str.length() > 50) str = str.substring(0, 50);
                str += "...";
                intro.setText(str);
            }
        };
        adapter.refresh(myBooks);
        adapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), ReadPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bookid", myBooks.get(position).getBookId());
                bundle.putInt("currentChapter", myBooks.get(position).getReadChapter());
                bundle.putString("bookname", myBooks.get(position).getName());
                System.out.println("传进去当前阅读章节数为：" + myBooks.get(position).getReadChapter());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(final int position) {
                // 长按弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("确定要从书架移除" + myBooks.get(position).getName() + "吗？");
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseControl.getInstance(getActivity()).deleteShelfBook(myBooks.get(position).getBookId());
                        getMyBooks();
                        setRecyclerView();
                    }
                });
                dialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
