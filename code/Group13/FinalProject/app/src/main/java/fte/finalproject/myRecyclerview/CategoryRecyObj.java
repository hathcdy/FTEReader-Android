package fte.finalproject.myRecyclerview;

public class CategoryRecyObj {
    private String categoryName;
    private String bookCount;

    public CategoryRecyObj(String categoryName, String bookCount) {
        this.categoryName = categoryName;
        this.bookCount = bookCount;
    }

    public String getBookCount() {
        return bookCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setBookCount(String bookCount) {
        this.bookCount = bookCount;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
