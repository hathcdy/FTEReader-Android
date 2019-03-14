package fte.finalproject.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

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
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;


public class BookService {
    public static String ApiUrl = "http://api.zhuishushenqi.com";
    public static String StaticsUrl = "http://statics.zhuishushenqi.com";
    public static String ChapterUrl = "http://chapter2.zhuishushenqi.com";
    public static String FuzzySearchUrl = "https://www.apiopen.top";

    private static BookService bookService = new BookService();

    public static synchronized BookService getBookService() {
        return bookService;
    }

    OkHttpClient build = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build();

    // 用于API访问
    Retrofit retrofitForApi = new Retrofit.Builder()
            .baseUrl(ApiUrl)
            // 设置json数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            // RxJava封装OkHttp的Call函数，本质还是利用OkHttp请求数据
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(build)
            .build();

    // 用于图片访问
    Retrofit retrofitForStatics = new Retrofit.Builder()
            .baseUrl(StaticsUrl)
            // 设置json数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            // RxJava封装OkHttp的Call函数，本质还是利用OkHttp请求数据
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(build)
            .build();

    // 用于章节访问
    Retrofit retrofitForChapter = new Retrofit.Builder()
            .baseUrl(ChapterUrl)
            // 设置json数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            // RxJava封装OkHttp的Call函数，本质还是利用OkHttp请求数据
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(build)
            .build();

    Retrofit retrofitForFuzzySearch = new Retrofit.Builder()
            .baseUrl(FuzzySearchUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(build)
            .build();

    // 用于访问api
    private UrlService ApiService = retrofitForApi.create(UrlService.class);

    // 用于访问图片
    private UrlService StaticsService = retrofitForStatics.create(UrlService.class);

    // 用于访问章节
    private UrlService ChapterService = retrofitForChapter.create(UrlService.class);

    // 用于模糊搜索
    private UrlService FuzzySearchService = retrofitForFuzzySearch.create(UrlService.class);

    // 所有排行榜
    private AllRankingObj allRankingObj;

    // 单一排行榜
    private SingleRankingObj singleRankingObj;

    // 一级分类
    private ClassificationObj1 classificationObj1;

    // 二级分类
    private ClassificationObj2 classificationObj2;

    // 书籍列表
    private CategoryObj categoryObj;

    // 书籍详情
    private BookObj bookObj;

    // 章节列表
    private CptListObj cptListObj;

    // 章节内容
    private ChapterObj chapterObj;

    // 书籍搜索结果
    private SearchResultObj searchResultObj;

    // 模糊搜索结果
    private FuzzySearchResultObj fuzzySearchResultObj;

    /*
     * 获取所有排行榜
     * @param 无
     * @return AllRankingObj
     */

    public AllRankingObj getAllRankingObj() {
        Response<AllRankingObj> response = null;
        try {
            response = ApiService.getAllRanking().execute();
            allRankingObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allRankingObj;
    }

    /*
     * 获取单一排行榜
     * @param rankingId String _id 周榜、monthRank 月榜、totalRank 总榜 可从AllRankingObj中获得
     * @return SingleRankingObj
     */

    public SingleRankingObj getSingleRankingObj(String rankingId) {
        Response<SingleRankingObj> response = null;
        try {
            response = ApiService.getSingleRanking(rankingId).execute();
            singleRankingObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return singleRankingObj;
    }

    /*
     * 获取一级分类
     * @param 无
     * @return ClassificationObj1
     */

    public ClassificationObj1 getClassification1() {
        Response<ClassificationObj1> response = null;
        try {
            response = ApiService.getClassificationObj1().execute();
            classificationObj1 = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classificationObj1;
    }

    /*
     * 获取二级分类
     * @param 无
     * @return ClassificationObj2
     */

    public ClassificationObj2 getClassification2() {
        Response<ClassificationObj2> response = null;
        try {
            response = ApiService.getClassificationObj2().execute();
            classificationObj2 = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classificationObj2;
    }

    /*
     * 获取主题书单列表
     * @param type String hot(热门)、new(新书)、reputation(好评)、over(完结)
     *        major String 玄幻 可以从一级分类获得
     *        start String 起始位置，从0开始
     *        limit String 获取数量限制 20
     *        gender String 性别 male、female
     * @return CategoryObj
     * 示例 bookService.getBooksByCategoty("hot", "玄幻", "0", "20", "male");
     */

    public CategoryObj getBooksByCategoty(String type, String major, int start, int limit, String gender) {
        Response<CategoryObj> response = null;
        try {
            response = ApiService.getBooksByCategory(type, major, start, limit, gender).execute();
            categoryObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categoryObj;
    }

    public BookObj getBookById(String bookid) {
        Response<BookObj> response = null;
        try {
            response = ApiService.getBookById(bookid).execute();
            bookObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookObj;
    }

    /*
     * 获取章节列表
     * @param String bookid 书籍id，可从CategoryObj中获得
     * @return CptListObj 章节列表对象
     */

    public CptListObj getChaptersByBookId(String bookid) {
        Response<CptListObj> response = null;
        try {
            response = ApiService.getChapters(bookid).execute();
            cptListObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cptListObj;
    }

    /*
     * 获取章节内容
     * @param String link 章节链接，可从CptListObj中获得
     * @return ChapterObj 章节对象
     */

    public ChapterObj getChapterByLink(String link) {
        Response<ChapterObj> response = null;
        try {
            response = ChapterService.getChapter(link).execute();
            chapterObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapterObj;
    }

    /*
     * 获取书籍搜索结果
     * @param query String 关键词
     *        start 结果开始位置
     *        limit 结果最大数量
     * @return SearchResultObj 搜索结果对象
     */
    public SearchResultObj getSearchResultObj(String query, int start, int limit) {
        Response<SearchResultObj> response = null;
        try {
            response = ApiService.getSearchResult(query, start, limit).execute();
            searchResultObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResultObj;
    }

    /*
     * 获取书籍模糊搜索结果
     * @param name String 关键词
     * @return FuzzyResultObjs 模糊搜索结果对象
     */
    public FuzzySearchResultObj getFuzzySearchResult(String name) {
        Response<FuzzySearchResultObj> response = null;
        try {
            response = FuzzySearchService.getFuzzySearchResult(name).execute();
            fuzzySearchResultObj = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fuzzySearchResultObj;
    }

    /*
     * 获取图片
     * @param path
     * @return Bitmap 图片
     */
    public Bitmap getImg(String path) {
        Response<InputStream> response = null;
        Bitmap bitmap = null;
        try {
            response = StaticsService.getImg(path).execute();
            InputStream is = response.body();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}