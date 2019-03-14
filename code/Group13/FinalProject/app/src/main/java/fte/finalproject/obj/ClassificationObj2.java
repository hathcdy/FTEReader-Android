package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClassificationObj2 {
    @SerializedName("male")
    private List<SubClass2> maleList;

    @SerializedName("female")
    private List<SubClass2> femaleList;

    @SerializedName("picture")
    private List<SubClass2> picList;

    @SerializedName("press")
    private List<SubClass2> pressList;

    @SerializedName("ok")
    private boolean ok;

    public List<SubClass2> getMaleList() {
        return maleList;
    }

    public List<SubClass2> getFemaleList() {
        return femaleList;
    }

    public List<SubClass2> getPicList() {
        return picList;
    }

    public List<SubClass2> getPressList() {
        return pressList;
    }

    public boolean isOk() {
        return ok;
    }

    public class SubClass2 {
        @SerializedName("major")
        private String major;

        @SerializedName("mins")
        private String[] mins;

        public String getMajor() {
            return major;
        }

        public String[] getMins() {
            return mins;
        }
    }
}
