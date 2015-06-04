package android.clase.obligatorio1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by alfredo on 30/05/15.
 */
public class MatchesDAO {
    private DatabaseOpenHelper dbHelper;
    private SQLiteDatabase database;

    /**
     * Gets a read/write SQLiteDatabase instance
     */
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the SQLiteHelper instance to release resources
     */
    public void close(){
        dbHelper.close();
    }

    /**
     * Constructor
     * @param context
     */
    public MatchesDAO(Context context){
        dbHelper = new DatabaseOpenHelper(context);
        open();
    }

    public void insertTodaysMatches( String link, String matchesJson) throws SQLiteException {
        ContentValues cv = new  ContentValues();
        cv.put(DatabaseOpenHelper.COLUMN_MATCHES_LINK,    link);
        cv.put(DatabaseOpenHelper.COLUMN_MATCHES_JSON, matchesJson);
        database.insert(DatabaseOpenHelper.MATCHES_TABLE, null, cv);
    }

    public String getMatches(String link){
        String selection = DatabaseOpenHelper.COLUMN_MATCHES_LINK + " = ?";
        String[] selectionArgs = {link};
        Cursor cursor = database.query(DatabaseOpenHelper.MATCHES_TABLE,
                DatabaseOpenHelper.MATCHES_TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        String result = null;
        if(cursor.moveToFirst())
            result = cursor.getString(1);
        cursor.close();
        return result;
    }
}
