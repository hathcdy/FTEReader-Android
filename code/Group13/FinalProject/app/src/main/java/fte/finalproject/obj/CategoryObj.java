package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryObj {
    // 书籍总数
    @SerializedName("total")
    private int total;

    // 书籍列表
    @SerializedName("books")
    private List<BookObj> books;

    // 获取结果
    @SerializedName("ok")
    private boolean ok;

    public int getTotal() {
        return total;
    }

    public List<BookObj> getBooks() {
        return books;
    }

    public boolean isOk() {
        return ok;
    }
}
