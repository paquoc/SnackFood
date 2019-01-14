package com.miniproject16cntn.a1612543.snackfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DBRestaurant extends SQLiteOpenHelper {

    public static final String DB_NAME ="restaurant_list";
    private static final String TABLE_NAME ="restaurant";
    private static final String ID ="id";
    private static final String NAME = "name";
    private static final String LIST ="listeating";
    private static final String PRICE ="price";
    private static final String ADDRESS ="address";
    private static final String LAT ="lat";
    private static final String LNG ="lng";
    private static final String STARTTIME = "starttime";
    private static final String ENDTIME = "endtime";
    private static final String IMAGE = "image";
    private static final String DESCRIPTION = "description";
    private static final String UNIT = "unit";
    private static final String FAVORITE = "favorite";

    public static String DB_PATH;

    private final int cID = 0;
    private final int cNAME = 1;
    private final int cLIST = 2;
    private final int cPRICE = 3;
    private final int cADDRESS = 4;
    private final int cLAT = 5;
    private final int cLNG = 6;
    private final int cSTARTTIME = 7;
    private final int cENDTIME = 8;
    private final int cIMAGE = 9;
    private final int cDECRIPTION = 10;
    private final int cUNIT = 11;
    private final int cFAVORITE = 12;

    private Context context;

    public DBRestaurant(Context context){
        super(context, DB_NAME, null, 1);
        this.context = context;
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }

    public void create() throws IOException {
        if (databaseIsExist()) {
            this.getReadableDatabase();
            this.close();
            copyDataBaseFromAsset();
        } else {
            InitDB();
        }
    }

    private void copyDataBaseFromAsset() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0){
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    private boolean databaseIsExist() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID +" integer primary key, "+
                NAME + " TEXT," +
                LIST + " TEXT, "+
                PRICE +" TEXT, "+
                ADDRESS +" TEXT," +
                LAT +" REAL," +
                LNG + " REAL," +
                STARTTIME + " TEXT," +
                ENDTIME + " TEXT," +
                IMAGE + " BLOB,"+
                DESCRIPTION + " TEXT," +
                UNIT + " TEXT," +
                FAVORITE + " NUMERIC)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create Database successfylly", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    public void addRestaurant(Restaurant restaurant)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);

        values.put(ID, count + 1);
        values.put(NAME, restaurant.getName());
        values.put(LIST, restaurant.getEating());
        values.put(PRICE, restaurant.getPrice());
        values.put(ADDRESS, restaurant.getAddress());
        values.put(LAT, restaurant.getLatLng().latitude);
        values.put(LNG, restaurant.getLatLng().longitude);
        values.put(STARTTIME, restaurant.getStartTime());
        values.put(ENDTIME, restaurant.getEndTime());
        values.put(IMAGE, restaurant.getImage());
        values.put(DESCRIPTION,restaurant.getDescription());
        values.put(UNIT, restaurant.getUnit());
        values.put(FAVORITE, restaurant.getFavorite());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public Restaurant getRestaurantById(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, LIST,PRICE,ADDRESS,LAT,LNG, STARTTIME, ENDTIME, IMAGE, DESCRIPTION, UNIT, FAVORITE}, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Restaurant restaurant = GetRestaurantFromCursor(cursor);
        cursor.close();
        db.close();
        return restaurant;
    }

    @NonNull
    private Restaurant GetRestaurantFromCursor(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(cursor.getInt(cID));
        restaurant.setName(cursor.getString(cNAME));
        restaurant.setListEating(cursor.getString(cLIST));
        restaurant.setPrice(cursor.getString(cPRICE));
        restaurant.setAddress(cursor.getString(cADDRESS));
        restaurant.setLatLng(getLatLng(cursor));
        restaurant.setStartTime(cursor.getString(cSTARTTIME));
        restaurant.setEndTime(cursor.getString(cENDTIME));
        restaurant.setImage(cursor.getBlob(cIMAGE));
        restaurant.setDescription(cursor.getString(cDECRIPTION));
        restaurant.setUnit(cursor.getString(cUNIT));
        restaurant.setFavorite(cursor.getInt(cFAVORITE));

        return restaurant;
    }

    public List<Restaurant> getAllRestaurant(){
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                    Restaurant restaurant = GetRestaurantFromCursor(cursor);
                    restaurants.add(restaurant);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }

    public List<Restaurant> getRestaurantNearby(LatLng center, float radius){
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                LatLng locationRestaurant = getLatLng(cursor);
                float distance = Distance(center, locationRestaurant);
                if (distance <= radius)
                {
                    Restaurant restaurant = GetRestaurantFromCursor(cursor);
                    restaurants.add(restaurant);
                }

            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }

    public List<Restaurant> getRestaurant(String food)
    {
        String[] keys = food.split(" ");

        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                if (isMatch(keys, cursor.getString(cNAME)) ||
                        isMatch(keys, cursor.getString(cLIST)))
                {
                    Restaurant restaurant = GetRestaurantFromCursor(cursor);
                    restaurants.add(restaurant);
                }

            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }

    private boolean isMatch(String[] keys, String string) {
        int n = keys.length;
        string = string.toLowerCase();
        for (int i = 0; i < n; i++)
            if (string.contains(keys[i].toLowerCase()))
                return true;
        return false;
    }

    @NonNull
    private LatLng getLatLng(Cursor cursor) {
        return new LatLng(cursor.getDouble(cLAT),
                cursor.getDouble(cLNG));
    }

    private float Distance(LatLng center, LatLng locationRestaurant) {
        Location A = new Location("A");
        Location B = new Location("B");
        A.setLatitude(center.latitude);
        A.setLongitude(center.longitude);
        B.setLatitude(locationRestaurant.latitude);
        B.setLongitude(locationRestaurant.longitude);
        return A.distanceTo(B);
    }

    private void InitDB() {
        //region Initialize Value

        List<Integer> listId = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));
        List<String> listName = new ArrayList<String>(Arrays.asList(
                "Xe trái cây chú Ba","Sạp trái cây cô Tư",
                "Bánh xèo", "Bánh xèo",
                "Trà sữa Dragon"));

        String[] e1 = {"Xoài","Cóc","Ổi","Mận","Mít"};
        int[] p1 = {10000,13000,17000,21000,9000};
        String[] u1 = {"kg","kg","kg","kg","kg"};

        String[] e2 = {"Chuối","Bưởi","Táo"};
        int[] p2 = {24000,13000,2000};
        String[] u2 = {"kg","kg","kg"};

        String[] e3 = {"Bánh xèo","Bánh khọt"};
        int[] p3 = {4000,5000};
        String[] u3 = {"Cái", "Cái"};

        String[] e4 = {"Bánh xèo"};
        int[] p4 = {7000};
        String[] u4 = {"Cái"};

        String[] e5 = {"Trà sữa Đào","Hồng trà","Lục trà socolo","",""};
        int[] p5 = {25000,34000,14000};
        String[] u5 = {"Ly","Ly","Ly"};

        List<String[]> listArrayEating = new ArrayList<String[]>(Arrays.asList(e1,e2,e3,e4,e5));
        List<int[]> listArrayPrice = new ArrayList<int[]>(Arrays.asList(p1,p2,p3,p4,p5));
        List<String[]> listUnit = new ArrayList<String[]>(Arrays.asList(u1,u2,u3,u4,u5));

        List<LatLng> listLatlng = new ArrayList<LatLng>(Arrays.asList(
                new LatLng(10.876870, 106.805059), new LatLng(10.876857, 106.804904),
                new LatLng(10.877317, 106.804773), new LatLng(10.877536, 106.804698),
                new LatLng(10.877257, 106.803791)));

        List<String> listAddress = new ArrayList<String>(Arrays.asList(
                "Đường số 1, Khu đô thị Đại học Quốc gia",
                "Đường trục chính trung tâm",
                "Khu vực làng Đại học, Thủ Đức",
                "ĐƯờng không tên",
                "Đường không tê"));
        List<String> listStartTime = new ArrayList<String>(Arrays.asList("13:00", "08:00", "18:00", "18:30", "09:00"));
        List<String> listEndTime = new ArrayList<String>(Arrays.asList("17:00", "19:30", "20:30", "21:00", "21:00"));
        List<String> listDescription = new ArrayList<String>(Arrays.asList(
                "Xe trái cây của chú Ba bán đã hơn 5 năm, chuyên bán các loại trái cây giá rẻ chất lượng cao phục vụ các bạn sinh viên",
                "Sạp trái cây của cô Tư chuyên bán các loại trái cây quý hiếm, mắc tiền, hướng đến đối tượng các phụ huynh có điều kiện",
                "Quán bánh xèo quen thuộc với những bạn sinh viên Bách Khoa hay đi học về khuya, ghé qua đây gọi 1 dĩa 4 bánh là hết sẩy",
                "Quán bánh xèo còn được gọi là Bánh xèo cổng chui vì nằm ngay khu vực ngày xưa là cổng chui",
                "Quán trà sữa Dragon là thương hiệu vỉa hè lâu đời được truyền từ đời này sang đời khác và hiện đang phát triển mạnh mẽ với rất nhiều đồ uống ngon mà giá cả rất sinh viên"));
        List<byte[]> listImage = new ArrayList<byte[]>(Arrays.asList(
                getBytes(R.drawable.diet),
                getBytes(R.drawable.bread),
                getBytes(R.drawable.icecream),
                getBytes(R.drawable.pizza),
                getBytes(R.drawable.chickenleg)
        ));

        List<Integer> listFavorite = new ArrayList<Integer>(Arrays.asList(0,0,1,0,1));
//endregion

        for (int i = 0; i < listId.size(); i++) {
            Restaurant restaurant =new Restaurant(
                    listId.get(i),
                    listName.get(i),
                    listArrayEating.get(i),
                    listArrayPrice.get(i),
                    listAddress.get(i),
                    listLatlng.get(i),
                    listStartTime.get(i),
                    listEndTime.get(i),
                    listImage.get(i),
                    listDescription.get(i),
                    listUnit.get(i),
                    listFavorite.get(i));
            this.addRestaurant(restaurant);
        }
    }

    private byte[] getBytes(int t) {
        byte[] image;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), t);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        image = outputStream.toByteArray();
        return image;
    }


    public void updateFavorite(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FAVORITE,restaurant.getFavorite());

        db.update(TABLE_NAME,
                values,ID +"=?",new String[] { String.valueOf(restaurant.getId())});

    }

    public List<Restaurant> getFavorite() {
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                if (cursor.getInt(cFAVORITE) == 1)
                {
                    Restaurant restaurant = GetRestaurantFromCursor(cursor);
                    restaurants.add(restaurant);
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return restaurants;
    }
}
