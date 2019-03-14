package fte.finalproject.obj;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// 获取章节列表返回结果
public class CptListObj {
    // 请求结果
    @SerializedName("ok")
    private boolean ok;

    @SerializedName("mixToc")
    private mixToc imixToc;

    public boolean isOk() {
        return ok;
    }

    public mixToc getImixToc() {
        return imixToc;
    }

    /*
     * 章节列表
     */
    public class mixToc {
        @SerializedName("_id")
        private String _id;
        // 书籍id
        @SerializedName("book")
        private String book;
        // 章节数
        @SerializedName("chaptersCount1")
        private int chaptersCount;
        // 章节列表
        @SerializedName("chapters")
        public List<ChapterLinkObj> chapterLinks;
        // 更新时间
        @SerializedName("updated")
        private String updated;

        public String get_id() {
            return _id;
        }

        public String getBook() {
            return book;
        }

        public int getChaptersCount() {
            return chaptersCount;
        }

        public List<ChapterLinkObj> getChapterLinks() {
            return chapterLinks;
        }

        public String getUpdated() {
            return updated;
        }

    }
}
