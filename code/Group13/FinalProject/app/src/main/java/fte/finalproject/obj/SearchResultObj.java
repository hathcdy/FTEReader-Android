package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultObj {
    // 书籍列表
    @SerializedName("books")
    private List<book> bookList;

    // 总数
    @SerializedName("total")
    private int total;

    @SerializedName("ok")
    private boolean ok;

    public List<book> getBookList() {
        return bookList;
    }

    public int getTotal() {
        return total;
    }

    public boolean isOk() {
        return ok;
    }

    public class book {
        // 书籍id
        @SerializedName("_id")
        private String _id;

        @SerializedName("hasCp")
        private boolean hasCp;

        // 书名
        @SerializedName("title")
        private String title;

        @SerializedName("aliases")
        private String aliases;

        // 一级分类
        @SerializedName("cat")
        private String cat;

        // 作者
        @SerializedName("author")
        private String author;

        @SerializedName("site")
        private String site;

        // 封面
        @SerializedName("cover")
        private String cover;

        // 简介
        @SerializedName("shortIntro")
        private String shortIntro;

        // 最新章节
        @SerializedName("lastChapter")
        private String lastChapter;

        // 留存率
        @SerializedName("retentionRatio")
        private float retentionRatio;

        @SerializedName("banned")
        private int banned;

        @SerializedName("allowMonthly")
        private boolean allowMonthly;

        // 关注人数
        @SerializedName("latelyFollower")
        private int latelyFollower;

        // 字数
        @SerializedName("wordCount")
        private int wordCount;

        // 类型
        @SerializedName("contentType")
        private String contentType;

        @SerializedName("superscript")
        private String superscript;

        @SerializedName("sizetype")
        private int sizetype;

        @SerializedName("highlight")
        private highlight mhighlight;

        public String get_id() {
            return _id;
        }

        public boolean isHasCp() {
            return hasCp;
        }

        public String getTitle() {
            return title;
        }

        public String getAliases() {
            return aliases;
        }

        public String getCat() {
            return cat;
        }

        public String getAuthor() {
            return author;
        }

        public String getSite() {
            return site;
        }

        public String getCover() {
            return cover;
        }

        public String getShortIntro() {
            return shortIntro;
        }

        public String getLastChapter() {
            return lastChapter;
        }

        public float getRetentionRatio() {
            return retentionRatio;
        }

        public int getBanned() {
            return banned;
        }

        public boolean isAllowMonthly() {
            return allowMonthly;
        }

        public int getLatelyFollower() {
            return latelyFollower;
        }

        public int getWordCount() {
            return wordCount;
        }

        public String getContentType() {
            return contentType;
        }

        public String getSuperscript() {
            return superscript;
        }

        public int getSizetype() {
            return sizetype;
        }

        public highlight getMhighlight() {
            return mhighlight;
        }

        public class highlight {
            @SerializedName("title")
            private String[] title;

            public String[] getTitle() {
                return title;
            }
        }
    }
}
