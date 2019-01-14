package com.miniproject16cntn.a1612543.snackfood.CommentAndRate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.os.Build.ID;

public class DBReview extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "review";
    private static final String DB_NAME = "review_list";
    private static String DB_PATH;
    private static final String USER = "idUser";
    private static final String RESTAURANT = "idRestaurant";
    private static final String TIMESTAMP = "timeStamp";
    private static final String SCORE = "score";
    private static final String COMMENT = "comment";

    private final int cUSER = 0;
    private final int cRESTAURANT = 1;
    private final int cTIME = 2;
    private final int cSCORE = 3;
    private final int cCOMMENT = 4;

    private Context context;

    public DBReview(Context context){
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
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                USER +" TEXT, "+
                RESTAURANT + " integer," +
                TIMESTAMP + " TEXT,"+
                SCORE +" integer, "+
                COMMENT +" TEXT," +
                "PRIMARY KEY (" + USER + "," + RESTAURANT + "," + TIMESTAMP + "));";

        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create Database successfully", Toast.LENGTH_SHORT).show();
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, "Drop successfully", Toast.LENGTH_SHORT).show();
    }

    public void AddReview(Review review){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER, review.getUser());
        values.put(RESTAURANT, review.getRestaurant());
        values.put(TIMESTAMP, review.getTimestamp());
        values.put(SCORE, review.getScore());
        values.put(COMMENT, review.getComment());

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public Review getReviewById(String user, int restaurant, String timestamp){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { USER,
                        RESTAURANT, TIMESTAMP,SCORE,COMMENT}, ID + "=?",
                new String[] { user, String.valueOf(restaurant), timestamp }, null, null, timestamp, null);
        if (cursor != null)
            cursor.moveToFirst();

        Review review = GetReviewFromCursor(cursor);
        cursor.close();
        db.close();
        return review;
    }

    private Review GetReviewFromCursor(Cursor cursor) {
        Review review = new Review();
        review.setUser(cursor.getString(cUSER));
        review.setRestaurant(cursor.getInt(cRESTAURANT));
        review.setTimestamp(cursor.getString(cTIME));
        review.setScore(cursor.getInt(cSCORE));
        review.setComment(cursor.getString(cCOMMENT));

        return review;
    }

    public void updateComment(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COMMENT,review.getComment());

        db.update(TABLE_NAME,
                values,ID +"=?",new String[] { review.getUser(),
                        String.valueOf(review.getRestaurant()),
                        review.getTimestamp()});
    }
}
