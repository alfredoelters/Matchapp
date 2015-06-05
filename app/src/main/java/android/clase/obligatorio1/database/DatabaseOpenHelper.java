package android.clase.obligatorio1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alfredo on 30/05/15.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "matchapp.db";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    public static final String MATCHES_TABLE = "matches";
    public static final String COLUMN_MATCHES_LINK = "matches_link";
    public static final String COLUMN_MATCHES_JSON = "matches_json";

    public static final String[] MATCHES_TABLE_COLUMNS = {COLUMN_MATCHES_LINK, COLUMN_MATCHES_JSON};


    private static final String CREATE_MATCHES_TABLE = "create table " + MATCHES_TABLE + "("
            + COLUMN_MATCHES_LINK + " text primary key ,"
            + COLUMN_MATCHES_JSON + " text not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MATCHES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + MATCHES_TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
