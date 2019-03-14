package fte.finalproject.obj;

import java.io.Serializable;
import java.util.List;

public class ChapterLinks implements Serializable {
    public List<ChapterLinkObj> chapterLinkList;

    public List<ChapterLinkObj> getChapterLinkList() {
        return chapterLinkList;
    }
}
