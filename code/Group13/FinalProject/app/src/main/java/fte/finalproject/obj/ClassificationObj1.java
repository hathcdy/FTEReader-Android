package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// 获取一级分类返回结果
public class ClassificationObj1 {
    // male分类
    @SerializedName("male")
    private List<SubClass1> maleList;

    // female分类
    @SerializedName("female")
    private List<SubClass1> femaleList;

    // picture分类
    @SerializedName("picture")
    private List<SubClass1> picList;

    @SerializedName("press")
    private List<SubClass1> pressList;

    @SerializedName("ok")
    private boolean ok;

    public List<SubClass1> getMaleList() {
        return maleList;
    }

    public List<SubClass1> getFemaleList() {
        return femaleList;
    }

    public List<SubClass1> getPicList() {
        return picList;
    }

    public List<SubClass1> getPressList() {
        return pressList;
    }

    public boolean isOk() {
        return ok;
    }

    // 一级分类
    public class SubClass1 {
        // 名称
        @SerializedName("name")
        private String name;

        // 书籍数量
        @SerializedName("bookCount")
        private int bookCount;

        // monthlyCount?
        @SerializedName("monthlyCount")
        private int monthlyCount;

        // 分类图标
        @SerializedName("icon")
        private String icon;

        // 封面图链接
        @SerializedName("bookCover")
        private String[] bookCover;

        public String getName() {
            return name;
        }

        public int getBookCount() {
            return bookCount;
        }

        public int getMonthlyCount() {
            return monthlyCount;
        }

        public String getIcon() {
            return icon;
        }

        public String[] getBookCover() {
            return bookCover;
        }
    }

}
