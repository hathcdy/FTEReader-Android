package fte.finalproject.service;

import android.graphics.Bitmap;

import java.io.InputStream;

import fte.finalproject.obj.AllRankingObj;
import fte.finalproject.obj.BookObj;
import fte.finalproject.obj.CategoryObj;
import fte.finalproject.obj.ChapterObj;
import fte.finalproject.obj.ClassificationObj1;
import fte.finalproject.obj.ClassificationObj2;
import fte.finalproject.obj.CptListObj;
import fte.finalproject.obj.FuzzySearchResultObj;
import fte.finalproject.obj.SearchResultObj;
import fte.finalproject.obj.SingleRankingObj;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UrlService {
    /*
     * 获取所有排行榜
     * @param 无
     * @return Call<AllRankingObj>
     */
    @GET("/ranking/gender")
    Call<AllRankingObj> getAllRanking();

    /*
     * 获取单一排行榜
     * @param rankingId String _id 周榜、monthRank 月榜、totalRank 总榜
     * @return Call<SingleRankingObj>
     */
    @GET("/ranking/{rankingId}")
    Call<SingleRankingObj> getSingleRanking(@Path("rankingId") String rankingId);


    /*
     * 获取一级分类
     * @param 无
     * @return Call<ClassificationObj1>
     */
    @GET("/cats/lv2/statistics")
    Call<ClassificationObj1> getClassificationObj1();

    /*
     * 获取二级分类
     * @param 无
     * @return Call<ClassificationObj2>
     */
    @GET("/cats/lv2")
    Call<ClassificationObj2> getClassificationObj2();

    /*
     * 获取主题书单列表
     * @param type String hot(热门)、new(新书)、reputation(好评)、over(完结)
     *        major String 玄幻
     *        start String 起始位置，从0开始
     *        limit String 限制获取数量 20
     *        gender String 性别 male、female
     * @return Call<CategoryObj>
     */
    @GET("/book/by-categories")
    Call<CategoryObj> getBooksByCategory(@Query("type") String type, @Query("major") String major, @Query("start") int start, @Query("limit") int limit,  @Query("gender") String gender);

    /*
     * 获取书籍详情
     * @param bookid String 书籍id
     * @return Call<BookObj> 书籍对象
     */
    @GET("/book/{bookid}")
    Call<BookObj> getBookById(@Path("bookid") String bookid);

    /*
     * 获取章节列表
     * @param bookid 书籍id
     * @return Call<CptListObj>
     */
    @GET("/mix-atoc/{bookid}?view=chapters")
    Call<CptListObj> getChapters(@Path("bookid") String bookid);

    /*
     * 获取章节内容
     * @param link 章节链接
     * @return Call<ChapterObj> 章节对象
     */
    @GET("/chapter/{link}")
    Call<ChapterObj> getChapter(@Path("link") String link);

    /*
     * 获取书籍搜索结果
     * @param name String 书名
     *        start int 结果开始位置
     *        limit int 结果数量限制
     * @return Call<SearchResultObj> 搜索结果对象
     */
    @GET("/book/fuzzy-search")
    Call<SearchResultObj> getSearchResult(@Query("query") String query, @Query("start") int start, @Query("limit") int limit);

    /*
     * 获取模糊搜索结果
     * @param name 关键字
     * @return Call<FuzzySearchResultObj> 模糊搜索结果对象
     */
    @GET("/novelSearchApi")
    Call<FuzzySearchResultObj> getFuzzySearchResult(@Query("name") String name);

    /*
     * 获取图片
     * @param path
     * @return Call<Bitmap> 图片
     */
    @GET("{path}")
    Call<InputStream> getImg(@Path("path") String path);

}
