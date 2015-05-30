package android.clase.obligatorio1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by alfredo on 30/05/15.
 */
public class TeamCrestsDAO {
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
    public TeamCrestsDAO(Context context){
        dbHelper = new DatabaseOpenHelper(context);
        open();
    }

    public void insertCrest( String link, byte[] image) throws SQLiteException {
        ContentValues cv = new  ContentValues();
        cv.put(DatabaseOpenHelper.COLUMN_CREST_LINK,    link);
        cv.put(DatabaseOpenHelper.COLUMN_CREST_IMAGE,   image);
        database.insert(DatabaseOpenHelper.CRESTS_TABLE, null, cv);
    }

    public byte[] getCrest(String link){
        String selection = DatabaseOpenHelper.COLUMN_CREST_LINK + " = ?";
        String[] selectionArgs = {link};
        Cursor cursor = database.query(DatabaseOpenHelper.CRESTS_TABLE,
                DatabaseOpenHelper.CRESTS_TABLE_COLUMNS, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        byte[] result = cursor.getBlob(1);
        cursor.close();
        return result;
    }
}
