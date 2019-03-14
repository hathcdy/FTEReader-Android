package fte.finalproject.obj;

import com.google.gson.annotations.SerializedName;

// 获取章节内容返回结果
public class ChapterObj {
    // 请求结果
    @SerializedName("ok")
    private boolean ok;

    @SerializedName("chapter")
    private Chapter ichapter;

    public boolean isOk() {
        return ok;
    }

    public Chapter getIchapter() {
        return ichapter;
    }

    /*
     * 章节详情
     */
    public class Chapter {
        // 章节标题
        @SerializedName("title")
        private String title;

        // 章节内容
        @SerializedName("body")
        private String body;


        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }
}
