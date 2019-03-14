package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

public class FuzzySearchResultObj {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private String[] data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String[] getData() {
        return data;
    }
}
