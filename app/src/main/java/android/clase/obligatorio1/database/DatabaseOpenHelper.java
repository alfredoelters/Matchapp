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

    public static final String CRESTS_TABLE = "crests";
    public static final String COLUMN_CREST_LINK = "crest_link";
    public static final String COLUMN_CREST_IMAGE = "crest_image";

    public static final String[] CRESTS_TABLE_COLUMNS = {COLUMN_CREST_LINK, COLUMN_CREST_IMAGE};

    private static final String DATABASE_CREATE = "create table " + CRESTS_TABLE + "("
            + COLUMN_CREST_LINK + " text primary key , "
            + COLUMN_CREST_IMAGE + " blob not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists " + CRESTS_TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
