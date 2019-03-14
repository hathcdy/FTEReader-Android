package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllRankingObj {

    // 男生
    @SerializedName("male")
    private List<subClass> maleList;

    // 女生
    @SerializedName("female")
    private List<subClass> femaleList;

    @SerializedName("ok")
    private boolean ok;

    public List<subClass> getMaleList() {
        return maleList;
    }

    public List<subClass> getFemaleList() {
        return femaleList;
    }

    public boolean isOk() {
        return ok;
    }

    public class subClass {
        // 周榜id
        @SerializedName("_id")
        private String id;

        // 排行榜全名
        @SerializedName("title")
        private String title;

        // 排行榜大图标
        @SerializedName("cover")
        private String cover;

        @SerializedName("collapse")
        private boolean collapse;

        // 月榜id
        @SerializedName("monthRank")
        private String monthRank;

        // 总榜id
        @SerializedName("totalRank")
        private String totalRank;

        // 排行榜简称
        @SerializedName("shortTitle")
        private String shortTitle;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getCover() {
            return cover;
        }

        public boolean isCollapse() {
            return collapse;
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
    }

}
