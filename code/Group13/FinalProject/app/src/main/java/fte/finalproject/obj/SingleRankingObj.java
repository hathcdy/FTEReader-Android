package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleRankingObj {
    @SerializedName("ranking")
    private Ranking ranking;

    @SerializedName("ok")
    private boolean ok;

    public Ranking getRanking() {
        return ranking;
    }

    public boolean isOk() {
        return ok;
    }

    public class Ranking {
        //  周榜id
        @SerializedName("_id")
        private String _id;

        // 更新时间
        @SerializedName("updated")
        private String updated;

        // 排行榜全名
        @SerializedName("title")
        private String title;

        @SerializedName("tag")
        private String tag;

        // 排行榜大图标
        @SerializedName("cover")
        private String cover;

        // 排行榜小图标
        @SerializedName("icon")
        private String icon;

        @SerializedName("__v")
        private String __v;

        // 月榜id
        @SerializedName("monthRank")
        private String monthRank;

        // 总榜id
        @SerializedName("totalRank")
        private String totalRank;

        // 排行榜简称
        @SerializedName("shortTitle")
        private String shortTitle;

        @SerializedName("created")
        private String created;

        @SerializedName("biTag")
        private String biTag;

        @SerializedName("isSub")
        private boolean isSub;

        @SerializedName("collapse")
        private boolean collapse;

        @SerializedName("new")
        private boolean _new;

        // 性别
        @SerializedName("gender")
        private String gender;

        @SerializedName("priority")
        private int priority;

        // 书籍列表
        @SerializedName("books")
        private List<BookObj> bookList;

        // 周榜id
        @SerializedName("id")
        private String id;

        // 总数
        @SerializedName("total")
        private int total;

        public String get_id() {
            return _id;
        }

        public String getUpdated() {
            return updated;
        }

        public String getTitle() {
            return title;
        }

        public String getTag() {
            return tag;
        }

        public String getCover() {
            return cover;
        }

        public String getIcon() {
            return icon;
        }

        public String get__v() {
            return __v;
        }

        public String getMonthRank() {
            return monthRank;
        }

        public String getTotalRank() {
            return totalRank;
        }

        public String getShortTitle() {
            return shortTitle;
        }

        public String getCreated() {
            return created;
        }

        public String getBiTag() {
            return biTag;
        }

        public boolean isSub() {
            return isSub;
        }

        public boolean isCollapse() {
            return collapse;
        }

        public boolean is_new() {
            return _new;
        }

        public String getGender() {
            return gender;
        }

        public int getPriority() {
            return priority;
        }

        public List<BookObj> getBookList() {
            return bookList;
        }

        public String getId() {
            return id;
        }

        public int getTotal() {
            return total;
        }
    }
}
