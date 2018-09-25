package tirzad.starunique.wordguess.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tirzad.starunique.wordguess.data.WordContract.WordEntry;
/**
 * Created by StarUnique on 13/09/2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "notebook.db";
    public static final int DATA_BASE_VERSION = 1;
//    CREATE TABLE words(_ID INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, sentence TEXT NOT NULL,
// date TEXT NOT NULL DEFAULT 13/09/2017

    public static final String SQL_CREATE_WORDS_TABLE =
            "CREATE TABLE " +
                    WordEntry.TABLE_NAME + "(" +
                    WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WordEntry.COLUMN_WORD + " TEXT NOT NULL," +
                    WordEntry.COLUMN_SENTENCE + " TEXT NOT NULL," +
                    WordEntry.COLUMN_DATE + " TEXT NOT NULL DEFAULT " + "today" + "," +
                    WordEntry.COLUMN_READABLE + " TEXT NOT NULL DEFAULT " + WordEntry.READABLE_DEFAULT +
            ");";

    public static final String SQL_DELETE_WORDS_TABLE =
            "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;

    public WordDbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_WORDS_TABLE);}

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_WORDS_TABLE);
        onCreate(db);
    }
}
