package fte.finalproject.obj;

import android.graphics.Bitmap;

public class ShelfBookObj {
    String bookId;                  //id
    private Bitmap icon;            //图标
    private String iconURL;         //图标网络url
    private String name;            //书名
    private String description;     //描述
    private String author;          //作者
    private String major;           //一级分类
    int type;                       //0代表网络图片，1代表本地图片
    private String address;         //本地书籍url
    private int readChapter;        //阅读到的章节

    public ShelfBookObj(String bookId, String name, Bitmap icon, String iconURL, int readChapter, String address,int type,String description,String author, String major) {
        this.bookId = bookId;
        this.name = name;
        this.icon = icon;
        this.iconURL = iconURL;
        this.readChapter = readChapter;
        this.address = address;
        this.type = type;
        this.description = description;
        this.author = author;
        this.major = major;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getAddress() {
        return address;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public int getReadChapter() {
        return readChapter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setReadChapter(int readChapter) {
        this.readChapter = readChapter;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
