package tirzad.starunique.wordguess.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
/**
 * Created by StarUnique on 16/09/2017.
 */

public class WordProvider extends ContentProvider {

    public static final int WORDS = 200;
    public static final int WORDS_ID = 201;

    public static final String LOG_TAG = WordProvider.class.getSimpleName();
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY, WordContract.PATH_WORDS, WORDS);
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY, WordContract.PATH_WORDS + "/#", WORDS_ID);
    }

    private WordDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new WordDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                cursor = database.query(WordContract.WordEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case WORDS_ID:
                selection = WordContract.WordEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WordContract.WordEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        String word = values.getAsString(WordContract.WordEntry.COLUMN_WORD);
        if (TextUtils.isEmpty(word))
            throw new IllegalArgumentException("Word cannot be empty");
        String sentence = values.getAsString(WordContract.WordEntry.COLUMN_SENTENCE);
        if (TextUtils.isEmpty(sentence))
            throw new IllegalArgumentException("Sentence cannot be empty");

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(WordContract.WordEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                deleteCount = database.delete(WordContract.WordEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WORDS_ID:
                selection = WordContract.WordEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleteCount = database.delete(WordContract.WordEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                getContext().getContentResolver().notifyChange(uri, null);
                return updateWord(uri,contentValues, selection, selectionArgs);
            case WORDS_ID:
                selection = WordContract.WordEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updateWord(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateWord(Uri uri, ContentValues values,
                           String selection, String[] selectionArgs) {
        if (values.size() == 0)
            return 0;

        if (values.containsKey(WordContract.WordEntry.COLUMN_WORD)) {
            String word = values.getAsString(WordContract.WordEntry.COLUMN_WORD);
            if (TextUtils.isEmpty(word))
                throw new IllegalArgumentException("Word is needed.");
        }

        if (values.containsKey(WordContract.WordEntry.COLUMN_SENTENCE)) {
            String sentence = values.getAsString(WordContract.WordEntry.COLUMN_SENTENCE);
            if (TextUtils.isEmpty(sentence))
                throw new IllegalArgumentException("Sentence is needed.");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(WordContract.WordEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
