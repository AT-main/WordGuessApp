package tirzad.starunique.wordguess.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by StarUnique on 13/09/2017.
 */

public class WordContract {

    public WordContract() {
    }
    public static final String CONTENT_AUTHORITY = "tirzad.starunique.wordguess";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORDS = "words";

    public static final class WordEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);

        public static final String TABLE_NAME = "words";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_SENTENCE = "sentence";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_READABLE = "readable";

        public static final String DATE_DEFAULT = "13/09/2017";

        public static final String READABLE_DEFAULT = "true";


    }
}
