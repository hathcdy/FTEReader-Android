package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChapterLinkObj implements Serializable {
    // 章节链接
    @SerializedName("link")
    private String link;

    // 章节标题
    @SerializedName("title")
    private String title;

    // 是否不可读
    @SerializedName("unreadable")
    private boolean unreadable;

    public ChapterLinkObj() {
        super();
    }

    public ChapterLinkObj(String _link, String _title, boolean _unreadable) {
        link = _link;
        title = _title;
        unreadable = _unreadable;
    }


    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public boolean isUnreadable() {
        return unreadable;
    }
}