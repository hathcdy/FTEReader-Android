package fte.finalproject.myRecyclerview;

import android.graphics.Bitmap;

public class RankRecyObj {
    private Bitmap image;
    private String name;
    private int color;

    public RankRecyObj(Bitmap image, String name, int color) {
        this.image = image;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }
}
