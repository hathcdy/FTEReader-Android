package fte.finalproject.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fte.finalproject.R;

//阅读界面的Fragment
public class ReadPageFragment extends Fragment {
    // 页面数据
    private String title;       //章节名
    private String content;     //本章内容
    int day_or_night_status;    // 日间或夜间模式
    int textSize;               //  字体大小

    // 帧页面View
    View view;
    // 帧页面控件
    private TextView titile_control;        // 标题
    private TextView content_control;       // 阅读页内容
    private FrameLayout whole_layout_control;    // 整个框页面


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.content = getArguments().getString("content");
        this.title = getArguments().getString("title");
        this.day_or_night_status = getArguments().getInt("day_or_night_status");
        this.textSize = getArguments().getInt("textSize");
        //System.out.println("日间夜间：" + day_or_night_status);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取帧页面View
        view = inflater.inflate(R.layout.fragment_read_page, null);

        // 获取页面控件
        init_page_control();

        // 设置页面内容
        init_page_info();

        //todo
        return view;
    }

    private void init_page_info() {
        titile_control.setText(title);      // 设置标题
        content_control.setText(content);   // 设置阅读页内容
        content_control.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        if(day_or_night_status == 0) {
            whole_layout_control.setBackgroundColor(getResources().getColor(R.color.PapayaWhip));
        }
        else whole_layout_control.setBackgroundColor(getResources().getColor(R.color.nightBackGround));
        //progress_control.setText(Integer.toString(currentChapter+1) + "/" + Integer.toString(totalChapter+1));
    }

    private void init_page_control() {
        titile_control = view.findViewById(R.id.fragment_read_page_title);
        content_control = view.findViewById(R.id.fragment_read_page_content);
        whole_layout_control = view.findViewById(R.id.fragment_read_page_whole_layout);
        //progress_control = view.findViewById(R.id.fragment_read_page_process);
    }
}
