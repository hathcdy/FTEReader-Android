package fte.finalproject.obj;

import java.io.Serializable;
import java.util.List;

public class RecomListObj implements Serializable {
    private List<BookObj> bookObjs;

    public RecomListObj(List<BookObj> bookObjs) {
        this.bookObjs = bookObjs;
    }

    public List<BookObj> getBookObjs() {
        return bookObjs;
    }
}
