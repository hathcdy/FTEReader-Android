package fte.finalproject.control;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import fte.finalproject.MainActivity;
import fte.finalproject.R;
import fte.finalproject.obj.ShelfBookObj;
import fte.finalproject.obj.UserStatusObj;

public class DatabaseControl extends SQLiteOpenHelper {
    private static final String DB_NAME= "readerBase.db";
    private static final String TABLE_NAME1 = "shelfbook_table";
    private static final String TABLE_NAME2 = "categorybook_table";
    private static final String TABLE_NAME3 = "status_table";
    private static final int DB_VERSION = 1;


    private static DatabaseControl instance = null;
    public static DatabaseControl getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseControl(context);
        }
        return instance;
    }

    //byte[] 转 Bitmap
    public Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
    //Bitmap → byte[]
    public byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //这个list用于保存所有书架书籍信息
    private List<ShelfBookObj> allShelfBook = null;
    public List<ShelfBookObj> getAllShelfBook() {
        return getAllShelfBookFromDB();
    }

    //从数据库中查询所有书架书目
    private List<ShelfBookObj> getAllShelfBookFromDB() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME1, null);
        List<ShelfBookObj> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String major = cursor.getString(cursor.getColumnIndex("major"));
            String author = cursor.getString(cursor.getColumnIndex("author"));
            int readChapter = cursor.getInt(cursor.getColumnIndex("progress"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            byte[] imageByte = cursor.getBlob(cursor.getColumnIndex("image"));
            list.add(new ShelfBookObj(id,name, bytesToBitmap(imageByte),"",readChapter,address,type,description,author,major));
        }
        cursor.close();
        db.close();
        return list;
    }
    //根据book id删除数据
    public void deleteShelfBook(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME1, "_id = ?", new String[] { id });
    }

    public void addShelfBook(ShelfBookObj book) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //开始添加第一条数据_id TEXT PRIMARY KEY, name TEXT, type INTEGER ,progress INTEGER, address TEXT,image BLOB, description TEXT
        values.put("name",book.getName());
        values.put("description",book.getDescription());
        values.put("type",book.getType());
        values.put("_id",book.getBookId());
        values.put("author",book.getAuthor());
        values.put("major",book.getMajor());
        values.put("address",book.getAddress());
        values.put("progress",book.getReadChapter());
        values.put("image",bitmapToBytes(book.getIcon()));
        db.insert(TABLE_NAME1,null,values);
        db.close();
    }

    //获取多少条搜索历史
    public int getHistoryCount() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME2, null, null, null, null, null, null);
        return cursor.getCount();
    }

    //更新阅读进度
    public void updateProgress(int progress, String id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("progress", progress);
        db.update(TABLE_NAME1, value, "_id=?", new String[] { id });
        db.close();
    }

    public List<String> getAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME2, null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            list.add(content);
        }
        cursor.close();
        db.close();
        return list;
    }

    //搜索历史添加（不超过10个）
    public void addSearchHistory(String s) {
        SQLiteDatabase db = getWritableDatabase();
        if (getHistoryCount() > 9){
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME2, null);
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("content"));
            db.delete(TABLE_NAME2, "content=?", new String[] {name});
        }
        ContentValues cv = new ContentValues();
        cv.put("content", s);
        db.insert(TABLE_NAME2, null, cv);
        db.close();
    }

    public boolean judgeBookExist(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME1, null, "_id=?", new String[] { id }, null, null, null);
        return cursor.moveToFirst();
    }

    //删除搜索历史
    public void deleteHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME2);
    }

    // 更新用户阅读器习惯的状态
    public void updateStatus(int user_id, int hor_or_ver_screen) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("hor_or_ver_screen", hor_or_ver_screen);
        db.update(TABLE_NAME3, value, "user_id=?", new String[] { Integer.toString(user_id)});
        db.close();
    }

    // 获取用户阅读器习惯的横竖屏
    public int get_Hor_Or_Ver_Screen_Status(int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        String id = Integer.toString(user_id);
        String sql = String.format("SELECT * FROM " + TABLE_NAME3  + " where user_id='%s'" , id);
        Cursor cursor = db.rawQuery(sql, null);
        int res = 1;
        while (cursor.moveToNext()) {
            res = cursor.getInt(cursor.getColumnIndex("hor_or_ver_screen"));
            return res;
        }
        cursor.close();
        db.close();
        return res;
    }

    // 更新用户阅读器习惯的状态
    public void updateStatus(int user_id, UserStatusObj u) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("hor_or_ver_screen", u.getHor_or_ver_screen()); // 1表示竖屏，0表示横屏
        value.put("day_or_night_status", u.getDay_or_night_status()); // 0表示日间，1表示夜间
        value.put("textSize", u.getTextSize()); // 字体大小
        db.update(TABLE_NAME3, value, "user_id=?", new String[] { Integer.toString(user_id)});
        db.close();
    }

    public UserStatusObj get_User_Status_Obj(int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        String id = Integer.toString(user_id);
        String sql = String.format("SELECT * FROM " + TABLE_NAME3  + " where user_id='%s'" , id);
        Cursor cursor = db.rawQuery(sql, null);
        UserStatusObj res = null;
        while (cursor.moveToNext()) {
            int hor_or_ver_screen = cursor.getInt(cursor.getColumnIndex("hor_or_ver_screen"));
            int day_or_night_status = cursor.getInt(cursor.getColumnIndex("day_or_night_status"));
            int textSize = cursor.getInt(cursor.getColumnIndex("textSize"));
            res = new UserStatusObj(user_id, hor_or_ver_screen, day_or_night_status, textSize);
            return res;
        }
        cursor.close();
        db.close();
        return res;
    }




    public DatabaseControl(Context context) {
        super(context, DB_NAME, null, DB_VERSION );
    }

    @Override//创建数据库
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //书架上的书，含书名、类型（本地还是网络）、阅读进度、资源地址、书的封面
        String CREATE_TABLE1 = "CREATE TABLE if not exists "
                + TABLE_NAME1
                + " (_id TEXT PRIMARY KEY, name TEXT, type INTEGER ,progress INTEGER, address TEXT,image BLOB, description TEXT, author TEXT, major TEXT)";
        //搜索历史
        String CREATE_TABLE2 = "CREATE TABLE if not exists "
                + TABLE_NAME2
                + " (_id INTEGER PRIMARY KEY, content TEXT)";
        // 阅读器状态保存
        String CREATE_TABLE3 = "CREATE TABLE if not exists "
                + TABLE_NAME3
                + " (user_id INTEGER PRIMARY KEY, hor_or_ver_screen INTEGER, day_or_night_status INTEGER, textSize INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE1);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
        sqLiteDatabase.execSQL(CREATE_TABLE3);

        Resources res = MainActivity.getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.mipmap.bookcover);
        ContentValues values = new ContentValues();
        ShelfBookObj book = new ShelfBookObj("5816b415b06d1d32157790b1","圣墟",bitmap,"",0,"default",0,"在破败中崛起，在寂灭中复苏。沧海成尘，雷电枯竭，那一缕幽雾又一次临近大地，世间的枷锁被打开了，一个全新的世界就此揭开神秘的一角……","辰东","玄幻");
        //开始添加第一条数据_id TEXT PRIMARY KEY, name TEXT, type INTEGER ,progress INTEGER, address TEXT,image BLOB, description TEXT
        values.put("name",book.getName());
        values.put("description",book.getDescription());
        values.put("type",book.getType());
        values.put("_id",book.getBookId());
        values.put("author",book.getAuthor());
        values.put("major",book.getMajor());
        values.put("address",book.getAddress());
        values.put("progress",book.getReadChapter());
        values.put("image",bitmapToBytes(book.getIcon()));
        sqLiteDatabase.insert(TABLE_NAME1,null,values);
        values.clear();
        bitmap = BitmapFactory.decodeResource(res, R.mipmap.bookcover2);
        book = new ShelfBookObj("59ba0dbb017336e411085a4e","元尊",bitmap,"",0,"default",0,"《斗破苍穹》《武动乾坤》之后全新力作，朝堂太子踏上了荆棘重生之路…","天蚕土豆","玄幻");
        values.put("name",book.getName());
        values.put("description",book.getDescription());
        values.put("type",book.getType());
        values.put("_id",book.getBookId());
        values.put("author",book.getAuthor());
        values.put("major",book.getMajor());
        values.put("address",book.getAddress());
        values.put("progress",book.getReadChapter());
        values.put("image",bitmapToBytes(book.getIcon()));
        sqLiteDatabase.insert(TABLE_NAME1,null,values);

        // 往阅读器状态表格中保存一条初始阅读器状态
        UserStatusObj u = new UserStatusObj(0,1,0,18);
        values.clear();
        values.put("user_id", 0);           // 用户id为0，表示默认状态
        values.put("hor_or_ver_screen", u.getHor_or_ver_screen()); // 1表示竖屏，0表示横屏
        values.put("day_or_night_status", u.getDay_or_night_status()); // 0表示日间，1表示夜间
        values.put("textSize", u.getTextSize()); // 字体大小
        sqLiteDatabase.insert(TABLE_NAME3,null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }
}
